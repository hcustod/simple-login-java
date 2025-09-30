<%@ page contentType="text/html;charset=UTF-8" %>
<%
  String ctx = request.getContextPath();
  Object u = request.getAttribute("username");
  String name = (u == null) ? "guest" : String.valueOf(u);
%>
<!DOCTYPE html>
<html>
<head>
  <title>Home</title>
  <meta charset="UTF-8"/>
  <style>
    :root { --bg:#f7f7f8; --panel:#ffffffcc; --border:#e5e7eb; --text:#111827; --muted:#6b7280; }
    *, *::before, *::after { box-sizing: border-box; }
    html, body { height: 100%; }
    body {
      margin: 0;
      min-height: 100dvh;
      display: grid;
      place-items: center;
      padding: 24px;
      background: var(--bg);
      color: var(--text);
      font: 16px/1.5 system-ui, -apple-system, "Segoe UI", Roboto, Arial, sans-serif;
    }
    .container {
      position: relative; z-index: 1;
      width: 100%; max-width: 560px;
      background: var(--panel);
      border: 1px solid var(--border);
      border-radius: 14px;
      padding: 24px;
      box-shadow: 0 10px 20px rgba(0,0,0,0.06), 0 6px 6px rgba(0,0,0,0.05);
      backdrop-filter: blur(6px);
    }
    h1 { margin: 0 0 8px; font-size: 26px; }
    .muted { color: var(--muted); margin: 0 0 20px; }
    form { margin: 0; }
    .btn {
      display: inline-block;
      padding: 12px 16px;
      border: 1px solid var(--text);
      background: var(--text);
      color: #fff;
      border-radius: 10px;
      cursor: pointer;
    }
    .btn:hover { filter: brightness(0.95); }
    #bg-wrap { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
    #bg-canvas { width: 100%; height: 100%; display: block; }
  </style>
</head>
<body>
  <div class="container" role="main" aria-labelledby="page-title">
    <h1 id="page-title">Welcome, <%= name %>!</h1>
    <p class="muted">You’re signed in.</p>
    <p class="muted">Your UserID is: <%= request.getAttribute("userId") %></p>

    <form method="post" action="<%= ctx %>/logout">
      <button class="btn" type="submit">Logout</button>
    </form>
  </div>

  <div id="bg-wrap"><canvas id="bg-canvas"></canvas></div>
  <link rel="stylesheet" href="<%= ctx %>/css/background.css"/>
  <script src="https://unpkg.com/three@0.159.0/build/three.min.js"></script>
  <script src="<%= ctx %>/js/background.js"></script>
</body>
</html>
