<!DOCTYPE html>
<html>
<head>
    <title>Вход</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial; margin: 40px; }
        .success { color: green; margin-bottom: 15px; }
        .error { color: red; margin-bottom: 15px; }
        input { padding: 5px; margin: 5px; width: 200px; }
        button { padding: 8px 15px; background: #4CAF50; color: white; border: none; cursor: pointer; }
    </style>
</head>
<body>
<h2>Вход в систему</h2>

<#if RequestParameters.registered??>
    <div class="success">Регистрация успешна! Войдите в систему.</div>
</#if>

<#if RequestParameters.error??>
    <div class="error">Неверное имя пользователя или пароль</div>
</#if>

<form method="post" action="/login">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <div>
        <label>Имя пользователя:</label><br>
        <input type="text" name="username" required>
    </div>
    <div>
        <label>Пароль:</label><br>
        <input type="password" name="password" required>
    </div>
    <div style="margin-top: 15px;">
        <button type="submit">Войти</button>
        <a href="/register" style="margin-left: 10px;">Регистрация</a>
    </div>
</form>
</body>
</html>