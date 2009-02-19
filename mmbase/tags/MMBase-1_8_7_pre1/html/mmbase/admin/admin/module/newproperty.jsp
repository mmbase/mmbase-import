<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@include file="../../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<% String module = request.getParameter("module"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Module <%=module%>, New Property</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="module property data">

<form action="<mm:url page="actions.jsp" />" method="POST">
<tr>
  <th class="header">Property</th>
  <th class="header">Value</th>
  <th class="navigate">Create</th>
</tr>
<tr>
 <td class="data"><input type="text" name="property" value="" /></td>
 <td class="data">
    <input type="text" name="value" value="" />
</td>
<td class="linkdata">
    <input type="hidden" name="module" value="<%=module%>" />
    <input type="hidden" name="cmd" value="MODULE-SETPROPERTY" />
    <input type="image" src="<mm:url page="/mmbase/style/images/create.gif" />" alt="Create" border="0"  />
</td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="<%="actions.jsp?module="+module%>"/>"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="3">Return to Module Administration</td>
</tr>
</table>
</body></html>
</mm:cloud>