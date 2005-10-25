<mm:import externid="addnode"/>
<mm:node number="$addnode" notfound="skip">
<mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><di:translate key="pop.msgitemadded" /></mm:import>
<% msgString = dummy; %>
<%@ include file="getmyfeedback.jsp" %>
<% if (isEmpty) { %>
  <mm:remove referid="thisfeedback"/>
  <mm:maycreate type="popfeedback">
    <mm:createnode type="popfeedback" id="thisfeedback"/>
  </mm:maycreate>
  <mm:createrelation role="related" source="currentpop" destination="thisfeedback" />
  <mm:createrelation role="related" source="thisfeedback" destination="student" />
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
</mm:node>
