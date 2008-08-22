<mm:related path="rolerel,components">
  <mm:import id="page" reset="true">/<mm:field name="components.name"/>/index.jsp</mm:import>
  <a href="<mm:treefile page="$page" objectlist="$includePath" referids="$referids"/>" target="_parent">
    <mm:field name="components.name" jspvar="component" vartype="String" write="false">
      <di:translate key='<%= component + ".linktext" %>' />
    </mm:field>
  </a>
</mm:related>
