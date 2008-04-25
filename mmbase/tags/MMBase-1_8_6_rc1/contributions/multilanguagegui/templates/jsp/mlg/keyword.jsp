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
<mm:import externid="setname" />
<mm:import externid="keyword" />
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->
<body onLoad="javascript:self.focus();document.edit.lang.focus()">
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="75%">
	<tr>
	  <th colspan="4">Multilanguage GUI editor 0.2</th>
	</tr>
	<mm:include page="path.jsp?type=keyword&setname=$setname&keyword=$keyword" />
		<mm:nodelistfunction set="mlg" name="getTranslations" referids="setname,keyword">
		<tr>
		<form action="keyword.jsp" method="post">
		<th><mm:field name="name" /></th>
		<td>
		<input name="value" value="<mm:field name="value" />" size="32" />
		</td>
		<td align="middle">
			<input name="lang" type="hidden" value="<mm:field name="name" />" />
			<input name="setname" type="hidden" value="<mm:write referid="setname" />" />
			<input name="keyword" type="hidden" value="<mm:write referid="keyword" />" />
			<input name="action" type="hidden" value="changelanguage" />
			<input type="submit" value="change" />
		</td>
		</form>
		<form action="keyword.jsp" method="post">
		<td align="middle">
			<input name="lang" type="hidden" value="<mm:field name="name" />" />
			<input name="setname" type="hidden" value="<mm:write referid="setname" />" />
			<input name="keyword" type="hidden" value="<mm:write referid="keyword" />" />
			<input name="action" type="hidden" value="removelanguage" />
			<input type="submit" value="remove" />
		</td>
		</tr>
		</form>
		</mm:nodelistfunction>

	<tr>
	<form action="keyword.jsp" method="post" name="edit">
	<th><input size="2" name="lang" /></th>
	<td>
		<input size="32" name="value" />
	</td>
	<td align="middle">
		<input type="submit" value="add" />
	</td>
	<td>
	</td>
	<input name="setname" type="hidden" value="<mm:write referid="setname" />" />
	<input name="keyword" type="hidden" value="<mm:write referid="keyword" />" />
	<input name="action" type="hidden" value="addlanguage" />
	</form>
	</tr>
</table>
</center>
</mm:cloud>
</body>
</html>
