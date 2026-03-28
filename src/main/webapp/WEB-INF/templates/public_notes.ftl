<!DOCTYPE html>
<html>
<head>
    <title>Публичные заметки</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Публичные заметки</h1>

<br>

<form method="get" action="/notes/public" style="margin-bottom: 20px;">
    <input type="text" name="keyword" value="<#if keyword??>${keyword}</#if>" placeholder="Поиск по заголовку">
    <button type="submit">Искать</button>
</form>

<table border="1" cellpadding="8">
    <thead>
    <tr>
        <th>ID</th>
        <th>Заголовок</th>
        <th>Содержимое</th>
        <th>Дата</th>
        <th>Автор</th>
    </tr>
    </thead>
    <tbody>
    <#list notes as note>
        <tr>
            <td>${note.id}</td>
            <td>${note.title}</td>
            <td><#if note.content??>${note.content}</#if></td>
            <td>${note.createdAt}</td>
            <td>${note.author.username}</td>
        </tr>
    <#else>
        <tr>
            <td colspan="5" style="text-align:center; color:gray;">Публичных заметок нет</td>
        </tr>
    </#list>
    </tbody>
</table>
</body>
</html>