<#import "../layout/base.ftlh" as base>
<@base.layout title="Профиль автора">
    <h2>Профиль автора: ${form.nickname}</h2>
    <h3>Редактирование данных:</h3>
    <form action="${currentContext}/admin/edit-author?id=${form.id}" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">
        <p>
            <label>Никнейм</label><br>
            <input type="text" name="nickname" value="${form.nickname}" required>
        </p>
        <p>
            <label>Об авторе</label><br>
            <textarea name="bio" rows="5" cols="40">${form.bio!}</textarea>
        </p>
        <button type="submit" class="usual-button">Изменить</button>
    </form>
    <p>
    <form action="${currentContext}/admin/edit-author?id=${form.id}" method="post"
          onsubmit="return confirm('Удалить этого автора и все его книги?');">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">
        <input type="hidden" name="delete" value="true">
        <button type="submit" class="btn-danger" onclick="return confirm('Вы хотите удалить этого автора?');">
            Удалить автора
        </button>
    </form></p>

    <hr style="margin: 20px 0;">


    <h3>Книги автора:</h3>
    <#if myBooks?size == 0>
        <p>У этого автора пока нет книг.</p>
    <#else>
        <ul>
            <#list myBooks as book>
                <li>
                    <a href="${currentContext}/admin/edit-book?id=${book.id}">${book.title}</a>
                </li>
            </#list>
        </ul>
    </#if>
    <p><a href="${currentContext}/admin/upload?id=${form.id}" class="usual-button" style="margin-bottom: 15px; display: inline-block;">
        Добавить новую книгу
    </a></p>

    <p style="margin-top: 20px;">
        <a href="${currentContext}/admin/all-authors">Назад к списку авторов</a> |
        <a href="${currentContext}/admin">В админ-панель</a>
    </p>
</@base.layout>