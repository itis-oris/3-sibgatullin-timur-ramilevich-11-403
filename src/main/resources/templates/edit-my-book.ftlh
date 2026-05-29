<#import "layout/base.ftlh" as base>
<@base.layout title="Изменение книги">
    <h2>Изменение книги</h2>
    <form method="post" action="${currentContext}/edit-my-book?id=${form.id}" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">
        <input type="hidden" id="authorNickname" value="${user.nickname}">

        <p>
            <label for="title">Название книги:</label><br>
            <input type="text" id="title" name="title" value="${form.title!}" required>
        </p>
        <p>
            <label for="description">Описание:</label><br>
            <textarea id="description" name="description" rows="5" cols="40">${form.description!}</textarea>
        </p>
        <p>
            <button type="button" class="usual-button" onclick="generateDescription()">
                Сгенерировать описание
            </button>
            <button type="button" class="usual-button" onclick="improveDescription()">
                Улучшить описание
            </button>
        </p>
        <p>
            <label for="genreIds">Жанры:</label><br>
            <select id="genreIds" name="genreIds" multiple required size="5">
                <#list genres as genre>
                    <option value="${genre.id}"
                            <#if form.genreIds?? && form.genreIds?seq_contains(genre.id)>selected</#if>
                    >${genre.name}</option>
                </#list>
            </select>
        </p>
        <p>
            <label for="file">Новый файл книги (оставьте пустым, чтобы не менять):</label><br>
            <input type="file" id="file" name="file"
                   accept=".pdf,.epub,.fb2,.mobi,.docx,.txt">
        </p>
        <button type="submit" class="usual-button">Обновить</button>
    </form>

    <hr style="margin: 20px 0;">

    <form action="${currentContext}/edit-my-book?id=${form.id}" method="post"
          onsubmit="return confirm('Удалить эту книгу?');">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">
        <input type="hidden" name="delete" value="true">
        <button type="submit" class="btn-danger" onclick="return confirm('Вы хотите удалить книгу?');">
            Удалить книгу
        </button>
    </form>

    <script src="${currentContext}/static/js/ai.js"></script>
</@base.layout>