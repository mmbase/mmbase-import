<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Timed Email Queue Monitor</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
</head>
<body class="basic" >

<table summary="email test" width="93%" cellspacing="1" cellpadding="3" border="0">

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
  <td class="multidata" colspan="4"><p>Email builder not active</p></td>
</tr>
<% } else { %>

<tr>
  <td class="multidata" colspan="4"><p>This tool shows the performamce of the timed MMBase email builder.</p></td>
</tr>

<tr><td>&nbsp;</td></tr>
<tr>
  <td class="data" colspan="2">Max number of queued messages in memory</td>
  <td class="data" colspan="2"><%=email.getInfo("MAXMEMTASKS")%></td>
</tr>
<tr>
  <td class="data" colspan="2">Messages queued time</td>
  <td class="data" colspan="2"><%=email.getInfo("DBQUEUEDTIME")%></td>
</tr>
<tr>
  <td class="data" colspan="2">Queued probe time</td>
  <td class="data" colspan="2"><%=email.getInfo("DBQUEUEPROBETIME")%></td>
</tr>
<tr>
  <td class="data" colspan="2">Number of queued messages in database</td>
  <td class="data" colspan="2"><%=email.getInfo("DBQUEUED")%></td>
</tr>
<tr>
  <td class="data" colspan="2">Number of queued messages in memory</td>
  <td class="data" colspan="2"><%=email.getInfo("MEMTASKS")%></td>
</tr>
<tr>
  <td class="data" colspan="2">Number of messages send</td>
  <td class="data" colspan="2"><%=email.getInfo("NUMBEROFMAILSEND")%></td>
</tr>
<tr>
  <td class="data">Show first 500 entry's of the queue</td>
  <td class="navigate">
    <a href="email/emailqueue.jsp"><img src="../images/next.gif" alt="next" border="0" align="right"></a>
  </td>
</tr>

<% } %>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../default.jsp" target="_top"><img src="../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="3">Return to home page</td>
</tr>
</table>
</body></html>
</mm:cloud>
