<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" logon="admin">
<% String database = request.getParameter("database"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Database <%=database%></title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="database actions" width="93%" cellspacing="1" cellpadding="3">
<%
   Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");
%>
<tr align="left">
 <th class="header" colspan="5">Description of <%=database%></th>
</tr>
<tr>
 <td class="multidata" colspan="5">
        <p>In one of the next MMBase-releases you'll find here detailed information about
            the <%=database%>-databasemodule.</p>&nbsp;
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<%
    Module config=LocalContext.getCloudContext().getModule("config");
    if (config!=null) {
        String check=config.getInfo("CHECK-databases-"+database);
%>
<form action="../config/details.jsp" method="POST" target="_xml">
<tr align="left">
<th class="header">Action</th>
  <th class="header" colspan="3">Status</th>
  <th class="header" >View</th>
</tr>
<tr>
 <td class="data">XML-check</td>
 <td class="data" colspan="3"><%=check%></td>
 <td class="linkdata" >
<%    if (check.equals("Checked ok")) { %>
        <input type="hidden" name="todo" value="show" />
<%  } else { %>
        <input type="hidden" name="todo" value="annotate" />
<%  } %>
    <input type="hidden" name="config" value="databases" />
    <input type="hidden" name="target" value="<%=database%>" />
    <input type="submit" value="YES" />
 </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<% } %>

<tr>
<td class="navigate"><a href="../databases.jsp"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="4">Return to Database Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
