<%@page session="false" %><% response.setStatus(403); 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<html>
  <head>
    <title>403 Forbidden</title>
    <%@include file="meta.jsp" %>
  </head>
  <body class="basic">
    <h1>403 Forbidden - <%= request.getAttribute("javax.servlet.error.message") %></h1>
    <h2><%=org.mmbase.Version.get()%></h2>
    <p>
      &nbsp;
      <% String mesg = (String) request.getAttribute("org.mmbase.servlet.error.message");
         if (mesg != null) {
       %>
      <%=mesg%>
      <% } %>
    </p>
  </body>
</html>

