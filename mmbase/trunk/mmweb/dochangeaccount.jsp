<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@ page import="org.mmbase.www.*,java.net.*" session="true"%>
<mm:cloud logon="admin" pwd="dontusesvp" jspvar="cloud">
<%
  try {
    if(request.getParameter("change_password") != null) {
       AccountHandler.changePassword(cloud, request, response);
       String url = "/login/mmaccount.jsp?message=" + URLEncoder.encode("Password has been changed.");
       config.getServletContext().getRequestDispatcher(url).forward(request, response);
    } else {
       AccountHandler.changeEmail(cloud, request);
       String url = "/login/mmaccount.jsp?message=" + URLEncoder.encode("Email address has been changed.");
       config.getServletContext().getRequestDispatcher(url).forward(request, response);
       response.sendRedirect(url);
    }
  } catch(Exception ex) {
     String url = "/login/mmaccount.jsp?reason=" + URLEncoder.encode(ex.getMessage());
     config.getServletContext().getRequestDispatcher(url).forward(request, response);
  }
%>
</mm:cloud>
