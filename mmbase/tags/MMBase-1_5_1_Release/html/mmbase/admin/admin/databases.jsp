<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Databases</title>
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="databases" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
<th class="header" colspan="5">Database Overview
</th>
</tr>
<tr>
  <td class="multidata" colspan="5">
  <p>This overview lists all database systems supported by this system, as well as
     all connection pools (which administrate the actual database connections).
  </p>
  </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Name</th>
  <th class="header">Version</th>
  <th class="header">Installed</th>
  <th class="header">Maintainer</th>
  <th class="header">&nbsp;</th>
</tr>
<%
   Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");
   NodeList databases=mmAdmin.getList("DATABASES",null,request,response);
   for (int i=0; i<databases.size(); i++) {
    Node database=databases.getNode(i);
%>
<tr>
  <td class="data"><%=database.getStringValue("item1")%></td>
  <td class="data"><%=database.getStringValue("item2")%></td>
  <td class="data"><%=database.getStringValue("item3")%></td>
  <td class="data"><%=database.getStringValue("item4")%></td>
  <td class="navigate">
    <a href="database/actions.jsp?database=<%=database.getStringValue("item1")%>"><img src="../images/next.gif" border="0" alt="next" align="right"></a>
  </td>
</tr>
<% } %>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header" colspan="2">Pool Name</th>
  <th class="header">Size</th>
  <th class="header">Connections Created</th>
  <th class="header">&nbsp;</th>
</tr>
<%
   Module jdbc=LocalContext.getCloudContext().getModule("jdbc");
   NodeList pools=jdbc.getList("POOLS",null,request,response);
   for (int i=0; i<pools.size(); i++) {
    Node pool=pools.getNode(i);
%>
<tr>
  <td class="data" colspan="2"><%=pool.getStringValue("item1")%></td>
  <td class="data"><%=pool.getStringValue("item2")%></td>
  <td class="data"><%=pool.getStringValue("item3")%></td>
  <td class="navigate">
    <a href="database/connections.jsp?pool=<%=pool.getStringValue("item1")%>"><img src="../images/next.gif" border="0" alt="next" align="right"></a>
  </td>
</tr>
<% } %>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../default.jsp" target="_top"><img src="../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="4">Return to home page</td>
</tr>
</table>
</body></html>
</mm:cloud>
