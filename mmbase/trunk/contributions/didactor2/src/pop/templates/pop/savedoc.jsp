<mm:import externid="addnode"/>
<% msgString = "Item added"; %>
<%@ include file="getmyfeedback.jsp" %>
<% if (isEmpty) { %>
  <mm:remove referid="thisfeedback"/>
  <mm:maycreate type="popfeedback">
    <mm:createnode type="popfeedback" id="thisfeedback"/>
  </mm:maycreate>
  <mm:createrelation role="related" source="currentpop" destination="thisfeedback" />
  <mm:createrelation role="related" source="thisfeedback" destination="user" />
  <mm:createrelation role="related" source="currentcomp" destination="thisfeedback" />
<% } %>
<mm:remove referid="isrelated"/>
<mm:node number="$addnode">
  <mm:related path="popfeedback" constraints="popfeedback.number='$thisfeedback'">
    <mm:import id="isrelated">1</mm:import>
  </mm:related>
</mm:node>
<mm:notpresent referid="isrelated">
  <mm:createrelation role="related" source="thisfeedback" destination="addnode"/>
</mm:notpresent>
<mm:remove referid="isrelated"/>