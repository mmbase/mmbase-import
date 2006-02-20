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
<mm:node referid="thisfeedback">
   <mm:setfield name="rank"><mm:write referid="myfeedback1"/></mm:setfield>
   <mm:setfield name="text"><mm:write referid="myfeedback2"/></mm:setfield>
</mm:node>
