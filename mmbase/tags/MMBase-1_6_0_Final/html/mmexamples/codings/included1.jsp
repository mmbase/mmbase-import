<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=iso-8859-1" %>
<p>in page: Café tweeëntwintig</p>
<p>mmbase:  
  <mm:import externid="node" from="parameters">codings</mm:import> 
  <mm:cloud>
		<mm:node number="$node" notfound="skip">
      <mm:field name="subtitle" />
    </mm:node>
  </mm:cloud>
</p>
