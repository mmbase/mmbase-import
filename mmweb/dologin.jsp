<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@ page import="org.mmbase.www.*,java.net.*" session="true"%>
<mm:cloud jspvar="cloud">
<%
  try {
    if(request.getParameter("forgotten") != null) {
       AccountHandler.forgotten(cloud, request);
       String url = "/login/mmlogin.jsp?message=" + URLEncoder.encode("An email has been sent.");
       config.getServletContext().getRequestDispatcher(url).forward(request, response);
    } else {
       AccountHandler.login(cloud, request, response);
       String orgUrl = request.getParameter("orgLocation");
       String url = orgUrl == null ? request.getContextPath() + "/index.jsp" : orgUrl;
       response.sendRedirect(url);
    }
  } catch(Exception ex) {
     String msg = ex.getMessage();
     msg = msg == null ? ex.getClass().getName() : msg;
     String url = "/login/mmlogin.jsp?reason=" + URLEncoder.encode(msg);
     config.getServletContext().getRequestDispatcher(url).forward(request, response);
  }
%>
</mm:cloud>
