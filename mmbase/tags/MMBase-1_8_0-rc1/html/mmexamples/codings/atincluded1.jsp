<%@page pageEncoding="ISO-8859-1" %>
<p>in page: Café tweeëntwintig</p>
<p>
  in attribute: <mm:write value="Café tweeëntwintig" />
</p>
<p>mmbase:  
		<mm:node number="$node" notfound="skip">
      <mm:field name="subtitle" />
    </mm:node>
</p>
