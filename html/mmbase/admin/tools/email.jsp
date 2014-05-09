<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@include file="../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator" jspvar="cloud">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Timed Email Queue Monitor</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >

<table summary="email test">

<%
    NodeManager email=null;
    try {
       email=cloud.getNodeManager("email");
    } catch (NotFoundException e) {}
%>

<tr>
  <th class="header" colspan="4">Dynamic &amp; Timed Email System - Queue Monitor - v1.0</th>
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
  <td class="data" colspan="3">Max number of queued messages in memory</td>
  <td class="data" ><%=email.getInfo("MAXMEMTASKS")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Messages queued time</td>
  <td class="data" ><%=email.getInfo("DBQUEUEDTIME")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Queued probe time</td>
  <td class="data" ><%=email.getInfo("DBQUEUEPROBETIME")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Number of queued messages in database</td>
  <td class="data" ><%=email.getInfo("DBQUEUED")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Number of queued messages in memory</td>
  <td class="data" ><%=email.getInfo("MEMTASKS")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Number of messages send</td>
  <td class="data" ><%=email.getInfo("NUMBEROFMAILSEND")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Show first 500 entry's of the queue</td>
  <td class="navigate">
    <a href="<mm:url page="email/emailqueue.jsp" />" ><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="view" border="0" /></a>
  </td>
</tr>

<% } %>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="3">Return to home page</td>
</tr>
</table>
</body></html>
</mm:cloud>
