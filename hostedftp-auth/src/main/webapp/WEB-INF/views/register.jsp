<%@ page contentType="text/html;charset=UTF-8" %>
<%
  String ctx = request.getContextPath();

  Object prefill = request.getAttribute("prefillUser");
  String prefillUser = (prefill == null) ? "" : String.valueOf(prefill);

  Object err = request.getAttribute("error");
  String errorMsg = (err == null) ? "" : String.valueOf(err);

  Object minObj = request.getAttribute("minPwLen");
  int minPwLen;
  try { minPwLen = (minObj == null) ? 12 : Integer.parseInt(String.valueOf(minObj)); }
  catch (Exception e) { minPwLen = 12; }
%>
<!DOCTYPE html>
<html>
<head>
  <title>Register</title>
  <meta charset="UTF-8"/>
  <style>
    :root { --bg:#f7f7f8; --panel:#ffffffcc; --border:#e5e7eb; --text:#111827; --muted:#6b7280; --danger:#dc2626; }
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
      width: 100%; max-width: 460px;
      background: var(--panel);
      border: 1px solid var(--border);
      border-radius: 14px;
      padding: 24px;
      box-shadow: 0 10px 20px rgba(0,0,0,0.06), 0 6px 6px rgba(0,0,0,0.05);
      backdrop-filter: blur(6px);
    }
    h1 { margin: 0 0 18px; font-size: 26px; }
    .field { margin-bottom: 16px; }
    .label { display: block; font-weight: 600; margin-bottom: 8px; }
    .input {
      display: block;
      width: 100%;
      padding: 12px 14px;
      border: 1px solid #d1d5db;
      border-radius: 10px;
      outline: none;
      background: #fff;
    }
    .input:focus {
      border-color: #2563eb;
      box-shadow: 0 0 0 3px rgba(37,99,235,0.15);
    }
    .hint { display:block; margin-top:6px; font-size:12px; color:var(--muted); }
    .btn {
      display: inline-block;
      width: 100%;
      padding: 12px 16px;
      border: 1px solid var(--text);
      background: var(--text);
      color: #fff;
      border-radius: 10px;
      cursor: pointer;
    }
    .btn:hover { filter: brightness(0.95); }
    .row { margin-top: 14px; }
    .muted { color: var(--muted); }
    .link { color: #2563eb; text-decoration: none; }
    .link:hover { text-decoration: underline; }
    .error { color: var(--danger); margin-top: 12px; }

    #bg-wrap { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
    #bg-canvas { width: 100%; height: 100%; display: block; }
  </style>
</head>
<body>
  <div class="container" role="main" aria-labelledby="page-title">
    <h1 id="page-title">Create account</h1>

    <form method="post" action="<%= ctx %>/register" novalidate>
      <div class="field">
        <label class="label" for="username">Username</label>
        <input class="input" id="username" name="username" type="text"
               autocomplete="username" value="<%= prefillUser %>" required>
      </div>

      <div class="field">
        <label class="label" for="password">Password</label>
        <input class="input" id="password" name="password" type="password"
               minlength="<%= minPwLen %>" autocomplete="new-password" required>
        <span class="hint">At least <%= minPwLen %> characters.</span>
      </div>

      <div class="field">
        <label class="label" for="confirm">Confirm password</label>
        <input class="input" id="confirm" name="confirm" type="password"
               minlength="<%= minPwLen %>" autocomplete="new-password" required>
      </div>

      <button class="btn" type="submit">Create account</button>
    </form>

    <p class="error"><%= errorMsg %></p>

    <p class="row muted">
      Already have an account? <a class="link" href="<%= ctx %>/login">Log in</a>
    </p>
  </div>

  <div id="bg-wrap"><canvas id="bg-canvas"></canvas></div>
  <link rel="stylesheet" href="<%= ctx %>/css/background.css"/>
  <script src="https://unpkg.com/three@0.159.0/build/three.min.js"></script>
  <script src="<%= ctx %>/js/background.js"></script>
</body>
</html>
