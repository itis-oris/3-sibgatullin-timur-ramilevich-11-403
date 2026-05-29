<#import "../layout/base.ftlh" as base>
<@base.layout title="Автор: ${author.nickname}">
    <h2>Профиль автора ${author.nickname!}</h2>
    <p>
        <h3>Об авторе:</h3>
        <label>${author.bio!}</label>
    </p>

    <h3>Книги автора:</h3>
    <#if authorBooks?size == 0>
        <p>У этого автора пока нет книг.</p>
    <#else>
        <ul>
            <#list authorBooks as book>
                <li><a href="${currentContext}/book?id=${book.id}">${book.title}</a></li>
            </#list>
        </ul>
    </#if>
    <#if author.account??>
        <h3>Комментарии автора:</h3>
        <#if authorComments?size == 0>
            <p>Автор пока не оставлял комментариев.</p>
        <#else>
            <ul>
                <#list authorComments as comment>
                    <li>Комментарий к: <a href="${currentContext}/book?id=${comment.book.id}">${comment.book.title}</a>
                        от ${comment.createdAtFormatted} — ${comment.text}</li>
                </#list>
            </ul>
        </#if>
    </#if>
</@base.layout>