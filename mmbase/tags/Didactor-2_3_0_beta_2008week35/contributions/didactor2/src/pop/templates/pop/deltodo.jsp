<mm:import externid="ids" jspvar="list" vartype="List"/>
<mm:node number="$currentpop">
  <mm:relatednodescontainer type="object">
    <mm:constraint field="number" referid="ids" operator="IN"/>
    <mm:relatednodes>
       <mm:deletenode deleterelations="true"/>
    </mm:relatednodes>
  </mm:relatednodescontainer>
</mm:node>
