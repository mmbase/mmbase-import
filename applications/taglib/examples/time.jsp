<%@page language="java" contentType="text/html; charset=UTF-8" %><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<head>
<title>	MMBase time tag</title> 
<link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<mm:cloud>
<%@ include file="menu.jsp"%>

<h1>Time Tag</h1>
<p>
This page contains time tag examples. For information about the
time tag attributes see the 
<a href="<mm:url page="/mmdocs/mmbase-taglib.html#time" />">Taglib documentation</a>.
</p>
<mm:log />
<table>
<tr><td colspan="2">Show the time in seconds from the start of the epoch</td></tr>
<tr valign="top">
  <td width="50%"><pre>&lt;mm:time time="now" /&gt;</pre></td>
  <td width="50%"><mm:time time="now"/></td>
</tr>
<tr><td colspan="2">Show the time in seconds from the epoch and format it in a nice way</td></tr>
<tr valign="top">
  <td width="50%"><pre>&lt;mm:time time="now" format="EEEE d MMMM" /&gt;</pre></td>
  <td width="50%"><mm:time time="now" format="EEEE d MMMM" /></td>
</tr>
<tr><td colspan="2"> Show the time in different languages and formats. If the format starts with a
colon, localized date formats can be used with constants FULL, LONG, MEDIUM and SHORT. Otherwise it
is a pattern for SimpleDateFormat.  </td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/timelocale.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timelocale.jsp" %></td>
</tr>
<tr><td colspan="2"> You can use keywords such as: yesterday,
        tomorrow, today, now (not the beginning of the day), the days
        of the week, and the months of the year, to show the time. The
        tag will always show the beginning of the day
        (resp. month). You can also give the time in a human readable
        way, and optionally give the format for that with 'inputformat'
   (see <a href="http://java.sun.com/j2se/1.3/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>)
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/timetime.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timetime.jsp" %></td>
</tr>
<tr><td colspan="2"> 
Use the offset attribute if you want to change the time. 
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/timeoffset.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timeoffset.jsp" %></td>
</tr>
<tr><td colspan="2"> 
How to use jspvars and taglib vars.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/timeenv.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timeenv.jsp" %></td>
</tr>
<tr><td colspan="2"> 
Using a field of a node that is a time.
</td></tr>
<tr valign="top">
  <td width="50%">
    <pre>
        &lt;mm:field name="mmevents.start" >
               &lt;mm:time format="yyyy MM dd" />
        &lt;/mm:field>
    </pre>
  </td>
  <td width="50%">2002 03 12</td>
</tr>
</table>
</mm:cloud>
</body>
</html>
