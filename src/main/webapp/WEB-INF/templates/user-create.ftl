<!DOCTYPE html>
<html>
<head>
    <title>Создать пользователя</title>
</head>
<body>
<h1>Новый пользователь</h1>

<form action="/users" method="post">
    <label>Username:
        <input type="text" name="username" required>
    </label>
    <button type="submit">Создать</button>
</form>
<br><br>
<a href="/users">Назад</a>
</body>
</html>