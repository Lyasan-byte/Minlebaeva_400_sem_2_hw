<!DOCTYPE html>
<html>
<head>
    <title>Пользователи</title>
</head>
<body>
<h1>Список пользователей</h1>

<a href="/users/create">Добавить пользователя</a>
<br><br>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Username</th>
        <th>Действия</th>
    </tr>
    <#list users as user>
        <tr>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>
                <a href="/users/${user.id}">Просмотр</a>
                <form action="/users/${user.id}/delete" method="post" style="display:inline;">
                    <button type="submit">Удалить</button>
                </form>
            </td>
        </tr>
    </#list>
</table>
</body>
</html>