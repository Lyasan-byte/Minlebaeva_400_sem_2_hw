<!DOCTYPE html>
<html>
<head>
    <title>Мои заметки</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Мои заметки</h1>

<div style="margin-bottom: 20px;">
    <a href="/notes/create">Создать заметку</a>
    |
    <a href="/notes/public">Публичные заметки</a>
    |
    <a href="/users">Пользователи</a>
</div>

<form method="get" action="/notes" style="margin-bottom: 20px;">
    <input type="text" name="keyword" value="<#if keyword??>${keyword}</#if>" placeholder="Поиск по заголовку">
    <button type="submit">Искать</button>
</form>

<form action="/logout" method="post" style="margin-bottom: 20px;">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit">Выйти</button>
</form>

<table border="1" cellpadding="8">
    <thead>
    <tr>
        <th>ID</th>
        <th>Заголовок</th>
        <th>Содержимое</th>
        <th>Дата</th>
        <th>Публичная</th>
        <th>Действия</th>
    </tr>
    </thead>
    <tbody>
    <#list notes as note>
        <tr>
            <td>${note.id}</td>
            <td>${note.title}</td>
            <td><#if note.content??>${note.content}</#if></td>
            <td>${note.createdAt}</td>
            <td><#if note.public>Да<#else>Нет</#if></td>
            <td>
                <a href="/notes/${note.id}/edit">Редактировать</a>
                |
                <form action="/notes/${note.id}/delete" method="post" style="display:inline;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" onclick="return confirm('Удалить заметку?')">Удалить</button>
                </form>
            </td>
        </tr>
    <#else>
        <tr>
            <td colspan="6" style="text-align:center; color:gray;">Заметок нет</td>
        </tr>
    </#list>
    </tbody>
</table>
</body>
</html>