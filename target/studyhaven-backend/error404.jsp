<%@ page isErrorPage="true" %>
<html>
<head>
    <title>404 Error</title>
</head>
<body>
    <h2>404 Error: Resource not found</h2>
    <p>The requested resource could not be found: <%= request.getAttribute("javax.servlet.error.request_uri") %></p>
</body>
</html>