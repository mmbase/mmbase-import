<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<mm:cloud>
<%@ include file="menu.jsp"%>

<h3>Time Taglib</h3>
The time taglib enables you to manage times in your web pages easily.<BR>
The taglib contains five attributes:<BR>
<LI>time
<LI>inputformat
<LI>offset
<LI>format
<LI>language
<BR><BR>
You use the <B>time</B> attribute to specify which time should be used. This can be done in a couple of ways. By not using the time attribute the time taglib will use the current time. The second ways is by specifying the time in seconds from EPOC (<B>UTC</b>). You can also use keywords as: yesterday, tomorrow, nextweek, lastweek. Or by specifying a time in a human readable way such as: Vrijdag 10 Januari 2002. (In the last case the attribute inputformat should be used to tell the time taglib how to read the human readable time). There are even more ways, see the examples.<BR><BR>
The <b><I>inputformat</I></B> attribute should only be used when the time attribute contains a human readable time. The way you have to specify the attribute can be read at http://java.sun.com/j2se/1.3/docs/api/java/text/SimpleDateFormat.html. Notice that times that look like: yyyy/mm/dd hh:mm:ss, yyyy/mm/dd, and hh:mm:ss don't need this attribute. They are parsed automatically.<BR><BR>
The <b>offset</b> attribute can be used to change the time specified with the time attribute. The offset is expressed in seconds.<BR><BR>
The <b>format</b> keyword is used to specify how you want to display the time. If the format attribute is not used the time will be displayed in seconds from EPOC (UTC). The syntax of the format and inputformat attributes are identical.<BR><BR>
The <B>language</B> attribute enables you to display the days and months in another language. If this attribute is not used the default system language will be used.<BR><BR>
The time taglib extend basic MMBase taglib functionality, this means that more attributes are available. See these examples.<BR><BR>

<h3>Examples</H3>
Show the time in seconds from EPOC (this is the time that is used by MMBase):<BR>
&lt;mm:time />
<mm:time />
<BR><BR>

Show the time in seconds from EPOC and format it in a nice way:<BR>
&lt;mm:time format="EEEE d MMMM" />
<mm:time format="EEEE d MMMM" /><BR><BR>

Display in different languages:<BR>
&lt;mm:time format="EEEE d MMMM" language="en" />
<mm:time format="EEEE d MMMM" language="en" /><BR>
&lt;mm:time format="EEEE d MMMM" language="es" />
<mm:time format="EEEE d MMMM" language="es" /><BR>
&lt;mm:time format="EEEE d MMMM" language="de" />
<mm:time format="EEEE d MMMM" language="de" /><BR>
&lt;mm:time format="EEEE d MMMM" language="fr" />
<mm:time format="EEEE d MMMM" language="fr" />
<BR><BR>

Show the time of yesterday and format it in a nice way:<BR>
&lt;mm:time time="yesterday" format="EEEE d MMMM yyyy" />
<mm:time time="yesterday" format="EEEE d MMMM yyyy" />
<BR><BR>

Add an offset of 3 days to the time:<BR>
&lt;mm:time offset="259200" format="EEEE d MMM yy" />
<mm:time offset="259200" format="EEEE d MMM yy" />
<BR><BR>

Time given in yyyy/mm/dd will be automatically converted:<BR>
&lt;mm:time time="<mm:time id="ymd" format="yyyy/MM/dd" />" />
<mm:time referid="ymd" />
<BR><BR>

Time given in hh:mm:ss will be automatically converted:<BR>
&lt;mm:time time="<mm:time id="hms" format="HH:mm:ss" />" />
<mm:time referid="hms" />
<BR><BR>

Time given in yyyy/mm/dd hh:mm:ss will be automatically converted:<BR>
&lt;mm:time time="<mm:time id="ymdhms" format="yyyy/MM/dd HH:mm:ss" />" />
<mm:time referid="ymdhms" />
<BR><BR>

Days can be used for the time attribute<BR>
&lt;mm:time time="saturday" format="yyyy/MM/dd HH:mm:ss" />" 
<mm:time time="saturday" format="yyyy/MM/dd HH:mm:ss" /><BR>
<BR><BR>

Months can be used for the time attribute. The offset will indicate when the current month or the next month has to be displayed. This example will show the next februari from day 20.<BR>
&lt;mm:time time="february" format="yyyy/MM/dd HH:mm:ss" offset="1728000" />" 
<mm:time time="february" format="yyyy/MM/dd HH:mm:ss" offset="1728000" /><BR>
<BR><BR>

Display the human readable time in another way and use an offset of three days:<BR>
&lt;mm:time time="2002/04/05" offset="259200" format="EEEE d MMMM yyyy" /><BR>
<mm:time time="2002/04/05" offset="259200" format="EEEE d MMMM yyyy" />
<BR><BR>

All format characters you can use, the meaning can be found at http://java.sun.com/j2se/1.3/docs/api/java/text/SimpleDateFormat.html:<BR>
&lt;mm:time format="G y M MMM MMMM d h H m s S E EEEE D F w W a k K z" />
<mm:time format="G y M MMM MMMM d h H m s S E EEEE D F w W a k K z" />
<BR><BR>

<H3>advanced examples</H3>

Convert a human readable time to seconds from EPOC:<BR>
&lt;mm:time time="2000/11/01" inputformat="y/M/d" />
<mm:time time="2000/11/01" inputformat="y/M/d" />
<BR><BR>

Display yesterday and save it as a jspvar:<BR>
&lt;mm:time time="yesterday" format="EEEE d MMMM" jspvar="yesterday" />
<mm:time time="yesterday" format="EEEE d MMMM" jspvar="yesterday" />
<BR><BR>

Display tomorrow and save it as a jspvar, but don't show the result:<BR>
&lt;mm:time time="tomorrow" format="EEEE d MMMM" jspvar="tomorrow"  write="false" />
<mm:time time="tomorrow" format="EEEE d MMMM" jspvar="tomorrow" write="false" />
<BR><BR>

Use the jspvars in your page:<BR>
I wanna see all movies between &lt;%=yesterday%> and &lt;%=tomorrow%>.<BR>
I wanna see all movies between <%=yesterday%> and <%=tomorrow%>.
<BR><BR>

Set the time in the context:<BR>
&lt;mm:time id="time" time="2002/04/05" format="EEEE d MMMM yyyy" write="false"/>
<mm:time id="time" time="2002/04/05" format="yyyy/MM/dd" write="false"/><BR><BR>

Display the time:<BR>
&lt;mm:write referid="time" />
<mm:write referid="time" /><BR><BR>

Reuse the time:<BR>
&lt;mm:time referid="time" format="EEEE d MMMM yyyy" />
<mm:time referid="time" format="EEEE d MMMM yyyy"  /><BR><BR>

Display a field of a node that is a time:<BR>
&lt;mm:list path="mmevents" fields="start" max="1" ><BR>
        &lt;mm:field name="mmevents.start" ><BR>
               &lt;mm:time format="yyyy MM dd" /><BR>
        &lt;/mm:field><BR>
&lt;/mm:list> <BR>
<mm:list path="mmevents" fields="start" max="1" >
        <mm:field name="mmevents.start" >
               <mm:time format="yyyy MM dd" />
        </mm:field>
</mm:list><BR><BR>

</mm:cloud>
