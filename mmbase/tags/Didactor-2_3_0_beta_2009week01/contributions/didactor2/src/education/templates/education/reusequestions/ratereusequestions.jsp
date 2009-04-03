<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest" required="true"/>


<%@include file="/shared/setImports.jsp" %>

<mm:createnode type="givenanswers" id="givenanswer">
  <mm:setfield name="score">1</mm:setfield>
</mm:createnode>

<mm:createrelation role="related" source="madetest" destination="givenanswer"/>
<mm:createrelation role="related" source="question" destination="givenanswer"/>

</mm:cloud>
</mm:content>
