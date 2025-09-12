<%@ page import="app.Db.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
  User user = (User) session.getAttribute("user");
  if (user == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
  }
%>
<!DOCTYPE html>
<html>
<head><title>Home</title></head>
<body>
  <h1>Welcome, <%= user.username() %></h1>
  <p>Your user id from the database: <strong><%= user.id() %></strong></p>

  <form method="post" action="<%=request.getContextPath()%>/logout">
    <button type="submit">Logout</button>
  </form>
</body>
</html>
