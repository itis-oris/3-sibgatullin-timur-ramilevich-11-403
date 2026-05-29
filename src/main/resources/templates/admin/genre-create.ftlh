<!DOCTYPE html>
<#import "../layout/base.ftlh" as base>
<@base.layout title="Создание жанра">
<h2>Создание жанра</h2>
<form method="post" action="${currentContext}/admin/create-genre">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">

    <p>
        <label for="name">Название:</label><br>
        <input type="text" id="name" name="name" value="${form.name!}">
        <#if form.name?? && form.name?length < 2>
            <span class="error">От 2 до 50 символов</span>
        </#if>
    </p>

    <p>
        <label for="description">Описание:</label><br>
        <textarea id="description" name="description" rows="4" cols="40">${form.description!}</textarea>
    </p>

    <button type="submit" class="usual-button">Создать</button>
</form>
</@base.layout>