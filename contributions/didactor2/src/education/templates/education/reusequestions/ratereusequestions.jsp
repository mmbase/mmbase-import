<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest" required="true"/>


<%@include file="/shared/setImports.jsp" %>

<mm:node number="$question">
   <mm:relatednodescontainer path="givenanswers,madetests" element="givenanswers">
      <mm:constraint field="madetests.number" referid="madetest"/>
      <mm:relatednodes>
         <mm:deletenode deleterelations="true"/>
      </mm:relatednodes>
   </mm:relatednodescontainer>
</mm:node>

<mm:createnode type="givenanswers" id="givenanswer">
  <mm:setfield name="score">1</mm:setfield>
</mm:createnode>

<mm:createrelation role="related" source="madetest" destination="givenanswer"/>
<mm:createrelation role="related" source="question" destination="givenanswer"/>

</mm:cloud>
</mm:content>
