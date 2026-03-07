<!DOCTYPE html>
<html>
<head>
    <title>Создать пользователя</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Новый пользователь</h1>

<#if error??>
    <div style="color: red; margin-bottom: 20px;">
        <p>${error}</p>
    </div>
</#if>

<form action="/users" method="post">
    <div>
        <label for="username">Username:</label>
        <input type="text"
               id="username"
               name="username"
               value="<#if user?? && user.username??>${user.username}</#if>"
               required
               placeholder="Введите имя пользователя"/>
    </div>

    <div style="margin-top: 20px;">
        <button type="submit">Создать</button>
        <a href="/users" style="margin-left: 10px;">Отмена</a>
    </div>
</form>

<br/>
<a href="/users">Назад к списку</a>
</body>
</html>