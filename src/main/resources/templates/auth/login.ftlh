<#import "../layout/base.ftlh" as base>
<@base.layout title="Вход">
    <h2>Вход в систему</h2>
    <form action="${currentContext}/auth/login" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <p>
            <label>Логин</label><br>
            <input type="text" name="login" value="${form.login!}" required>
        </p>
        <p>
            <label>Пароль</label><br>
            <input type="password" name="password" required>
        </p>
        <button type="submit" class="usual-button">Войти</button>
    </form>

    <p style="margin-top: 15px;">
        Ещё не зарегистрированы?
        <a href="${currentContext}/auth/register">Создать аккаунт</a>
    </p>
</@base.layout>