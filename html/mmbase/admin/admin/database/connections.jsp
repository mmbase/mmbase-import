<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
<% String database = request.getParameter("database"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Database Connections</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="databases">
<tr>
  <th class="header">Connection</th>
  <th class="header">Database</th>
  <th class="header">State</th>
  <th class="header">Last Query</th>
  <th class="header">Query #</th>
</tr>
<%
   Module jdbc=ContextProvider.getDefaultCloudContext().getModule("jdbc");
   java.util.Map params = new java.util.Hashtable();
   params.put("CLOUD", cloud);
   NodeList connections=jdbc.getList("CONNECTIONS",params,request,response);
   for (int i=0; i<connections.size(); i++) {
    Node connection=connections.getNode(i);
%>
<tr>
  <td class="data"><%=i%></td>
  <td class="data"><%=connection.getStringValue("item1")%></td>
  <td class="data"><%=connection.getStringValue("item2")%></td>
  <td class="data"><%=connection.getStringValue("item3")%></td>
  <td class="data"><%=connection.getStringValue("item4")%></td>
</tr>
<% } %>
<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../databases.jsp"/>"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="4">Return to Database Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
