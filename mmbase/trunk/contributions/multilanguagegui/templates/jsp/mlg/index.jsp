<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="css/mmbase-dev.css" />
   <title>MMBase forums</title>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</head>
<mm:cloud logon="admin" method="http">
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="75%">
	<tr>
	  <th colspan="3">Multilanguage GUI editor 0.2</th>
	</tr>
	<mm:include page="path.jsp?type=index" />
	<tr>
	<form action="set.jsp" method="post">
	<th>select set</th>
	<td align="middle">
		<select name="setname">
		<mm:function set="mlg" name="getSets">
		  <mm:resultnodes>
			<option><mm:field name="name" />
		  </mm:resultnodes>
		</mm:function>
		</select>
		
	</td>
	<td align="middle">
		<input type="submit" value="change" />
	</td>
	</form>
	</tr>

	<tr>
	<form action="index.jsp">
	<th>create set</th>
	<td align="middle">
		<input size="20" name="newset" />
	</td>
	<td align="middle">
		<input type="submit" value="create" />
	</td>
	</form>
	</tr>
</table>
</center>
</mm:cloud>
</html>
