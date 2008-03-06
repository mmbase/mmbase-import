<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@ page import="org.mmbase.www.*,java.net.*" session="true"%>
<mm:cloud logon="admin" pwd="dontusesvp" jspvar="cloud">
<%
  try {
    PartnerHandler.addProject(cloud, request);
%> <jsp:forward page="/development/article/partner.jsp">
     <jsp:param name="message" value="Project has been added."/>
   </jsp:forward> <%
  } catch(Exception ex) {
    String message = ex.getMessage();
    message = message == null ? "" : message;
%> <jsp:forward page="/development/article/partner.jsp">
     <jsp:param name="message" value="<%= message %>"/>
   </jsp:forward> <%
  }
%>
</mm:cloud>
