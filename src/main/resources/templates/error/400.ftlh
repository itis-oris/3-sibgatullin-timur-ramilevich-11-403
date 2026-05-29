<#import "../layout/base.ftlh" as base>
<@base.layout title="Некорректный запрос">
    <h2>400 — Некорректный запрос</h2>
    <p>Сервер не смог обработать запрос из-за неверного синтаксиса.</p>
    <a href="${currentContext}/home" class="usual-button">На главную</a>
</@base.layout>
