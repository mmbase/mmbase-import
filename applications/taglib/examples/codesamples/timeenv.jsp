Display yesterday and save it as a jspvar:<br />
<mm:time time="yesterday" format="EEEE d MMMM" jspvar="yesterday" write="true" ><br />
And tomorrow too.<mm:time time="tomorrow" format="EEEE d MMMM" jspvar="tomorrow"><br />
Use the jspvars in your page:<br />
I want to see all movies between <%="" + yesterday%> and <%=tomorrow%>.<br />
</mm:time>
</mm:time>
Can also give it an id:<br />
<mm:time id="nice_time" time="2002/04/05"  write="false"/>
Reuse the time:<br />
<mm:time referid="nice_time" format=":FULL"  />
