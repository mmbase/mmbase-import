<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@ page import="org.mmbase.www.*,java.net.*" session="true"%>
<mm:cloud logon="admin" pwd="xxxxxxx" jspvar="cloud">
<%
  try {
    PartnerHandler.addProject(cloud, request);
    String url = "/download/article/partner.jsp?message=" + URLEncoder.encode("Project has been added.");
    config.getServletContext().getRequestDispatcher(url).forward(request, response);
  } catch(Exception ex) {
     String url = "/download/article/partner.jsp?message=" + URLEncoder.encode(ex.getMessage());
     config.getServletContext().getRequestDispatcher(url).forward(request, response);
  }
%>
</mm:cloud>
