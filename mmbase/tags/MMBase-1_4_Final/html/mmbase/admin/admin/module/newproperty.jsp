<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<% String module = request.getParameter("module"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Module <%=module%>, New Property</title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="module property data" width="93%" cellspacing="1" cellpadding="3">
<tr align="left">
  <th class="header">Property</th>
  <th class="header">Value</th>
  <th class="header">Change</th>
</tr>

<form action="actions.jsp" method="POST">
<tr>
  <td class="data"><input type="text" name="property" value="" /></td>
 <td class="data">
    <input type="text" name="value" value="" />
</td>
<td class="linkdata">
    <input type="hidden" name="module" value="<%=module%>" />
    <input type="hidden" name="cmd" value="MODULE-SETPROPERTY" />
    <input type="submit" value="Change" />
</td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="actions.jsp?module=<%=module%>"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="3">Return to Module Administration</td>
</tr>
</table>
</body></html>
</mm:cloud>
