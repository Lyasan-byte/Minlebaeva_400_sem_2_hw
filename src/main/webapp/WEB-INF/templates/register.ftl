<!DOCTYPE html>
<html>
<head>
    <title>Регистрация</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial; margin: 40px; }
        .error { color: red; margin-bottom: 15px; }
        input { padding: 5px; margin: 5px; width: 250px; }
        button { padding: 8px 15px; background: #4CAF50; color: white; border: none; cursor: pointer; }
    </style>
</head>
<body>
<h2>Регистрация</h2>

<#if error??>
    <div class="error">${error}</div>
</#if>

<form method="post" action="/register">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div>
        <label>Имя пользователя:</label><br>
        <input type="text" name="username" value="<#if username??>${username}</#if>" required>
    </div>

    <div>
        <label>Email:</label><br>
        <input type="email" name="email" value="<#if email??>${email}</#if>" required>
    </div>

    <div>
        <label>Пароль:</label><br>
        <input type="password" name="password" required>
    </div>

    <div>
        <label>Подтвердите пароль:</label><br>
        <input type="password" name="confirmPassword" required>
    </div>

    <div style="margin-top: 15px;">
        <button type="submit">Зарегистрироваться</button>
        <a href="/login" style="margin-left: 10px;">Уже есть аккаунт?</a>
    </div>
</form>
</body>
</html>