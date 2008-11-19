<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<%@page import="org.mmbase.util.LRUHashtable.LRUEntry" %>

<mm:cloud jspvar="cloud" loginpage="login.jsp" rank="administrator">
<c:set var="paramKeys" value="<%=request.getParameterMap().keySet()%>"/>
<c:forEach var="paramValue" items="${paramKeys}">
	<mm:deletenode number="${paramValue}" deleterelations="true"/>
</c:forEach>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>View/layout/portlet usage</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link href="../style.css" type="text/css" rel="stylesheet"/>
<script>
	function confirmForm() {
		// set var checkbox_choices to zero
		var checkbox_choices = 0;
		var form = document.delete_form;

		// Loop from zero to the one minus the number of checkbox button selections
		for (counter = 0; counter < form.length; counter++) {

			if (form[counter].checked){ 
				checkbox_choices++;
			}
		}
		return confirm("Are you sure you want to delete "+checkbox_choices+" items");
	}
</script>
</head>
<body class="basic" >
<form id="delete_form" name="delete_form" method="post" onsubmit="return confirmForm()">
<table>
	<tr>
		<th></th>
		<th>Layout</th>
		<th>Pages</th>
	<tr>
	
	<mm:listnodes type="layout">
		<tr>
			<c:set var="pages"><mm:relatednodes type="page"><mm:first><mm:size/></mm:first></mm:relatednodes></c:set>
			
			<td><input type="checkbox" name="<mm:field name="number"/>" ${(empty pages)?"checked":""}/></td>
			<td><mm:field name="title"/></td>
			<td>${pages}</td>
		</tr>
	</mm:listnodes>
</table>

<table>
	<tr>
		<th></th>
		<th>Views</th>
		<th>Portlets</th>
		<th>Portletdefs</th>
		<th>Pages</th>
	<tr>
	
	<mm:listnodes type="view">
		<tr>
			<c:set var="pages"><mm:relatednodes path="portlet,page"><mm:first><mm:size/></mm:first></mm:relatednodes></c:set>
			
			<td><input type="checkbox" name="<mm:field name="number"/>" ${(empty pages)?"checked":""}/></td>
			<td><mm:field name="title"/></td>
			<td><mm:relatednodes type="portlet"><mm:first><mm:size/></mm:first></mm:relatednodes></td>
			<td><mm:relatednodes type="portletdefinition"><mm:first><mm:size/></mm:first></mm:relatednodes></td>
			<td>${pages}</td>
		</tr>
	</mm:listnodes>
</table>

<table>
	<tr>
		<th></th>
		<th>Portlets</th>
		<th>Views</th>
		<th>Pages</th>
	<tr>
	
	<mm:listnodes type="portlet">
		<tr>
			<c:set var="pages"><mm:relatednodes type="page"><mm:first><mm:size/></mm:first></mm:relatednodes></c:set>

			<td><input type="checkbox" name="<mm:field name="number"/>" ${(empty pages)?"checked":""}/></td>
			<td><mm:field name="title"/></td>
			<td><mm:relatednodes type="view"><mm:first><mm:size/></mm:first></mm:relatednodes></td>
			<td>${pages}</td>
		</tr>
	</mm:listnodes>
</table>

<table>
	<tr>
		<th></th>
		<th>Portlet defs</th>
		<th>Portlet</th>
		<th>Layout</th>
		<th>Pages</th>
	<tr>
	
	<mm:listnodes type="portletdefinition">
		<tr>
			<c:set var="pages"><mm:relatednodes path="portlet,page"><mm:first><mm:size/></mm:first></mm:relatednodes></c:set>
			
			<td><input type="checkbox" name="<mm:field name="number"/>" ${(empty pages)?"checked":""}/></td>
			<td><mm:field name="title"/> (<mm:field name="type"/>)</td>
			<td><mm:relatednodes type="portlet"><mm:first><mm:size/></mm:first></mm:relatednodes></td>
			<td><mm:relatednodes type="layout"><mm:first><mm:size/></mm:first></mm:relatednodes></td>
			<td>${pages}</td>
		</tr>
	</mm:listnodes>
</table>
<input type="submit"/>
</form>
</body></html>
</mm:cloud>
