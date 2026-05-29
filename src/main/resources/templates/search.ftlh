<#import "layout/base.ftlh" as base>
<@base.layout title="Поиск книг">
    <h2>Расширенный поиск книг</h2>

    <div style="margin-bottom: 20px; padding: 15px; background: #f9f9f9; border-radius: 4px;">
        <p>
            <label for="searchTitle">Название книги:</label>
            <input type="text" id="searchTitle" placeholder="Введите часть названия..." style="width: 300px;">
        </p>
        <p>
            <label for="searchAuthor">Автор:</label>
            <input type="text" id="searchAuthor" placeholder="Введите имя автора..." style="width: 300px;">
        </p>
        <p>
            <label for="searchGenre">Жанры (удерживайте Ctrl для выбора нескольких):</label>
            <select id="searchGenre" multiple size="5" style="width: 300px;">
                <#list genres as genre>
                    <option value="${genre.id}">${genre.name}</option>
                </#list>
            </select>
        </p>
        <p>
            <label for="searchMinViews">Мин. просмотров:</label>
            <input type="number" id="searchMinViews" min="0" style="width: 150px;">
        </p>
        <p>
            <label for="searchMaxViews">Макс. просмотров:</label>
            <input type="number" id="searchMaxViews" min="0" style="width: 150px;">
        </p>
        <button type="button" class="usual-button" onclick="searchBooks()">Найти</button>
        <button type="button" class="usual-button" onclick="resetSearch()">Сбросить</button>
    </div>

    <div id="searchResults">
        <p style="color: #666;">Введите параметры поиска и нажмите "Найти"</p>
    </div>

    <div id="pagination" style="margin-top: 20px;"></div>
    <script src="${currentContext}/static/js/search.js"></script>
</@base.layout>