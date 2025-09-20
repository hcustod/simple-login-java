<%@ page contentType="text/html;charset=UTF-8" %>
<%
  String ctx = request.getContextPath();

  Object prefill = request.getAttribute("prefillUser");
  String prefillUser = (prefill == null) ? "" : prefill.toString();

  Object err = request.getAttribute("error");
  String errorMsg = (err == null) ? "" : err.toString();

  Object minObj = request.getAttribute("minPwLen");
  int minPwLen;
  try {
    minPwLen = (minObj == null) ? 12 : Integer.parseInt(minObj.toString());
  } catch (Exception e) {
    minPwLen = 12; // safe fallback
  }
%>
<!DOCTYPE html>
<html>
<head>
  <title>Register</title>
  <meta charset="UTF-8"/>
  <style>
    :root { --bg:#f7f7f8; --panel:#fff; --border:#e5e7eb; --text:#111827; --muted:#6b7280; --danger:#dc2626; }
    html,body { height:100%; }
    body { margin:0; background:var(--bg); color:var(--text);
           font: 16px/1.5 system-ui, -apple-system, "Segoe UI", Roboto, Arial, sans-serif; }
    .container { max-width:520px; margin:48px auto; background:var(--panel);
                 border:1px solid var(--border); border-radius:12px; padding:24px; }
    h1 { margin:0 0 16px; font-size:26px; }
    .field { margin-bottom:14px; }
    .label { display:block; font-weight:600; margin-bottom:6px; }
    .input { width:100%; padding:10px 12px; border:1px solid #d1d5db; border-radius:8px; outline:none; }
    .hint { display:block; margin-top:6px; font-size:12px; color:var(--muted); }
    .btn { display:inline-block; padding:10px 16px; border:1px solid var(--text);
           background:var(--text); color:#fff; border-radius:8px; cursor:pointer; }
    .row { margin-top:14px; }
    .muted { color:var(--muted); }
    .link { color:#2563eb; text-decoration:none; }
    .error { color:var(--danger); margin-top:12px; }
  </style>
</head>
<body>
  <div class="container">
    <h1>Create account</h1>

    <form method="post" action="<%= ctx %>/register">
      <div class="field">
        <label class="label">Username</label>
        <input class="input" name="username" type="text" autocomplete="username"
               value="<%= prefillUser %>" required>
      </div>

      <div class="field">
        <label class="label">Password</label>
        <input class="input" name="password" type="password"
               minlength="<%= minPwLen %>" autocomplete="new-password" required>
      </div>

      <div class="field">
        <label class="label">Confirm password</label>
        <input class="input" name="confirm" type="password"
               minlength="<%= minPwLen %>" autocomplete="new-password" required>
      </div>

      <button class="btn" type="submit">Create account</button>
    </form>

    <p class="error"><%= errorMsg %></p>

    <p class="row muted">
      Already have an account? <a class="link" href="<%= ctx %>/login">Log in</a>
    </p>
  </div>
</body>
</html>
