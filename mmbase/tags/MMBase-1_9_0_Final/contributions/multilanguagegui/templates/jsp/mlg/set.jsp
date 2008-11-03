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
<mm:import externid="wantedkeyword"></mm:import>
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->
<body onLoad="javascript:self.focus();document.edit.keyword.focus()">
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="75%">
	<tr>
	  <th colspan="3">Multilanguage GUI editor 0.2</th>
	</tr>
	<mm:include page="path.jsp?type=set&setname=$setname" />

        <tr>
        <form action="keyword.jsp" name="edit">
        <th>create keyword</th>
        <td align="middle">
                <input size="20" name="keyword"  value="<mm:write referid="wantedkeyword" />" />
        </td>
        <td align="middle">
                <input type="submit" value="create" />
        </td>
        <input name="action" type="hidden" value="addkeyword" />
        <input name="setname" type="hidden" value="<mm:write referid="setname" />" />
        </form>
        </tr>


	<mm:nodelistfunction set="mlg" name="getKeywords" referids="setname">
	<tr>
	    <th align="middle">
		<mm:field name="name" />
	    </th>
	<form action="keyword.jsp">
	<td align="middle" width="25%">
		<input type="submit" value="change" />
		<input name="keyword" type="hidden" value="<mm:field name="name" />" />
		<input name="setname" type="hidden" value="<mm:write referid="setname" />" />
	</td>
	</form>
	<form action="set.jsp">
	<td align="middle" width="25%">
		<input type="submit" value="remove" />
		<input name="keyword" type="hidden" value="<mm:field name="name" />" />
		<input name="setname" type="hidden" value="<mm:write referid="setname" />" />
		<input name="action" type="hidden" value="removekeyword" />
	</td>
	</form>
	</tr>
	</mm:nodelistfunction>

</table>
</center>
</mm:cloud>
</body>
</html>
