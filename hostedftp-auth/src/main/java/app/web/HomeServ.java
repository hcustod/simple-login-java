package app.web;

import app.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/home")
public class HomeServ extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    User u = (User) req.getSession().getAttribute("user");
    if (u == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    req.setAttribute("username", u.getUsername());
    req.setAttribute("userId", u.getId());

    req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // If someone POSTs to /home, just treat it like a GET
    doGet(req, resp);
  }
}
