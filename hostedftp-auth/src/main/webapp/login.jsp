<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Login</title></head>
<body>
  <h1>Login</h1>
  <form method="post" action="<%=request.getContextPath()%>/login">
    <div>
      <label>Username
        <input name="username" value="<%= request.getAttribute("prefillUser") == null ? "" : request.getAttribute("prefillUser") %>">
      </label>
    </div>
    <div>
      <label>Password
        <input name="password" type="password">
      </label>
    </div>
    <button type="submit">Sign in</button>
  </form>

  <p style="color:red;">
    <%= request.getAttribute("error") == null ? "" : request.getAttribute("error") %>
  </p>
</body>
</html>
