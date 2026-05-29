document.addEventListener('DOMContentLoaded', () => {
    const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content;

    const originalFetch = window.fetch;
    window.fetch = async (url, options = {}) => {
        options.headers = options.headers || {};
        if (csrfToken) {
            options.headers['X-XSRF-TOKEN'] = csrfToken;
        }
        options.credentials = 'same-origin';

        try {
            const response = await originalFetch(url, options);
            if (response.status === 401) {
                alert('Сессия истекла. Пожалуйста, войдите снова.');
                window.location.href = '/auth/login';
                return response;
            }
            if (response.status === 403) {
                alert('Доступ запрещён.');
                return response;
            }
            return response;
        } catch (err) {
            console.error('Network error:', err);
            alert('Ошибка сети. Проверьте соединение.');
            throw err;
        }
    };

    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function() {
            const btn = form.querySelector('button[type="submit"]');
            if (btn && !btn.disabled) {
                btn.disabled = true;
                btn.dataset.originalText = btn.innerText;
                btn.innerText = 'Отправка...';
                setTimeout(() => {
                    btn.disabled = false;
                    btn.innerText = btn.dataset.originalText;
                }, 1000);
            }
        });
    });

    document.querySelectorAll('[data-fav-btn]').forEach(btn => {
        btn.addEventListener('click', async function() {
            const bookId = this.dataset.bookId;
            const isFav = this.dataset.fav === 'true';
            const url = `${window.location.origin}/api/v1/favorites/${bookId}`;
            const method = isFav ? 'DELETE' : 'POST';

            try {
                const res = await fetch(url, { method });
                if (res.ok) {
                    this.dataset.fav = (!isFav).toString();
                    this.innerText = isFav ? 'Добавить в избранное' : 'Удалить из избранного';
                }
            } catch (e) {
                console.error(e);
            }
        });
    });
});