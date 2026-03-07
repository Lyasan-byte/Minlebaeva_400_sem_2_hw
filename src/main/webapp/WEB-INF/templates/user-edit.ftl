<!DOCTYPE html>
<html>
<head>
    <title>Редактировать пользователя</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Редактировать пользователя</h1>
<#if error??>
    <div style="color: red;">
        <p>${error}</p>
    </div>
</#if>

<#if user??>
    <form action="/users/${user.id}/edit" method="post">
        <div>
            <label for="username">Username:</label>
            <input type="text"
                   id="username"
                   name="username"
                   value="${user.username}"
                   required
                   placeholder="Введите имя пользователя"/>
        </div>

        <div style="margin-top: 20px;">
            <button type="submit">Обновить</button>
            <a href="/users" style="margin-left: 10px;">Отмена</a>
        </div>
    </form>
<#else>
    <p style="color: red;">Пользователь не найден</p>
</#if>

<br/>
<a href="/users">Назад к списку</a>
</body>
</html>