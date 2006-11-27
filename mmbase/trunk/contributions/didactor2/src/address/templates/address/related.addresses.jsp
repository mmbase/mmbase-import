<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:import externid="linkedlist" vartype="list" jspvar="linkedlist" from="request" />
<mm:node>
  <mm:relatednodescontainer type="object" role="related" searchdirs="source">
    <mm:typeconstraint name="people,contacts" />
    <mm:relatednodes>
      <mm:field name="number" jspvar="p" vartype="integer">
        <jsp:scriptlet>if (! linkedlist.contains(p)) { linkedlist.add(p); }</jsp:scriptlet>
      </mm:field>
    </mm:relatednodes>
  </mm:relatednodescontainer>
</mm:node>