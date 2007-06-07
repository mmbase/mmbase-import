<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud>
<% 
String nodeNumber = request.getParameter("node"); 
String imageNode;
String kartName = "";
String kartType = "";
String kartOpmerking = "";
%>
<mm:node number="<%=nodeNumber%>">
	<mm:field name="naam" jspvar="name" write="false" vartype="String" ><% kartName = name; %></mm:field>
	<mm:field name="type_gebied" jspvar="type" write="false" vartype="String" ><% kartType = type; %></mm:field>
	<mm:field name="opmerking" jspvar="opmerking" write="false" vartype="String" ><% kartOpmerking = opmerking; %></mm:field>
	
	<%--
	<mm:related>
	</mm:related>
	--%>
</mm:node>


<html>
<head>
<title><%=kartType%>:<%=kartName%></title>
</head>
<body>



Naam van de kaart: <b><%=kartName%></b> <a href="javascript:window.close()">close</a> 
<br>
Informatie betreffende de kaart uit het opmerkingen veld: <br/>
<%=kartOpmerking%>
<br/>

<img src="../../media/vastgoed/Schelde.jpg" border="0">













</body>
</html>
</mm:cloud>