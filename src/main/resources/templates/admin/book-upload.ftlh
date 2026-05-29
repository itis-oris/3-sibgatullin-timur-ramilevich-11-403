<#import "../layout/base.ftlh" as base>
<@base.layout title="Загрузка книги">
    <h2>Загрузка книги</h2>
    <form method="post" action="${currentContext}/admin/upload" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">
        <input type="hidden" name="authorId" value="${authorId}">
        <input type="hidden" id="authorNickname" value="${authorNickname}">

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
            <label for="genreIds">Жанры (удерживайте Ctrl для выбора нескольких):</label><br>
            <select id="genreIds" name="genreIds" multiple required size="5">
                <#list genres as genre>
                    <option value="${genre.id}">${genre.name}</option>
                </#list>
            </select>
        </p>
        <p>
            <label for="file">Файл книги (PDF, EPUB, FB2, MOBI, DOCX, TXT):</label><br>
            <input type="file" id="file" name="file"
                   accept=".pdf,.epub,.fb2,.mobi,.docx,.txt" required>
        </p>
        <button type="submit" class="usual-button">Загрузить</button>
    </form>

    <p style="margin-top: 15px;">
        <a href="${currentContext}/admin/edit-author?id=${authorId}">Назад к профилю автора</a>
    </p>

    <script src="${currentContext}/static/js/ai.js"></script>
</@base.layout>