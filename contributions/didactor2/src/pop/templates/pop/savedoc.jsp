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
<mm:createrelation role="related" source="thisfeedback" destination="addnode"/>