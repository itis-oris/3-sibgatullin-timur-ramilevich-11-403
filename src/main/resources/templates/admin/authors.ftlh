<#import "../layout/base.ftlh" as base>
<@base.layout title="Все авторы">
    <h2>Список всех авторов</h2>
    <label>Нажмите чтобы попасть в профиль</label>
    <#if authors?size == 0>
        <p>Авторов пока нет.</p>
    <#else>
        <ul class="admin-list">
            <#list authors as author>
                <li>
                    <a href="${currentContext}/admin/edit-author?id=${author.id}" class="admin-list__title">
                        ${author.nickname}
                    </a>
                    <#if author.bio?? && author.bio?has_content>
                        <p class="admin-list__desc">${author.bio}</p>
                    </#if>
                </li>
            </#list>
        </ul>
    </#if>
    <p class="admin-actions">
        <a href="${currentContext}/admin/create-author" class="usual-button">Создать нового автора</a>
        <a href="${currentContext}/admin">← Назад в админ-панель</a>
    </p>
    </html>
</@base.layout>
