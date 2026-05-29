<#import "layout/base.ftlh" as base>
<@base.layout title="Все жанры">
    <h2>Жанры книг</h2>
    <p style="color: #666; margin-bottom: 20px;">
        Выберите жанр, чтобы увидеть доступные книги
    </p>

    <#if genres?size == 0>
        <p>Жанров пока нет.</p>
    <#else>
        <ul style="list-style: none; padding: 0;">
            <#list genres as genre>
                <li style="padding: 15px; margin-bottom: 15px; border: 1px solid #eee; border-radius: 4px; background: #fafafa;">
                    <a href="${currentContext}/books?genreId=${genre.id}" style="font-size: 18px; font-weight: bold;">
                        ${genre.name}
                    </a>
                    <#if genre.description?? && genre.description?has_content>
                        <p style="margin: 8px 0 0 0; color: #555; font-size: 14px; line-height: 1.4;">
                            ${genre.description}
                        </p>
                    </#if>
                </li>
            </#list>
        </ul>
    </#if>
</@base.layout>