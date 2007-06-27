<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<mm:import externid="tests" required="true"/>
<mm:import externid="madetest" required="true"/>

<%-- count score of questions --%>
<% int points= 0; %>
<mm:import id="totalscore"><mm:write referid="TESTSCORE_INCOMPLETE"/></mm:import>
<mm:node referid="madetest">
  <mm:relatednodes type="givenanswers">
    <mm:field name="score" id="score" write="false"/> 
    <mm:compare referid="score" value="1">
      <% points= points+1; %>
    </mm:compare>
    <mm:compare referid="score" referid2="TESTSCORE_TBS">
      <mm:remove referid="totalscore"/>
      <mm:import id="totalscore"><mm:write referid="TESTSCORE_TBS"/></mm:import>
    </mm:compare>           
    <mm:remove referid="score"/>
  </mm:relatednodes>
  <mm:compare referid="totalscore" referid2="TESTSCORE_TBS">
    <mm:setfield name="score"><mm:write referid="TESTSCORE_TBS"/></mm:setfield>
  </mm:compare>
  <mm:compare referid="totalscore" referid2="TESTSCORE_TBS" inverse="true">
    <mm:setfield name="score"><%=points%></mm:setfield>
  </mm:compare>
</mm:node>

</mm:cloud>
</mm:content>
