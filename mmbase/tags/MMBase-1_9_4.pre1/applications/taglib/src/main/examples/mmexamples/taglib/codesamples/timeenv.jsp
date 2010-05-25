Display yesterday and save it as a jspvar. When no vartype 
is given, the formatted time is stored as String:<br />
<mm:time time="yesterday" format="EEEE d MMMM" 
         jspvar="yesterday" write="true" ><br />
And tomorrow too.
<mm:time time="tomorrow" format="EEEE d MMMM" jspvar="tomorrow"><br />
Use the jspvars in your page:<br />
I want to see all movies between
<%="" + yesterday%> and <%=tomorrow%>.<br />
</mm:time>
</mm:time>
<p>
</p>
When using vartype='date', the actual 'date' object is passed 
around, and can be reused for date-manipulations.
<mm:time id="nice_time" time="2002/04/05 12:34:56"  
         format=":LONG.LONG"  vartype="date" /><br />
Reusing the taglib variable 'nice_time':
<mm:write referid="nice_time" /><br />
<mm:time referid="nice_time" format=":FULL"  /><br />
<mm:time referid="nice_time" format=":FULL.FULL" 
         precision="hours"  /><br />
<mm:time referid="nice_time" vartype="date" jspvar="date">
  <%= date.getClass().getName() %>
</mm:time>
<hr />
<mm:time id="other_time" time="2002/04/05 12:34:56"  
         format=":LONG"   /><br />
<mm:write referid="other_time" /><br />
<mm:write referid="other_time" jspvar="date2">
  <%= date2.getClass().getName() %>
</mm:write>

