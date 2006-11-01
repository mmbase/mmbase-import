<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest" required="true"/>

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<mm:createnode type="givenanswers" id="givenanswer">
  <mm:setfield name="score"><mm:write referid="TESTSCORE_INCOMPLETE"/></mm:setfield>
</mm:createnode>

<mm:createrelation role="related" source="madetest" destination="givenanswer"/>
<mm:createrelation role="related" source="question" destination="givenanswer"/>


<mm:node number="$question">
  <%-- Compute size --%>
  <mm:import id="nof_rankinganswers" jspvar="nof_rankinganswers" vartype="Integer"> <mm:relatednodescontainer type="rankinganswers"><mm:size/></mm:relatednodescontainer></mm:import>
  <% int pos= 0; %>

  <mm:import id="correct">1</mm:import>
  <mm:relatednodes type="rankinganswers" role="posrel" orderby="posrel.pos" >
    <% pos++; %>
    <mm:field id="answer" name="number" write="false"/>

    <mm:import id="givenrankId"><mm:write referid="question"/>_<mm:write referid="answer"/></mm:import>
    <mm:import id="givenrank" externid="$givenrankId"/>
    <mm:compare referid="givenrank" value="<%= ""+pos %>" inverse="true">
       <mm:remove referid="correct"/>
       <mm:import id="correct">0</mm:import>
    </mm:compare>

    <mm:createrelation role="posrel" source="givenanswer" destination="answer">
      <mm:setfield name="pos"><mm:write referid="givenrank"/></mm:setfield>
    </mm:createrelation>

    <mm:remove referid="answer"/>
    <mm:remove referid="givenrank"/>
  </mm:relatednodes>

  <mm:node referid="givenanswer">
    <mm:setfield name="score"><mm:write referid="correct"/></mm:setfield>
  </mm:node>

</mm:node>


</mm:cloud>
</mm:content>
