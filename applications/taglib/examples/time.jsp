<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<title>
	MMBase time taglib
</title> 
<body>
<mm:cloud>
<%@ include file="menu.jsp"%>

<h1>Time Taglib</h1>
<p>
This page contains time taglib examples. For information about the
time taglib attributes see the 
<a href="<mm:url page="/mmdocs/mmbase-taglib.html#time" />">Taglib documentation</a>.
</p>
<p>
1) Show the time in seconds from EPOC.<BR>
&lt;mm:time />
<mm:time />
</p>
2) Show the time in seconds from EPOC and format it in a nice way.<BR>
&lt;mm:time format="EEEE d MMMM" />
<mm:time format="EEEE d MMMM" />
</p>
<%--
<p>
3) Show the time in different languages.<BR>
&lt;mm:time format="EEEE d MMMM" language="en" />
<mm:time format="EEEE d MMMM" language="en" /><BR>
&lt;mm:time format="EEEE d MMMM" language="es" />
<mm:time format="EEEE d MMMM" language="es" /><BR>
&lt;mm:time format="EEEE d MMMM" language="de" />
<mm:time format="EEEE d MMMM" language="de" /><BR>
&lt;mm:time format="EEEE d MMMM" language="fr" />
<mm:time format="EEEE d MMMM" language="fr" />
</p>
--%>
<p>
4) You can use keywords such as: yesterday, tomorrow, today, now (not the beginning of the day), the days of the week, and the months of the year, to show the time. The taglib will always show the beginning of the day (resp. month).<BR>
&lt;mm:time time="tomorrow" format="yyyy/MM/dd HH:mm:ss" />" 
<mm:time time="tomorrow" format="yyyy/MM/dd HH:mm:ss" /><BR>
&lt;mm:time time="saturday" format="yyyy/MM/dd HH:mm:ss" />" 
<mm:time time="saturday" format="yyyy/MM/dd HH:mm:ss" /><BR>
&lt;mm:time time="july" format="yyyy/MM/dd HH:mm:ss" />" 
<mm:time time="july" format="yyyy/MM/dd HH:mm:ss" /><BR>
</p>
<p>
5) You can also give the time in a human readable way, the following three cases will be parsed automatically:<BR>
<LI>yyyy/mm/dd, shows the beginning of that day.</LI>
<LI>hh:mm:ss, shows the current day with given time.</LI>
<LI>yyyy/mm/dd hh:mm:ss, shows exactly the given time.</LI>
&lt;mm:time time="<mm:time id="ymd" format="yyyy/MM/dd" />" />
<mm:time referid="ymd" />
</p>
<p>
6) If the time is given in another format use the input format. Use <A HREF="http://java.sun.com/j2se/1.3/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</A> to figure out the wanted format.<BR>
&lt;mm:time time="2000/11/01" inputformat="y/M/d" />
<mm:time time="2000/11/01" inputformat="y/M/d" />
</p>
<p>
7) Use the offset attribute if you want to change the time. This example adds 3 days (259200 seconds) to the given time.<BR>
&lt;mm:time time="now" offset="259200" format="EEEE d MMM yy" />
<mm:time time="now" offset="259200" format="EEEE d MMM yy" />
</p>
<p>
8a) Display yesterday and save it as a jspvar:<BR>
&lt;mm:time time="yesterday" format="EEEE d MMMM" jspvar="yesterday" />
<mm:time time="yesterday" format="EEEE d MMMM" jspvar="yesterday" />
<mm:time time="tomorrow" format="EEEE d MMMM" jspvar="tomorrow" write="false" />
</p>
<p>
8b) Use the jspvars in your page:<BR>
I want to see all movies between &lt;%=yesterday%> and &lt;%=tomorrow%>.<BR>
I want to see all movies between <%=yesterday%> and <%=tomorrow%>.
</p>
<p>
9) Display a field of a node that is a time:<BR>
        &lt;mm:field name="mmevents.start" ><BR>
               &lt;mm:time format="yyyy MM dd" /><BR>
        &lt;/mm:field><BR>
2002 03 12
</p>
<p>
10a) Set the time in the context:<BR>
&lt;mm:time id="time" time="2002/04/05" format="EEEE d MMMM yyyy" write="false"/>
<mm:time id="time" time="2002/04/05" format="yyyy/MM/dd" write="false"/>
</p>
<p>
10b) Reuse the time:<BR>
&lt;mm:time referid="time" format="EEEE d MMMM yyyy" />
<mm:time referid="time" format="EEEE d MMMM yyyy"  />
</p>
</mm:cloud>
</body>
</html>
