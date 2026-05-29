<#import "../layout/base.ftlh" as base>
<@base.layout title="Все жанры">
    <h2>Список всех жанров</h2>

    <#if genres?size == 0>
        <p>Жанров пока нет.</p>
    <#else>
        <ul class="admin-list">
            <#list genres as genre>
                <li>
                    <a href="${currentContext}/admin/edit-genre?id=${genre.id}" class="admin-list__title">
                        ${genre.name}
                    </a>
                    <#if genre.description?? && genre.description?has_content>
                        <p class="admin-list__desc">${genre.description}</p>
                    </#if>
                </li>
            </#list>
        </ul>
    </#if>

    <p class="admin-actions">
        <a href="${currentContext}/admin/create-genre" class="usual-button">Создать новый жанр</a>
        <a href="${currentContext}/admin">← Назад в админ-панель</a>
    </p>
</@base.layout>