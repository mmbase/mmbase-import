<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Cache Monitor, Node Cache</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
</head>
<body class="basic" >

<table summary="applications" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="5">Cache Monitor - v1.0</th>
</tr>
<tr>
  <td class="multidata" colspan="5">Node cache - first 1000 entries</td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Position</th>
  <th class="header">Count</th>
  <th class="header">Number</th>
  <th class="header">Owner</th>
  <th class="header">Type</th>
</tr>
<%
   Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");
   Hashtable params= new Hashtable();
   params.put("MAX","500");
   NodeList cache=mmAdmin.getList("NODECACHEENTRIES",params,request,response);
   for (int i=0; i<cache.size(); i++) {
    Node cachenode=cache.getNode(i);
%>
<tr>
  <td class="data"><%=i%></td>
  <td class="data"><%=cachenode.getStringValue("item1")%></td>
  <td class="data"><%=cachenode.getStringValue("item2")%></td>
  <td class="data"><%=cachenode.getStringValue("item3")%></td>
  <td class="data"><%=cachenode.getStringValue("item4")%></td>
</tr>
<% } %>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../cache.jsp"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="4">Return to Cache Monitor</td>
</tr>
</table>
</body></html>
</mm:cloud>
