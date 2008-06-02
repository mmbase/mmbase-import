<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<jsp:directive.page import="java.util.Set"/>
<jsp:directive.page import="java.util.TreeSet"/>
<jsp:directive.page import="org.mmbase.bridge.Node"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>DMdelivery email adressen</title>
</head>
<body>
<mm:cloud jspvar="cloud" method="http">

	<%
		Set<Integer> users = new TreeSet<Integer>();
	%>

	<mm:list path="users,usercomments" fields="users.number" orderby="users.number" distinct="true">
		<mm:first>gebruikers met een comment: <mm:size /><br /></mm:first>
		<mm:field name="users.number" jspvar="usernumber" vartype="Integer" write="false"><% users.add(usernumber); %></mm:field>
	</mm:list>

	<mm:list path="users,tags" fields="users.number" orderby="users.number" distinct="true">
		<mm:first>gebruikers met een tag: <mm:size /><br /></mm:first>
		<mm:field name="users.number" jspvar="usernumber" vartype="Integer" write="false"><% users.add(usernumber); %></mm:field>
	</mm:list>

	<mm:list path="users,userbookmarks" fields="users.number" orderby="users.number" distinct="true">
		<mm:first>gebruikers met een bookmark: <mm:size /><br /></mm:first>
		<mm:field name="users.number" jspvar="usernumber" vartype="Integer" write="false"><% users.add(usernumber); %></mm:field>
	</mm:list>

	<mm:list path="users,userprofiles" fields="users.number" orderby="users.number" distinct="true">
		<mm:first>gebruikers met een profiel: <mm:size /><br /></mm:first>
		<mm:field name="users.number" jspvar="usernumber" vartype="Integer" write="false"><% users.add(usernumber); %></mm:field>
	</mm:list>

	total aantal gebruikers: <%= users.size() %><p />

	<%
		for(Integer usernumber : users) {
		   	Node user = cloud.getNode(usernumber);
			out.println(user.getStringValue("account") + " | " + user.getStringValue("email") + "<br />");
		}
	%>


</mm:cloud>
</body>
</html>