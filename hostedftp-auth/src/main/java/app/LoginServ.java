package app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServ extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.getRequestDispatcher("/login.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String username = req.getParameter("username");
    String password = req.getParameter("password");

    Db.User user;
    try {
      user = Db.authenticate(username, password);
    } catch (SQLException e) {
      throw new ServletException("Database error during login", e);
    }

    if (user == null) {
      req.setAttribute("error", "Invalid username or password.");
      req.setAttribute("prefillUser", username);
      req.getRequestDispatcher("/login.jsp").forward(req, resp);
      return;
    }

    HttpSession session = req.getSession(true);
    session.setAttribute("user", user);
    resp.sendRedirect(req.getContextPath() + "/");
  }
}
