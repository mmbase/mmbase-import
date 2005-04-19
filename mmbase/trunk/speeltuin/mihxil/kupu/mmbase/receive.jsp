<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" session="true"  import="java.io.*"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/html" language="en" expires="-1">
<html>
  <head>
    <title>KUPU- receiver</title>
    <link rel="stylesheet" href="mmbase/style/css/mmbase.css" type="text/css" />
    <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
  </head>
<body >
  <mm:log>pathinfo: <%= request.getPathInfo() %> <br />
requesturi: <%= request.getRequestURI() %> <br />
servername: <%= request.getServerName() %> <br />
attributes: <%= java.util.Collections.list(request.getAttributeNames()) %> <br />
Read: <%
BufferedReader r  =  new BufferedReader(request.getReader());
String line = r.readLine();
while (line != null) {
%><%=line%><%
 line = r.readLine();
}
%>
</mm:log>
</body>
</html>
</mm:content>
