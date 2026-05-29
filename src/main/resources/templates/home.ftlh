<#import "layout/base.ftlh" as base>
<@base.layout title="Главная страница">
    <#if user??>
        <h2>Добро пожаловать, ${user.nickname!}.</h2>
    </#if>

    <p>
        <h2>Популярные книги</h2>
            <#if popularBooks?size == 0>
                <li>Пока нет популярных книг.</li>
            <#else>
                <ul style="list-style: none; padding: 0;">
                    <#list popularBooks as book>
                        <li style="padding: 15px; margin-bottom: 15px; border: 1px solid #eee; border-radius: 4px; background: #fafafa;">
                            <a href="${currentContext}/book?id=${book.id}" style="font-size: 18px; font-weight: bold;">
                                ${book.title}
                            </a>
                            <span style="color: #666;"> — </span>
                            <a href="${currentContext}/author/${book.author.id}" style="color: #444;">
                                ${book.author.nickname}
                            </a>
                            <span style="float: right; color: #888; font-size: 13px;">
                                ${book.views} просмотров
                            </span>

                            <#if book.description?? && book.description?has_content>
                                <p style="margin: 8px 0 0 0; color: #555; font-size: 14px; line-height: 1.4;">
                                    <#if book.description?length gt 200>
                                        ${book.description[0..200]}...
                                    <#else>
                                        ${book.description}
                                    </#if>
                                </p>
                            </#if>
                        </li>
                    </#list>
                </ul>
            </#if>
    </p>
    <p>
        <h2>Последние книги</h2>
        <#if books?size == 0>
            <li>Пока нет добавленных книг.</li>
        <#else>
            <ul style="list-style: none; padding: 0;">
                <#list books as book>
                    <li style="padding: 15px; margin-bottom: 15px; border: 1px solid #eee; border-radius: 4px; background: #fafafa;">
                        <a href="${currentContext}/book?id=${book.id}" style="font-size: 18px; font-weight: bold;">
                            ${book.title}
                        </a>
                        <span style="color: #666;"> — </span>
                        <a href="${currentContext}/author/${book.author.id}" style="color: #444;">
                            ${book.author.nickname}
                        </a>
                        <span style="float: right; color: #888; font-size: 13px;">
                                ${book.views} просмотров
                            </span>

                        <#if book.description?? && book.description?has_content>
                            <p style="margin: 8px 0 0 0; color: #555; font-size: 14px; line-height: 1.4;">
                                <#if book.description?length gt 200>
                                    ${book.description[0..200]}...
                                <#else>
                                    ${book.description}
                                </#if>
                            </p>
                        </#if>
                    </li>
                </#list>
            </ul>
        </#if>
    </p>
</@base.layout>