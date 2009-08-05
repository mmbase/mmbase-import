<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="text/html" expires="0" escaper="none"><%
%><html>
  <mm:import externid="refer" required="true"/>
  <mm:import externid="loginsessionname"></mm:import>
  <head>
    <meta http-equiv="refresh" content="0; url=<mm:write referid="refer" />" />
    <title>Logout, redirecting to <mm:write referid="refer" /></title>
  </head>
  <mm:cloud method="logout" sessionname="$loginsessionname" />
  <body>
    <h1>Redirecting to <a href="<mm:write referid="refer" />"><mm:write referid="refer" /></a></h1>
  </body>
</html>
</mm:content>