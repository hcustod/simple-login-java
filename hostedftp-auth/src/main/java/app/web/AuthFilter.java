package app.web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebFilter(urlPatterns = {"/home", "/account/*"})
public class AuthFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    HttpSession s = req.getSession(false);
    boolean loggedIn = (s != null && s.getAttribute("user") != null);
    if (!loggedIn) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }
    chain.doFilter(request, response);
  }
}
