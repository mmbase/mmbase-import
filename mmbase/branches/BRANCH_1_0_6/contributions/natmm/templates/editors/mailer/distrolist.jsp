<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<mm:import externid="groups">-1</mm:import>
<html>
<head>
	<title>MMBase Mailer</title>
	<link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
	<link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
	<link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
</head>
<body>
<table>
	<tr>
		<th class="main" colspan="4"><div align="center">MMBase Mailer</div></th>
	</tr>
	<tr>
		<th class="main" colspan="4"><div align="center">Mail Adresses in 
			<mm:list nodes="$groups" path="deelnemers_categorie" orderby="deelnemers_categorie.naam" directions="UP">
				<mm:first inverse="true">, </mm:first>
				"<mm:field name="deelnemers_categorie.naam" />"
			</mm:list>
		</div></th>
	</tr>
	<mm:list nodes="$groups" path="deelnemers_categorie,related,deelnemers" orderby="deelnemers.lastname">
		<tr>
			<td width="70%"><mm:field name="deelnemers.lastname" /></td>
			<td width="15%"><mm:field name="deelnemers.firstname" /></td>
			<td width="15%"><mm:field name="deelnemers.suffix" /></td>
			<td width="15%"><mm:field name="deelnemers.email" /></td>
		</tr>
	</mm:list>
</table>
<div class="link">
<a href="javascript:self.close();"><img alt="close this window" src="<mm:url page="/mmbase/style/images/delete.gif" />" /></a>
</div>
</body>
</html>
</mm:cloud>
