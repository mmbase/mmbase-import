<%@page session="false" 
   language="java"
   contentType="text/html;charset=UTF-8" 
 %><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:cloud>
<p>
  in page: Café tweeëntwintig, Ĉu vi ŝatas tion?
</p>
<p>
  in attribute: <mm:write value="Café tweeëntwintig, Ĉu vi ŝatas tion?" />
</p>
<p>mmbase:  
<mm:import externid="node" from="parameters">codings</mm:import> 
  subtitle of node '<mm:write referid="node" />': 
  <mm:node number="$node" notfound="skip">
    <mm:field name="subtitle" />
  </mm:node>
</mm:cloud>
</p>
