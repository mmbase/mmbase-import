<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@include file="../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Servers</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="servers">
<tr>
<th class="header" colspan="7">Server Overview
</th>
</tr>
<tr>
  <td class="multidata" colspan="7"><p>This overview describes all MMBase servers running on this MMBase system.</p></td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
    <th class="header">Machine</th>
    <th class="header">State</th>
    <th class="header">Last Seen</th>
    <th class="header">Uptime</th>
    <th class="header">Host</th>
    <th class="header">OS</th>
    <th class="navigate">Manage</th>
</tr>
<mm:listnodes type="mmservers" >
<mm:context>
<tr>
    <td class="data">
    <mm:field name="name" />
    </td>
    <td class="data">
    <mm:field name="showstate" />
    </td>
    <td class="data">
    <mm:field name="showatime" />
    </td>
    <td class="data">
    <mm:field name="uptime" />
    </td>
    <td class="data">
    <mm:field name="host" />
    </td>
    <td class="data">
    <mm:field name="os" />
    </td>
    <td class="navigate" width="14">
        <mm:field name="name" id="server">
        <a href="<mm:url page="server/actions.jsp?server=$server" />"><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="next" border="0" /></a>
        </mm:field>
    </td>
</tr>
</mm:context>
</mm:listnodes>
<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="6">Return to home page</td>
</tr>
</table>
</body></html>
</mm:cloud>
