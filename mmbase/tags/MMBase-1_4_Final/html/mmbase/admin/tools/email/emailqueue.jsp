<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.Hashtable" %>
<mm:cloud name="mmbase">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Timed email Queue Monitor</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
</head>
<body class="basic" >

<table summary="applications" width="93%" cellspacing="1" cellpadding="3" border="0">

<%
    NodeManager email=null;
    try {
       email=cloud.getNodeManager("email");
    } catch (Exception e) {}
%>

<tr align="left">
  <th class="header" colspan="4">Dynamic & Timed Email System - Queue Monitor - v1.0</th>
</tr>
<% if (email==null) { %>
<tr>
  <td class="multidata" colspan="4"><p>Email builder not available</p></td>
</tr>
<% } else { %>

<tr>
  <td class="multidata" colspan="4">Email queue - first 500 entries</td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
  <td class="header" colspan="4">First (1000 max) messages into the memory queue
</td>
</tr>
<tr>
  <th class="header">second to mail</th>
  <th class="header">to</th>
  <th class="header">from</th>
  <th class="header">subject</th>
</tr>

<%
   Hashtable params= new Hashtable();
   params.put("MAX","1000");
   NodeList msgs=email.getList("MEMTASKS",params);
   for (int i=0; i<msgs.size(); i++) {
    Node msg=msgs.getNode(i);
%>
<tr>
  <td class="data"><%=msg.getValue("item2")%></td>
  <td class="data"><%=msg.getValue("item3")%></td>
  <td class="data"><%=msg.getValue("item4")%></td>
  <td class="data"><a href="email/fullmail.jsp?msg=<%=msg.getValue("item1")%>"><%=msg.getValue("item5")%></a></td>
</tr>
<%   } %>
<% } %>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../email.jsp"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="3">Return to Email Monitor</td>
</tr>
</table>
</body></html>
</mm:cloud>
