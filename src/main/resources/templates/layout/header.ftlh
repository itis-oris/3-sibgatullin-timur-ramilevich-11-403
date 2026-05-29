<header>
    <div class="container">
        <h1>FreeLib</h1>
        <nav>
            <a href="${currentContext}/home">Главная</a>
            <a href="${currentContext}/genres">Жанры</a>
            <a href="${currentContext}/search">Поиск</a>
            <#if user??>
                <a href="${currentContext}/profile">Профиль</a>
                <#if user.author>
                    <a href="${currentContext}/upload">Загрузить книгу</a>
                </#if>
                <#if user.admin>
                    <a href="${currentContext}/admin">Админ-панель</a>
                </#if>
                <a href="${currentContext}/account/settings">Настройки</a>
                <a href="${currentContext}/auth/logout" class="logout-link">Выход (${user.nickname})</a>
            <#else>
                <a href="${currentContext}/auth/login">Вход</a>
                <a href="${currentContext}/auth/register">Регистрация</a>
            </#if>
        </nav>
    </div>
</header>