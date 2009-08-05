package com.finalist.newsletter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UnsubscribeServlet extends HttpServlet {

   private static final long serialVersionUID = 1L;

   public void service(HttpServletRequest request, HttpServletResponse response) {

      int userId = 0;
      int newsletterNumber = 0;

      String user = request.getParameter("user");
      String newsletter = request.getParameter("newsletter");

      if (user != null && user.length() > 0) {
         userId = Integer.parseInt(user);
      }

      if (newsletter != null && newsletter.length() > 0) {
         newsletterNumber = Integer.parseInt(newsletter);
      }

   }
}
