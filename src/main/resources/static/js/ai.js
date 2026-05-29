function getCsrfHeader() {
    const tokenMeta = document.querySelector('meta[name="csrf-token"]');
    const paramMeta = document.querySelector('meta[name="csrf-param"]');
    if (!tokenMeta || !paramMeta) return {};
    return { 'X-XSRF-TOKEN': tokenMeta.content };
}

function collectFormData() {
    const title = document.getElementById('title').value.trim();
    const description = document.getElementById('description').value.trim();
    const author = document.getElementById('authorNickname').value;

    const genreSelect = document.getElementById('genreIds');
    const genres = Array.from(genreSelect.selectedOptions).map(o => o.text);

    return { title, description, author, genres };
}

function callAi(endpoint, requireDescription) {
    const { title, author, genres, description } = collectFormData();
    const textarea = document.getElementById('description');

    if (!title || !author || genres.length === 0) {
        alert('Заполните название, автора и выберите хотя бы один жанр');
        return;
    }
    if (requireDescription && !description) {
        alert('Сначала напишите черновик описания, чтобы ИИ мог его улучшить');
        return;
    }

    const originalValue = textarea.value;
    textarea.value = 'Запрос генерируется... это может занять 10-30 секунд';
    textarea.disabled = true;

    const body = { title, author, genres };
    if (requireDescription) {
        body.existingDesc = description;
    }

    const headers = { 'Content-Type': 'application/json' };
    const csrf = getCsrfHeader();
    for (const key in csrf) {
        headers[key] = csrf[key];
    }

    fetch(endpoint, { method: 'POST', headers: headers, body: JSON.stringify(body) })
        .then(function (response) { return response.json(); })
        .then(function (data) {
            textarea.disabled = false;
            if (data.error) {
                alert('Ошибка ИИ: ' + data.error);
                textarea.value = originalValue;
            } else {
                textarea.value = data.description;
            }
        })
        .catch(function (err) {
            textarea.disabled = false;
            textarea.value = originalValue;
            alert('Ошибка сети: ' + err.message);
        });
}

function generateDescription() {
    callAi('/api/v1/ai/generate-description', false);
}

function improveDescription() {
    callAi('/api/v1/ai/improve-description', true);
}