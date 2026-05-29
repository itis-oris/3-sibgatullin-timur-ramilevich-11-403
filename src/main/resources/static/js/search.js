let currentPage = 0;
const pageSize = 10;

function searchBooks(page = 0) {
    currentPage = page;
    const title = document.getElementById('searchTitle').value.trim();
    const authorName = document.getElementById('searchAuthor').value.trim();
    const genreSelect = document.getElementById('searchGenre');
    const genreIds = Array.from(genreSelect.selectedOptions).map(o => o.value);
    const minViews = document.getElementById('searchMinViews').value;
    const maxViews = document.getElementById('searchMaxViews').value;

    const params = new URLSearchParams();
    if (title) params.append('title', title);
    if (authorName) params.append('authorName', authorName);
    genreIds.forEach(id => params.append('genreIds', id));
    if (minViews) params.append('minViews', minViews);
    if (maxViews) params.append('maxViews', maxViews);
    params.append('page', page);
    params.append('size', pageSize);
    params.append('sort', 'createdAt,desc');

    const resultsDiv = document.getElementById('searchResults');
    resultsDiv.innerHTML = '<p>Загрузка...</p>';

    fetch('/api/v1/books?' + params.toString())
        .then(response => {
            if (!response.ok) throw new Error('Ошибка сети');
            return response.json();
        })
        .then(data => {
            displayResults(data);
        })
        .catch(error => {
            resultsDiv.innerHTML = '<p style="color: red;">Ошибка: ' + error.message + '</p>';
        });
}

function resetSearch() {
    document.getElementById('searchTitle').value = '';
    document.getElementById('searchAuthor').value = '';
    document.getElementById('searchGenre').selectedIndex = -1;
    document.getElementById('searchMinViews').value = '';
    document.getElementById('searchMaxViews').value = '';
    document.getElementById('searchResults').innerHTML = '<p style="color: #666;">Введите параметры поиска и нажмите "Найти"</p>';
    document.getElementById('pagination').innerHTML = '';
    currentPage = 0;
}

function displayResults(data) {
    const resultsDiv = document.getElementById('searchResults');
    const paginationDiv = document.getElementById('pagination');

    if (data.content.length === 0) {
        resultsDiv.innerHTML = '<p>Книги не найдены</p>';
        paginationDiv.innerHTML = '';
        return;
    }

    let html = '<h3>Найдено книг: ' + data.totalElements + '</h3><ul style="list-style: none; padding: 0;">';
    data.content.forEach(book => {
        html += `
            <li style="margin-bottom: 15px; padding: 15px; border: 1px solid #eee; border-radius: 4px; background: #fafafa;">
                <a href="/book?id=${book.id}" style="font-size: 18px; font-weight: bold;">${book.title}</a>
                <span style="float: right; color: #888; font-size: 13px;">👁 ${book.views} просмотров</span><br>
                <small>Автор: ${book.authorNickname}</small><br>
                <small>Жанры: ${book.genreNames.join(', ')}</small>
                ${book.description ? '<br><small style="color: #666;">' + book.description.substring(0, 200) + '...</small>' : ''}
            </li>
        `;
    });
    html += '</ul>';
    resultsDiv.innerHTML = html;

    let paginationHtml = '';
    if (data.totalPages > 1) {
        paginationHtml += '<p>Страница ' + (currentPage + 1) + ' из ' + data.totalPages + '</p>';
        if (currentPage > 0) {
            paginationHtml += '<button class="usual-button" onclick="searchBooks(' + (currentPage - 1) + ')">← Назад</button> ';
        }
        if (currentPage < data.totalPages - 1) {
            paginationHtml += '<button class="usual-button" onclick="searchBooks(' + (currentPage + 1) + ')">Вперёд →</button>';
        }
    }
    paginationDiv.innerHTML = paginationHtml;
}

document.getElementById('searchTitle').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') searchBooks();
});
document.getElementById('searchAuthor').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') searchBooks();
});