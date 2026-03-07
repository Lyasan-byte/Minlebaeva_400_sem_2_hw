<!DOCTYPE html>
<html>
<head>
    <title>Пользователи</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Список пользователей</h1>

<a href="/users/create">Добавить пользователя</a>
<br><br>
<#if error??>
    <div style="color: red;">
        <p>${error}</p>
    </div>
</#if>

<table border="1">
    <thead>
    <tr>
        <th>ID</th>
        <th>Username</th>
        <th>Действия</th>
    </tr>
    </thead>
    <tbody>
    <#list users as user>
        <tr>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>
                <a href="/users/${user.id}">Просмотр</a>
                |
                <a href="/users/${user.id}/edit">Редактировать</a>
                |
                <form action="/users/${user.id}/delete" method="post" style="display:inline;">
                    <button type="submit" onclick="return confirm('Вы уверены, что хотите удалить этого пользователя?')">
                        Удалить
                    </button>
                </form>
            </td>
        </tr>
    <#else>
        <tr>
            <td colspan="3" style="text-align: center; color: gray;">
                Пользователи не найдены
            </td>
        </tr>
    </#list>
    </tbody>
</table>
</body>
</html>