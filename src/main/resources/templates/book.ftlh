<#import "layout/base.ftlh" as base>
<@base.layout title="${book.title}">
    <h2>${book.title}</h2>
    <p><b>Автор:</b> <a href="${currentContext}/author/${book.author.id}">${book.author.nickname}</a></p>
    <p><b>Жанры:</b>
        <#list book.genres as g>
            <a href="${currentContext}/books?genreId=${g.id}">${g.name}</a><#if g_has_next>, </#if>
        </#list>
    </p>
    <p><b>Описание:</b> ${book.description!}</p>

    <#if user??>
        <p>
            <button data-fav-btn class="usual-button"
                    data-book-id="${book.id}"
                    data-fav="${isFavorite!?string('true','false')}">
                ${isFavorite!?string('Удалить из избранного','Добавить в избранное')}
            </button>
        </p>
    </#if>
    <a href="${currentContext}/download?id=${book.id}">Скачать книгу</a>
    <p>
    <h3>Комментарии</h3>
    <ul>
        <#list comments as c>
            <li><b>${c.createdAtFormatted} —
                    <a href="${currentContext}/user?id=${c.user.id}">${c.user.author.nickname}</a>:</b> ${c.text}</li>
        </#list>
    </ul>
    </p>
    <#if user??>
        <form method="post" action="${currentContext}/comment/add">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <input type="hidden" name="bookId" value="${book.id}">
            <textarea name="text" rows="3" cols="40" required></textarea><br>
            <button type="submit" class="usual-button">Оставить комментарий</button>
        </form>
    <#else>
        <p><a href="${currentContext}/auth/login">Войдите</a>, чтобы оставить комментарий.</p>
    </#if>

    <#if similarBooks?? && (similarBooks?size > 0)>
        <p>
            <h3>Вас могут заинтересовать:</h3>
            <ul>
                <#list similarBooks as similar>
                    <li>
                        <a href="${currentContext}/book?id=${similar.id}">
                            <b>${similar.title}</b>
                        </a>
                        <#if similar.author??>
                            — <a href="${currentContext}/author/${similar.author.id}">${similar.author.nickname}</a>
                        </#if>
                        <#if similar.description?? && (similar.description?length > 0)>
                            <br>
                            <small style="color: #666;">
                                <#if similar.description?length gt 150>
                                    ${similar.description[0..150]}...
                                <#else>
                                    ${similar.description}
                                </#if>
                            </small>
                        </#if>
                    </li>
                </#list>
            </ul>
        </p>
    </#if>
</@base.layout>