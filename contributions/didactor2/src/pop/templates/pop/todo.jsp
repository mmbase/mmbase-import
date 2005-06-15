  <mm:compare referid="command" value="addtodo">
    <%@ include file="addtodo.jsp" %>
    <mm:remove referid="command"/>
    <mm:import id="command">-1</mm:import>
  </mm:compare>
  <mm:compare referid="command" value="savetodo">
    <%@ include file="savetodo.jsp" %>
    <mm:remove referid="command"/>
    <mm:import id="command"><mm:write referid="returnto"/></mm:import>
  </mm:compare>
  <mm:compare referid="command" value="deltodo">
    <%@ include file="deltodo.jsp" %>
    <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><fmt:message key="MsgRemoveSelectedTodo"/></mm:import>
    <% msgString = dummy; %>
    <mm:remove referid="command"/>
    <mm:import id="command">continue</mm:import>
  </mm:compare>
