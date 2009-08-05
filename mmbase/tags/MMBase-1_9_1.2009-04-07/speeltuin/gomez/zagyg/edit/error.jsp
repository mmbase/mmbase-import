<%@ page isErrorPage="true"
%><%@ page import="java.io.*,java.util.*,org.mmbase.bridge.*,org.mmbase.util.*"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content language="nl">
<mm:cloud>
<html>
  <head>
    <title>Error page</title>
    <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
    <link rel="stylesheet" href="css/style.css" />
  </head>
  <body>
    <%@include file="util/error.div.jsp" %>
  </body>
</html>
</mm:log>
</mm:cloud>
</mm:content>