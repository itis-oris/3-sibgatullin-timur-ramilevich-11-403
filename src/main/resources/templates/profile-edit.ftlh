<#import "layout/base.ftlh" as base>
<@base.layout title="Редактировать свой профиль">
    <h2>Редактирование профиля</h2>
    <form action="${currentContext}/profile-edit" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">

        <p>
            <label>Никнейм</label><br>
            <input type="text" name="nickname" value="${form.nickname!}" required>
        </p>
        <p>
            <label>О себе</label><br>
            <textarea name="description" rows="5" cols="40">${form.description!}</textarea>
        </p>
        <button type="submit" class="usual-button">Изменить</button>
    </form>
    
    <#if user.author>
        <h3 style="margin-top: 20px;">Ваши книги:</h3>
        <#if myBooks?size == 0>
            <p>Вы пока не добавили ни одной книги.</p>
        <#else>
            <ul>
                <#list myBooks as book>
                    <li>
                        <a href="${currentContext}/edit-my-book?id=${book.id}">${book.title}</a>
                    </li>
                </#list>
            </ul>
        </#if>
    </#if>
</@base.layout>