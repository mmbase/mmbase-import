<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest" required="true"/>

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<mm:node number="$question" id="my_question">
   <mm:relatednodescontainer path="givenanswers,madetests" element="givenanswers">
      <mm:constraint field="madetests.number" referid="madetest"/>
      <mm:relatednodes>
         <mm:deletenode deleterelations="true"/>
      </mm:relatednodes>
   </mm:relatednodescontainer>

  <%-- Which answer has been given on the question --%>
  <mm:import externid="$question" id="givenanswer" />

  <%-- Save the answer --%>
  <mm:createnode type="givenanswers" id="my_givenanswers">
    <mm:setfield name="text"><mm:write referid="givenanswer"/></mm:setfield>
    <mm:setfield name="score"><mm:write referid="TESTSCORE_TBS"/></mm:setfield>
  </mm:createnode>
  <mm:createrelation role="related" source="madetest" destination="my_givenanswers"/>
  <mm:createrelation role="related" source="question" destination="my_givenanswers"/>
          
</mm:node>

</mm:cloud>
</mm:content>
