<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:import externid="peopleSet" vartype="set" jspvar="peopleSet" from="request" />
<mm:node>
  <mm:relatednodescontainer type="people" role="contact">
    <mm:relatednodes>
      <mm:field name="number" jspvar="p" vartype="integer">
        <jsp:scriptlet>peopleSet.add(p);</jsp:scriptlet>
      </mm:field>
    </mm:relatednodes>
  </mm:relatednodescontainer>
</mm:node>