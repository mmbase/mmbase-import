  <mm:compare referid="command" value="adddoc">
    <%@ include file="adddoc.jsp" %>
    <mm:remove referid="command"/>
    <mm:import id="command">-1</mm:import>
  </mm:compare>
  <mm:compare referid="command" value="savedoc">
    <%@ include file="savedoc.jsp" %>
    <mm:remove referid="command"/>
    <mm:import id="command"><mm:write referid="returnto"/></mm:import>
  </mm:compare>
  <mm:compare referid="command" value="deldocs">
    <%@ include file="deldocs.jsp" %>
    <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><fmt:message key="MsgDelSelectedDocDone"/></mm:import>
    <% msgString = dummy; %>
    <mm:remove referid="command"/>
    <mm:import id="command">continue</mm:import>
  </mm:compare>
