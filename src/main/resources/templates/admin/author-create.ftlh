<#import "../layout/base.ftlh" as base>
<@base.layout title="Создание автора">
    <h2>Создание автора</h2>
    <form action="${currentContext}/admin/create-author" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">
        <p>
            <label>Никнейм</label><br>
            <input type="text" name="nickname" value="${form.nickname!}" required>
        </p>
        <p>
            <label>Об авторе</label><br>
            <textarea name="bio" rows="5" cols="40">${form.bio!}</textarea>
        </p>
        <button type="submit" class="usual-button">Создать</button>
    </form>

    <p style="margin-top: 15px;">
        <a href="${currentContext}/admin">Назад в админ-панель</a>
    </p>
</@base.layout>