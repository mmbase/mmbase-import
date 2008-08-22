  <mm:compare referid="popcmd" value="addtodo">
    <%@ include file="addtodo.jsp" %>
    <mm:remove referid="popcmd"/>
    <mm:import id="popcmd">-1</mm:import>
  </mm:compare>
  <mm:compare referid="popcmd" value="savetodo">
    <%@ include file="savetodo.jsp" %>
    <mm:remove referid="popcmd"/>
    <mm:import id="popcmd"><mm:write referid="returnto"/></mm:import>
  </mm:compare>
  <mm:compare referid="popcmd" value="deltodo">
    <%@ include file="deltodo.jsp" %>
    <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><di:translate key="pop.msgremoveselectedtodo" /></mm:import>
    <% msgString = dummy; %>
    <mm:remove referid="popcmd"/>
    <mm:import id="popcmd">continue</mm:import>
  </mm:compare>
