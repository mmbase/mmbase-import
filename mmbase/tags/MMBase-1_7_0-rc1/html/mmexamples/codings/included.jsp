<%@page session="false" language="java" contentType="text/html;charset=utf-8"
 %><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" type="text/html">
<p>in page: Café tweeëntwintig, Ĉu vi ŝatas tion?</p>
<p>mmbase:  
<mm:import externid="node" from="parameters">codings</mm:import> 
  <mm:cloud>
    subtitle of node '<mm:write referid="node" />': <mm:node number="$node" notfound="skip">
    <mm:field name="subtitle" />
  </mm:node>
</mm:cloud>
</p>
</mm:content>
