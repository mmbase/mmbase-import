<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Databases</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="databases">
<tr>
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
<tr>
  <th class="header">Name</th>
  <th class="header">Version</th>
  <th class="header">Installed</th>
  <th class="header">Maintainer</th>
  <th class="navigate">View</th>
</tr>
<%
   Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
   java.util.Map params = new java.util.Hashtable();
   params.put("CLOUD", cloud);

   NodeList databases=mmAdmin.getList("DATABASES",params,request,response);
   for (int i=0; i<databases.size(); i++) {
    Node database=databases.getNode(i);
%>
<tr>
  <td class="data"><%=database.getStringValue("item1")%></td>
  <td class="data"><%=database.getStringValue("item2")%></td>
  <td class="data"><%=database.getStringValue("item3")%></td>
  <td class="data"><%=database.getStringValue("item4")%></td>
  <td class="navigate">
    <a href="<mm:url page="<%="database/actions.jsp?database="+database.getStringValue("item1")%>" />"><img src="<mm:url page="/mmbase/style/images/search.gif" />" border="0" alt="view" /></a>
  </td>
</tr>
<% } %>

<tr><td>&nbsp;</td></tr>

<tr>
  <th class="header" colspan="2">Pool Name</th>
  <th class="header">Size</th>
  <th class="header">Connections Created</th>
  <th class="navigate">View</th>
</tr>
<%
   Module jdbc=ContextProvider.getDefaultCloudContext().getModule("jdbc");
   NodeList pools=jdbc.getList("POOLS",params,request,response);
   for (int i=0; i<pools.size(); i++) {
    Node pool=pools.getNode(i);
%>
<tr>
  <td class="data" colspan="2"><%=pool.getStringValue("item1")%></td>
  <td class="data"><%=pool.getStringValue("item2")%></td>
  <td class="data"><%=pool.getStringValue("item3")%></td>
  <td class="navigate">
    <a href="<mm:url page="<%="database/connections.jsp?pool="+pool.getStringValue("item1")%>"/>"><img src="<mm:url page="/mmbase/style/images/search.gif" />" border="0" alt="next" /></a>
  </td>
</tr>
<% } %>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
    <td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
    <td class="data" colspan="4">Return to home page</td>
  </tr>
</table>
</body></html>
</mm:cloud>
