<!DOCTYPE html>
<html>
<head>
    <title>${formTitle}</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>${formTitle}</h1>

<#if error??>
    <div style="color:red; margin-bottom: 15px;">
        ${error}
    </div>
</#if>

<form action="${formAction}" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div>
        <label>Заголовок:</label><br>
        <input type="text" name="title" value="<#if note?? && note.title??>${note.title}</#if>" required>
    </div>

    <div style="margin-top: 10px;">
        <label>Содержимое:</label><br>
        <textarea name="content" rows="8" cols="50"><#if note?? && note.content??>${note.content}</#if></textarea>
    </div>

    <div style="margin-top: 10px;">
        <label>
            <input type="checkbox" name="public" value="true" <#if note?? && note.public>checked</#if>>
            Публичная заметка
        </label>
    </div>

    <div style="margin-top: 15px;">
        <button type="submit">Сохранить</button>
        <a href="/notes" style="margin-left: 10px;">Назад</a>
    </div>
</form>
</body>
</html>