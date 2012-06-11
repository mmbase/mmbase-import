<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@include file="../../settings.jsp" %>
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

<table summary="applications">

<mm:node number='<%=request.getParameter("msg")%>' id="msg">
<tr>
  <th class="header" colspan="4">Dynamic &amp; Timed Email System - Queue Monitor - v1.0</th>
</tr>
<tr>
  <td class="multidata" colspan="4">Full email content</td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
  <th class="header">Mail property</th>
  <th class="header">Value</th>
</tr>
<tr>
  <td class="data">Title</td>
  <td class="data"><mm:field name="title" /></td>
</tr>
<tr>
  <td class="data">Subject</td>
  <td class="data"><mm:field name="subject" /></td>
</tr>
<tr>
  <td class="data">To</td>
  <td class="data"><mm:field name="to" /></td>
</tr>
<tr>
  <td class="data">From</td>
  <td class="data"><mm:field name="from" /></td>
</tr>
<tr>
  <td class="data">Mail Type</td>
  <td class="data">
    <% int mailtype=msg.getIntValue("mailtype");
    %>
    <% if (mailtype==1) { %> onehsot
    <% } else if (mailtype==2) { %> repeat mail
    <% } else if (mailtype==3) { %> oneshot and keep
    <% } else { %> unknown
    <% } %>
    </td>
</tr>
<tr>
  <td class="data">Mail Time</td>
  <td class="data"><mm:field name="timesec(mailtime)" /></td>
</tr>

<tr>
  <td class="data">(Last) Mailed At</td>
  <td class="data"><mm:field name="timesec(mailedtime)" /></td>
</tr>

<tr>
  <td class="data">Repeat time in seconds</td>
  <td class="data">
    <% int reptime=msg.getIntValue("repeattime");
    %><%=reptime%>
    <% if (reptime==60) { %> (every minute)
    <% } else if (reptime==3600) { %> (every hour)
    <% } else if (reptime==86400) { %> (dayly)
    <% } else if (reptime==604800) { %> (weekly)
    <% } %>
  </td>
</tr>
<tr><td>&nbsp;</td></tr>

<tr>
  <th class="header" colspan="2">Body</th>
</tr>
<tr>
  <td class="multidata" colspan="2"><mm:field name="html(body)" /></td>
</tr>

<tr><td>&nbsp;</td></tr>

</mm:node>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../email.jsp" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" >Return to Email Monitor</td>
</tr>
</table>
</body></html>
</mm:cloud>
