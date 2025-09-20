<%@ page contentType="text/html;charset=UTF-8" %>
<%
  String ctx = request.getContextPath();
  Object u = request.getAttribute("username");
  String name = (u == null) ? "guest" : u.toString();
%>
<!DOCTYPE html>
<html>
<head>
  <title>Home</title>
  <meta charset="UTF-8"/>
  <style>
    :root { --bg:#f7f7f8; --panel:#fff; --border:#e5e7eb; --text:#111827; --muted:#6b7280; }
    html,body { height:100%; }
    body { margin:0; background:var(--bg); color:var(--text);
           font: 16px/1.5 system-ui, -apple-system, "Segoe UI", Roboto, Arial, sans-serif; }
    .container { max-width:720px; margin:48px auto; background:var(--panel);
                 border:1px solid var(--border); border-radius:12px; padding:24px; }
    h1 { margin:0 0 8px; font-size:26px; }
    .muted { color:var(--muted); margin:0 0 20px; }
    .btn { display:inline-block; padding:10px 16px; border:1px solid var(--text);
           background:var(--text); color:#fff; border-radius:8px; cursor:pointer; }
    form { margin:0; }
  </style>
</head>
<body>
  <div class="container">
    <h1>Welcome, <%= name %>!</h1>
    <p class="muted">You’re signed in.</p>

    <p class="muted">Your UserID is: <%= request.getAttribute("userId") %></p> 

    <form method="post" action="<%= ctx %>/logout">
      <button class="btn" type="submit">Logout</button>
    </form>
  </div>
</body>
</html>
