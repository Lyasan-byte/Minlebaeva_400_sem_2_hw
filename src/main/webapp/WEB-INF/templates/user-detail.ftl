<!DOCTYPE html>
<html>
<head>
    <title>Просмотр пользователя</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Информация о пользователе</h1>

<#if error??>
    <div style="color: red;">
        <p>${error}</p>
    </div>
</#if>

<#if user??>
    <table border="1">
        <tr>
            <th>ID:</th>
            <td>${user.id}</td>
        </tr>
        <tr>
            <th>Username:</th>
            <td>${user.username}</td>
        </tr>
    </table>

    <div style="margin-top: 20px;">
        <a href="/users/${user.id}/edit">Редактировать</a>
        |
        <a href="/users">Назад к списку</a>
        |
        <form action="/users/${user.id}/delete" method="post" style="display:inline;">
            <button type="submit" onclick="return confirm('Вы уверены, что хотите удалить этого пользователя?')">
                Удалить
            </button>
        </form>
    </div>
<#else>
    <p style="color: red;">Пользователь не найден</p>
    <a href="/users">Назад к списку</a>
</#if>
</body>
</html>