<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud>
<% 
String nodeNumber = request.getParameter("node"); 
String kartName = "";
String kartType = "";
%>
<mm:node number="<%=nodeNumber%>">
	<mm:field name="naam" jspvar="name" write="false" vartype="String" ><% kartName = name; %></mm:field>
	<mm:field name="type_gebied" jspvar="type" write="false" vartype="String" ><% kartType = type; %></mm:field>

<html>
<head>
<title><%=kartType%>:<%=kartName%></title>
</head>
<body>

<b><%=kartName%></b>

<br/><br/>
	<mm:relatednodes type="images" max="1">
		<img src="<mm:image template="s(920)" />" border="0">
	</mm:relatednodes>

<br/>
<mm:field name="opmerking"/>

</body>
</html>

</mm:node>
</mm:cloud>