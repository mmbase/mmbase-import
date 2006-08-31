<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" method="http">
<%@ include file="thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBase forums</title>
</head>
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
		<mm:nodelistfunction set="mlg" name="getSets">
			<option><mm:field name="name" />
		</mm:nodelistfunction>
		</select>
		
	</td>
	<td align="middle">
		<input type="submit" value="change" />
	</td>
	</form>
	</tr>

	<tr>
	<form action="index.jsp" method="post">
	<th>create set</th>
	<td align="middle">
		<input size="20" name="setname" />
	</td>
	<td align="middle">
                <input name="action" type="hidden" value="addset" />
		<input type="submit" value="create" />
	</td>
	</form>
	</tr>
</table>
</center>
</mm:cloud>
</html>
