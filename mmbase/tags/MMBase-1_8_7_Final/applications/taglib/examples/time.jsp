<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page errorPage="error.jsp" session="false" language="java" contentType="text/html; charset=UTF-8" import="java.util.*" %>
<mm:content type="text/html" language="client">
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
<a href="<mm:url page="$taglibdoc/reference/time.jsp" />">Taglib documentation</a>.
</p>
<mm:log />
<table>
<tr><th colspan="2">Try it your self</th></tr>
<tr valign="top">
  <mm:import externid="time">now</mm:import>
  <td width="50%">
    <form method="get">
      <input name="time" value="<mm:write referid="time" />" />
      <input type="submit" />
    </form>
  <td width="50%">
    <% try { %>
    <mm:time time="${time}" format=":FULL.FULL" />
    <% } catch (Exception e) { %>
      <%= e.getMessage() %>
    <% } %>
  </td>
</tr>
<tr><th colspan="2">Show the time in seconds from the start of the epoch</th></tr>
<tr valign="top">
  <td width="50%"><pre>&lt;mm:time time="now" /&gt;</pre></td>
  <td width="50%"><mm:time time="now"/></td>
</tr>
<tr><td colspan="2">Show the time in seconds from the epoch and format it in a nice way</td></tr>
<tr valign="top">
  <td width="50%"><pre>&lt;mm:time time="now" format="EEEE d MMMM" /&gt;</pre></td>
  <td width="50%"><mm:time time="now" format="EEEE d MMMM" /></td>
</tr>
<tr><th colspan="2"> Show the time in different languages and formats. If the format starts with a
colon, localized date formats can be used with constants FULL, LONG, MEDIUM and SHORT. Otherwise it
is a pattern for SimpleDateFormat.  </th></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/timelocale.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timelocale.jsp" %></td>
</tr>
<tr><th colspan="2"> You can use keywords such as: yesterday,
        tomorrow, today, now (not the beginning of the day), the days
        of the week, and the months of the year, to show the time. The
        tag will always show the beginning of the day
        (resp. month). You can also give the time in a human readable
        way, and optionally give the format for that with 'inputformat'
   (see <a href="http://java.sun.com/j2se/1.4/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>)
</th></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/timetime.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timetime.jsp" %></td>
</tr>
<tr><th colspan="2"> 
Use the offset attribute if you want to change the time. 
</th></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/timeoffset.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timeoffset.jsp" %></td>
</tr>
<tr><th colspan="2"> 
How to use jspvars and taglib vars.
</th></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/timeenv.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timeenv.jsp" %></td>
</tr>
<tr><th colspan="2"> 
Using a field of a node that is a time. The time tag is working as a writerreferrer then.
</th></tr>
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
<tr><th colspan="2"> 
A more generic demonstration of the 'Writer' and 'WriterReferrer' properties of the time tag can be like this.
</th></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/timewriter.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timewriter.jsp" %></td>
</tr>
<tr><th colspan="2"> 
examples of more elaborate time notations
</th></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/time.parser.jsp" /></mm:formatter></pre></td>
  <td width="50%"><pre><%@include file="codesamples/time.parser.jsp" %></pre></td>
</tr>
</table>
</mm:cloud>
</body>
</html>
</mm:content>
