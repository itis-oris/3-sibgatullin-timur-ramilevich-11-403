<#ftl encoding="UTF-8">
<#import "layout/base.ftlh" as base>
<@base.layout title="Пользователь ${anotherUser.author.nickname!}">
    <h2>Профиль пользователя ${anotherUser.author.nickname!}</h2>

    <p>
    <h3>О пользователе:</h3>
    <label>${anotherUser.author.bio!}</label>
    </p>

    <#if isAuthorOrAdmin>
        <h3>Книги этого автора:</h3>
        <#if authorBooks?size == 0>
            <p>Автор пока не добавил ни одной книги.</p>
        <#else>
            <ul>
                <#list authorBooks as book>
                    <li><a href="${currentContext}/book?id=${book.id}">${book.title}</a></li>
                </#list>
            </ul>
        </#if>
    </#if>
    <h3>Комментарии пользователя: ${anotherUser.author.nickname}</h3>
    <#if userComments?size == 0>
        <p>Пользователь пока не оставлял комментариев.</p>
    <#else>
        <ul>
            <#list userComments as comment>
                <li>
                    Комментарий к: <a href="${currentContext}/book?id=${comment.book.id}">${comment.book.title}</a>
                    от ${comment.createdAtFormatted} — ${comment.text}
                </li>
            </#list>
        </ul>
    </#if>

</@base.layout>
