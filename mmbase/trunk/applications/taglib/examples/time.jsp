<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<head>
<title>	MMBase time tag</title> 
<link href="style.css" rel="stylesheet" type="text/css" media="screen"  />
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
<table>
<tr><td colspan="2">Show the time in seconds from the start of the epoc</td></tr>
<tr valign="top">
  <td width="50%"><pre>&lt;mm:time /&gt;</pre></td>
  <td width="50%"><mm:time /></td>
</tr>
<tr><td colspan="2">Show the time in seconds from EPOC and format it in a nice way</td></tr>
<tr valign="top">
  <td width="50%"><pre>&lt;mm:time format="EEEE d MMMM" /&gt;</pre></td>
  <td width="50%"><mm:time format="EEEE d MMMM" /></td>
</tr>
<tr><td colspan="2">You can use keywords such as: yesterday, tomorrow, today, now (not the beginning of the day), the days of the week, and the months of the year, to show the time. The tag will always show the beginning of the day (resp. month).</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/timetime.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/timetime.jsp" %></td>
</tr>
</table>
<ol>
<%--
<li>
Show the time in different languages.<br />
&lt;mm:time format="EEEE d MMMM" language="en" />
<mm:time format="EEEE d MMMM" language="en" /><br />
&lt;mm:time format="EEEE d MMMM" language="es" />
<mm:time format="EEEE d MMMM" language="es" /><br />
&lt;mm:time format="EEEE d MMMM" language="de" />
<mm:time format="EEEE d MMMM" language="de" /><br />
&lt;mm:time format="EEEE d MMMM" language="fr" />
<mm:time format="EEEE d MMMM" language="fr" />
</li>
--%>
<li>

<mm:time time="tomorrow" format="yyyy/MM/dd HH:mm:ss" /><br />
&lt;mm:time time="saturday" format="yyyy/MM/dd HH:mm:ss" />" 
<mm:time time="saturday" format="yyyy/MM/dd HH:mm:ss" /><br />
&lt;mm:time time="july" format="yyyy/MM/dd HH:mm:ss" />" 
<mm:time time="july" format="yyyy/MM/dd HH:mm:ss" /><br />
</li>
<li>
You can also give the time in a human readable way, the following three cases will be parsed automatically:<br />
<ul>
<li>yyyy/mm/dd, shows the beginning of that day.</li>
<li>hh:mm:ss, shows the current day with given time.</li>
<li>yyyy/mm/dd hh:mm:ss, shows exactly the given time.</li>
</ul>
&lt;mm:time time="<mm:time id="ymd" format="yyyy/MM/dd" />" />
<mm:time referid="ymd" />
</li>
<li>
If the time is given in another format use the input format. Use <a href="http://java.sun.com/j2se/1.3/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a> to figure out the wanted format.<br />
&lt;mm:time time="2000/11/01" inputformat="y/M/d" />
<mm:time time="2000/11/01" inputformat="y/M/d" />
</li>
<li>
Use the offset attribute if you want to change the time. This example adds 3 days (259200 seconds) to the given time.<br />
&lt;mm:time time="now" offset="259200" format="EEEE d MMM yy" />
<mm:time time="now" offset="259200" format="EEEE d MMM yy" />
</li>
<li>
<p>a.<br />
Display yesterday and save it as a jspvar:<br />
&lt;mm:time time="yesterday" format="EEEE d MMMM" jspvar="yesterday" write="true" &gt; ... body ... &lt/mm:time&gt;
<mm:time time="yesterday" format="EEEE d MMMM" jspvar="yesterday" write="true" >
<mm:time time="tomorrow" format="EEEE d MMMM" jspvar="tomorrow">
</p>
<p>b. <br />
Use the jspvars in your page:<br />
I want to see all movies between &lt;%=yesterday%> and &lt;%=tomorrow%>.<br />
I want to see all movies between <%=yesterday%> and <%=tomorrow%>.
</mm:time>
</mm:time>
</p>
</li>
<li>
Display a field of a node that is a time:<br />
        &lt;mm:field name="mmevents.start" ><br />
               &lt;mm:time format="yyyy MM dd" /><br />
        &lt;/mm:field><br />
2002 03 12
</li>
<li>
<p>a. <br />
Set the time in the context:<br />
&lt;mm:time id="time" time="2002/04/05" format="EEEE d MMMM yyyy" write="false"/>
<mm:time id="time" time="2002/04/05" format="yyyy/MM/dd" write="false"/>
</p>
<p>b. <br />
Reuse the time:<br />
&lt;mm:time referid="time" format="EEEE d MMMM yyyy" />
<mm:time referid="time" format="EEEE d MMMM yyyy"  />
</p>
</li>
</ol>
</mm:cloud>
</body>
</html>
