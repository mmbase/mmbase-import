<%response.setHeader("Content-Type", "text/vnd.rn-realtext");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><window bgcolor="blue" link="gray" width="200" height="20">
<mm:cloud>
<mm:import externid="fragment" required="true" />
<mm:node number="$fragment">
<font color="white" size="2">
<a href="command:pause()" target="_player">Pause</a> |
<a href="command:play()"  target="_player">Play </a> <br>
</font>
</mm:node>
</mm:cloud>
</window>