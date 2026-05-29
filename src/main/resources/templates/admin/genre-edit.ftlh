<#import "../layout/base.ftlh" as base>
<@base.layout title="Изменение жанра">
    <h2>Изменение жанра: ${form.name}</h2>
    <form method="post" action="${currentContext}/admin/edit-genre?id=${form.id}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">

        <p>
            <label for="name">Название:</label><br>
            <input type="text" id="name" name="name" value="${form.name!}" required>
        </p>
        <p>
            <label for="description">Описание:</label><br>
            <textarea id="description" name="description" rows="5" cols="40">${form.description!}</textarea>
        </p>
        <button type="submit" class="usual-button">Изменить</button>
    </form>

    <form method="post" action="${currentContext}/admin/edit-genre?id=${form.id}" style="margin-top: 15px;">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">
        <input type="hidden" name="delete" value="true">
        <button type="submit" class="btn-danger"
                onclick="return confirm('Удалить этот жанр?');">
            Удалить жанр
        </button>
    </form>

    <p style="margin-top: 15px;">
        <a href="${currentContext}/admin/all-genres">Назад к списку жанров</a>
    </p>
</@base.layout>