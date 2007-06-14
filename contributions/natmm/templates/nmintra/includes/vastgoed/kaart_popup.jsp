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

Naam van de kaart: <b><%=kartName%></b> 
&nbsp;&nbsp;&nbsp;<a href="javascript:window.close()"><img border="0" src="../../media/vastgoed/close.gif"/> close</a> 
<br>
Informatie betreffende de kaart uit het opmerkingen veld: <br/>
<mm:field name="opmerking"/>
<br/>

	<mm:relatednodes type="images" max="1">
		<img src="<mm:image template="s(480x480)" />" border="0">
	</mm:relatednodes>
	
</body>
</html>

</mm:node>
</mm:cloud>