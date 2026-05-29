<#import "../macros/form.ftlh" as form>
<#macro layout title>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${title} | FreeLib</title>
        <link rel="stylesheet" href="${currentContext}/static/css/style.css">
        <meta name="csrf-token" content="${_csrf.token!}">
        <meta name="csrf-param" content="${_csrf.parameterName!}">
    </head>
    <body>
    <div class="container">
        <#include "header.ftlh">
        <main>
            <@form.flashMessages/>
            <#nested>
        </main>
        <#include "footer.ftlh">
    </div>
    <script src="${currentContext}/static/js/app.js"></script>
    </body>
    </html>
</#macro>