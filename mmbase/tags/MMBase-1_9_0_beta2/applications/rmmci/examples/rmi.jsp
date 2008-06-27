<%@ page import="org.mmbase.util.logging.Logging"%>
<%@ page import="org.mmbase.module.RemoteMMCI"%>
<%@ page import="org.mmbase.module.core.MMBase"%>
<%@ page import="java.rmi.registry.Registry"%>

<html>
<head>
    <link href="../style.css" type="text/css" rel="stylesheet"/>
    <title>RMI</title>
</head>
    <body>
       <h2>RMI</h2>
<%  
	RemoteMMCI rmmci = (RemoteMMCI) MMBase.getModule("rmmci");
	String[] names = rmmci.getListOfNames(rmmci.getHost(), rmmci.getPort());
	for(int i = 0; i < names.length; i++) {
%><%= names[i] %><br /><%  
	}
 %>

<form method="post">
	<input type="hidden" name="remotereset" value="<%= rmmci.getBindName() %>"/>
	<%= rmmci.getBindName() %>
	<input type="submit" value="register again"/>
</form>

<form method="post">
	<input type="hidden" name="remotetest" value="<%= rmmci.getBindName() %>"/>
	<%= rmmci.getBindName() %>
	<input type="submit" value="test"/>
</form>

<%
String remotereset = request.getParameter("remotereset");
if (remotereset != null) {
	rmmci.resetBind(rmmci.getHost(), rmmci.getPort(), remotereset);
%>
      Reset Done!
<% } %>
<%
String remotetest = request.getParameter("remotetest");
if (remotetest != null) {
%>
Test: <%= rmmci.test(rmmci.getHost(), rmmci.getPort(), remotetest)  %><br />
      Done!
<% } %>
   </body>
</html>