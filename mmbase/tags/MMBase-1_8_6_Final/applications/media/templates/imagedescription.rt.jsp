<%response.setHeader("Content-Type", "text/vnd.rn-realtext");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><window bgcolor="yellow" link="gray" width="200" height="60">
<mm:cloud>
<mm:import externid="image" required="true" />
<mm:import externid="turn" />
<mm:present referid="turn">
  <mm:import id="extra"> (<mm:write referid="turn" /> graden gedraaid)</mm:import>
</mm:present>
<mm:notpresent referid="turn">
  <mm:import id="extra" />
</mm:notpresent>
<mm:node number="$image">
<font color="black" size="2">Laatste plaatjes uit MMBase</font><br>
<font color="black" size="1"><mm:field name="description" /><mm:write referid="extra" /></font>
</mm:node>
</mm:cloud>
</window>
