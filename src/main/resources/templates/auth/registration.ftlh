<#import "../layout/base.ftlh" as base>
<@base.layout title="Регистрация">
    <h2>Регистрация</h2>
    <form action="${currentContext}/auth/register" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">

        <p>
            <label>Логин</label><br>
            <input type="text" name="login" value="${form.login!}" required>
            <#if fieldErrors?? && fieldErrors.login??>
                <br><span style="color:red; font-size:13px;">${fieldErrors.login}</span>
            </#if>
        </p>

        <p>
            <label>Пароль (16+ симв., строчная, прописная, цифра)</label><br>
            <input type="password" name="password" required>
            <#if fieldErrors?? && fieldErrors.password??>
                <br><span style="color:red; font-size:13px;">${fieldErrors.password}</span>
            </#if>
        </p>

        <p>
            <label>Повторите пароль</label><br>
            <input type="password" name="passwordConfirm" required>
            <#if fieldErrors?? && fieldErrors.passwordConfirm??>
                <br><span style="color:red; font-size:13px;">${fieldErrors.passwordConfirm}</span>
            </#if>
        </p>

        <p>
            <label>Выберите роль</label><br>
            <select name="role">
                <option value="READER">Читатель</option>
                <option value="AUTHOR">Автор</option>
            </select>
        </p>

        <p>
            <label>Никнейм (публичное имя)</label><br>
            <input type="text" name="nickname" value="${form.nickname!}" required>
            <#if fieldErrors?? && fieldErrors.nickname??>
                <br><span style="color:red; font-size:13px;">${fieldErrors.nickname}</span>
            </#if>
        </p>

        <p>
            <label>Расскажите о себе</label><br>
            <textarea name="description" rows="5" cols="40">${form.description!}</textarea>
        </p>

        <button type="submit" class="usual-button">Зарегистрироваться</button>
    </form>

    <p style="margin-top: 15px;">
        Уже есть аккаунт? <a href="${currentContext}/auth/login">Войти</a>
    </p>
</@base.layout>