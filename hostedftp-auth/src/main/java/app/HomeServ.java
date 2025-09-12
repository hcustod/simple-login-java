package app;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;

@WebServlet(name="HomeServ", urlPatterns={"/home"})
public class HomeServ extends HttpServlet {
  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Db.User user = (Db.User) req.getSession().getAttribute("user");
    if (user == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }
    req.setAttribute("user", user);
    req.getRequestDispatcher("/WEB-INF/index.jsp").forward(req, resp);
  }
}
