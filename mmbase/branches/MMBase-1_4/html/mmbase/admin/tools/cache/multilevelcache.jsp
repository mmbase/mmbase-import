<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.Hashtable" %>
<mm:cloud name="mmbase">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Cache Monitor, Multi Level Cache</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
</head>
<body class="basic" >

<table summary="applications" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="4">Cache Monitor - v1.0</th>
</tr>
<tr>
  <td class="multidata" colspan="4">Multi level cache - first 500 entries</td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Position</th>
  <th class="header">Count</th>
  <th class="header">Key</th>
  <th class="header">Query</th>
</tr>
<%
   Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");
   Hashtable params= new Hashtable();
   params.put("MAX","1000");
   NodeList cache=mmAdmin.getList("MULTILEVELCACHEENTRIES",params,request,response);
   for (int i=0; i<cache.size(); i++) {
    Node cachenode=cache.getNode(i);
%>
<tr>
  <td class="data"><%=i%></td>
  <td class="data"><%=cachenode.getStringValue("item8")%></td>
  <td class="data"><%=cachenode.getStringValue("item1")%></td>
  <td class="data"><%=cachenode.getStringValue("item7")%></td>
</tr>
<% } %>
<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../cache.jsp"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="3">Return to Cache Monitor</td>
</tr>
</table>
</body></html>
</mm:cloud>
