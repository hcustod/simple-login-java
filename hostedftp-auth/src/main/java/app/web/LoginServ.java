package app.web;

import app.dao.UserDao;
import app.model.User;
import app.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServ extends HttpServlet {
  private AuthService auth;

  @Override public void init() {
    this.auth = new AuthService(new UserDao());
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String username = req.getParameter("username");
    String pw = req.getParameter("password");

    try {
      User u = auth.login(username, pw == null ? null : pw.toCharArray());
      if (u == null) {
        req.setAttribute("error", "Invalid username or password.");
        req.setAttribute("prefillUser", username);
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        return;
      }
      HttpSession s = req.getSession(true);
      s.setAttribute("user", u);
      resp.sendRedirect(req.getContextPath() + "/home");
    } catch (SQLException e) {
      throw new ServletException(e);
    }
  }
}
