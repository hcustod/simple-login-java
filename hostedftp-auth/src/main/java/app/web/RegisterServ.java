package app.web;

import app.dao.UserDao;
import app.model.User;
import app.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServ extends HttpServlet {
  private AuthService auth;

  @Override
  public void init() {
    this.auth = new AuthService(new UserDao());
  }

  /** Helper: set attributes the JSP expects on every render */
  private void primeView(HttpServletRequest req, String prefillUser) {
    req.setAttribute("minPwLen", AuthService.getMinPasswordLength());
    if (prefillUser != null) req.setAttribute("prefillUser", prefillUser);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    primeView(req, null);
    req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    // Ensure we correctly read any non-ASCII input
    req.setCharacterEncoding("UTF-8");

    String username = req.getParameter("username");
    String password = req.getParameter("password");
    String confirm  = req.getParameter("confirm");

    // Normalize username for display; leave case decisions to service/DB policy
    String prefill = (username == null) ? "" : username.trim();

    // Always prime the view before any potential forward
    primeView(req, prefill);

    // Basic form check: matching passwords
    if (password == null || confirm == null || !password.equals(confirm)) {
      req.setAttribute("error", "Passwords must match.");
      req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
      return;
    }

    try {
      User u = auth.register(prefill, password.toCharArray());
      // Success: start session and redirect
      HttpSession s = req.getSession(true);
      s.setAttribute("user", u);
      resp.sendRedirect(req.getContextPath() + "/home");
    } catch (IllegalArgumentException ex) {
      // Validation errors (min length, username taken, etc.)
      req.setAttribute("error", ex.getMessage());
      req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    } catch (SQLException e) {
      throw new ServletException(e);
    }
  }
}
