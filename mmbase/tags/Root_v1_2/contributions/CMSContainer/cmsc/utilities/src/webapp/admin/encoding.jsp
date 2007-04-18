<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="sun.io.Converters"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>

<%
	try {
			request.setCharacterEncoding("UTF-8");
	}
	catch (Exception e) {
		%>
		<%= e.toString() %>
		<%
	}
%>
	
<mm:cloud>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>

File.encoding: <%=System.getProperty("file.encoding") %><br/>
OS encoding: <%= Converters.getDefaultEncodingName() %><br/>
Bytes received: <% 
	if (request.getParameter("intro") != null) {
		byte[] bytes = request.getParameter("intro").getBytes("UTF-8");
		for (int i = 0; i < bytes.length; i++) {
			%><%= Integer.toString(bytes[i]) %> <%
		}
	}
%><br/>
request: <%= request.getParameter("intro") %><br/>

<mm:import externid="intro" from="parameters"/>
<mm:present referid="intro">
MMBase:	<mm:write referid="intro" /><br/>
</mm:present>

<form method="post">
	<input type="text" name="intro" value=""/>
	<input type="submit" />
</form>
</body>
</html>

</mm:cloud>