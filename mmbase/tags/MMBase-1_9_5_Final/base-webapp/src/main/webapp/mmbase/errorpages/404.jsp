<%@page session="false" %><% response.setStatus(404); 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<html>
  <head>
    <title>404 The requested resource is unavailable</title>
    <%@include file="meta.jsp" %>
  </head>
  <body class="basic">
    <h1>404 The requested resource is unavailable</h1>
    <h2><%=org.mmbase.Version.get()%></h2>
    <p>
      <% String mesg = (String) request.getAttribute("org.mmbase.servlet.error.message");
         if (mesg == null) {
       %>
      The current URL (<%=request.getAttribute("javax.servlet.error.message")%>) does not
      point to an existing resource in this web-application.
      <% } else { %>
      <%=mesg%>
      <% } %>
    </p>
  </body>
</html>

