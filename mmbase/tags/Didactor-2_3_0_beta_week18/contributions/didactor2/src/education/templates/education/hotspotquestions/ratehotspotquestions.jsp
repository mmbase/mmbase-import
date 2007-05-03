<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest" required="true"/>
   
        
<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<mm:node number="$question" id="my_question">
<mm:createnode type="givenanswers" id="my_givenanswers">
  <mm:setfield name="score">0</mm:setfield>
</mm:createnode>

<mm:createrelation role="related" source="madetest" destination="my_givenanswers"/>
<mm:createrelation role="related" source="question" destination="my_givenanswers"/>
	
    <mm:import externid="hotspot$question" id="givenanswer" />

    <%-- Search the given answer in the possible answers --%>
    <% int i = 1; %>
    <mm:relatednodes type="hotspotanswers" id="my_answers" orderby="x1,y1">

      <mm:field id="answer" name="number" write="false"/>

      <mm:compare referid="givenanswer" referid2="answer">
        <%-- copy the correct field of the answer--%>
        <mm:field id="questioncorrect" name="correct" write="false"/>
        <mm:node referid="my_givenanswers">
          <mm:setfield name="score"><mm:write referid="questioncorrect"/></mm:setfield>
          <mm:setfield name="text"><%= i %></mm:setfield>
        </mm:node>
        <mm:remove referid="questioncorrect" />
        
<%--        <mm:createrelation role="related" source="my_givenanswers" destination="my_answers"/> --%>
        
      </mm:compare>
      <% i++; %>
    </mm:relatednodes>
  
</mm:node>

</mm:cloud>
</mm:content>
