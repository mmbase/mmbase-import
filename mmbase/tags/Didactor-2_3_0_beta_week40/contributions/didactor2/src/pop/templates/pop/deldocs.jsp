<%@ include file="getmyfeedback.jsp" %>
<mm:import externid="portfolio_items_ids" jspvar="list" vartype="List"/>
<mm:node number="$thisfeedback" notfound="skip">
  <mm:listrelationscontainer role="related" type="object">
    <mm:constraint field="object.number" referid="portfolio_items_ids" operator="IN"/>
    <mm:listrelations>
      <mm:deletenode/>
    </mm:listrelations>
  </mm:listrelationscontainer>
</mm:node>
