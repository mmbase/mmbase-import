<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" logon="admin">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Modules</title>
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="modules" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
<th class="header" colspan="5">Module Overview
</th>
</tr>
<tr>
  <td class="multidata" colspan="5">
  <p>This overview lists all modules known to this system.
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
   NodeList modules=mmAdmin.getList("MODULES",null,request,response);
   for (int i=0; i<modules.size(); i++) {
    Node module=modules.getNode(i);
%>
<tr>
  <td class="data"><%=module.getStringValue("item1")%></td>
  <td class="data"><%=module.getStringValue("item2")%></td>
  <td class="data"><%=module.getStringValue("item3")%></td>
  <td class="data"><%=module.getStringValue("item4")%></td>
  <td class="navigate">
    <a href="module/actions.jsp?module=<%=module.getStringValue("item1")%>"><img src="../images/next.gif" border="0" alt="next" align="right"></a>
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
