  <mm:compare referid="popcmd" value="adddoc">
    <%@ include file="adddoc.jsp" %>
    <mm:remove referid="popcmd"/>
    <mm:import id="popcmd">-1</mm:import>
  </mm:compare>
  <mm:compare referid="popcmd" value="savedoc">
    <%@ include file="savedoc.jsp" %>
    <mm:remove referid="popcmd"/>
    <mm:import id="popcmd"><mm:write referid="returnto"/></mm:import>
  </mm:compare>
  <mm:compare referid="popcmd" value="deldocs">
    <%@ include file="deldocs.jsp" %>
    <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><di:translate key="pop.msgdelselecteddocdone" /></mm:import>
    <% msgString = dummy; %>
    <mm:remove referid="popcmd"/>
    <mm:import id="popcmd">continue</mm:import>
  </mm:compare>
