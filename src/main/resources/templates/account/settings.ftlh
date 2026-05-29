<#import "../layout/base.ftlh" as base>
<@base.layout title="Настройки аккаунта">
    <h2>Настройки аккаунта</h2>

    <h3>Сменить пароль</h3>
    <form action="${currentContext}/account/change-password" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">
        <p>
            <label>Текущий пароль</label><br>
            <input type="password" name="currentPassword" required>
        </p>
        <p>
            <label>Новый пароль (16+ симв., строчная, прописная, цифра)</label><br>
            <input type="password" name="newPassword" required>
        </p>
        <p>
            <label>Повторите новый пароль</label><br>
            <input type="password" name="confirmPassword" required>
        </p>
        <button type="submit" class="usual-button">Сменить пароль</button>
    </form>

    <hr>
    <h3 style="color:#b00020">Удалить аккаунт</h3>
    <p>Это действие необратимо. Все ваши книги и комментарии будут удалены.</p>
    <form action="${currentContext}/account/delete" method="post"
          onsubmit="return confirm('Вы уверены, что хотите удалить аккаунт?');">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="idempotencyToken" value="${idempotencyToken}">
        <button type="submit" class="btn-danger" onclick="return confirm('Вы хотите удалить аккаунт?');">
            Удалить аккаунт
        </button>
    </form>
</@base.layout>