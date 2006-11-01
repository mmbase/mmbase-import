<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest" required="true"/>
   
        
<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<%-- 
	Multiple choice questions
	type	0: only 1 answer can be selected
			1: multiple answers can be selected
--%>

<mm:node number="$question" id="my_question">
<mm:createnode type="givenanswers" id="my_givenanswers">
  <mm:setfield name="score">0</mm:setfield>
</mm:createnode>

<mm:createrelation role="related" source="madetest" destination="my_givenanswers"/>
<mm:createrelation role="related" source="question" destination="my_givenanswers"/>
	
  <%-- Only 1 answer is given --%>
  <mm:field name="type" id="type" write="false"/>
  <mm:compare referid="type" value="0">
    <mm:import externid="$question" id="givenanswer" jspvar="sGivenAnswer" vartype="String"/>
    <mm:present referid="givenanswer">
      <%
          sGivenAnswer = sGivenAnswer.trim();
      %>

      <%-- Search the given answer in the possible answers --%>		
      <mm:relatednodes type="mcanswers" role="posrel" orderby="posrel.pos" id="my_answers">

        <mm:field id="answer" name="text" write="false" />
        <mm:import jspvar="sAnswer"><mm:field name="text" escape="none"/></mm:import>
        <%
          if ( sAnswer.trim().equals(sGivenAnswer)) {
        %>

          <%-- copy the correct field of the answer--%>
          <mm:field id="questioncorrect" name="correct" write="false"/>
        
          <mm:node referid="my_givenanswers">
            <mm:setfield name="score"><mm:write referid="questioncorrect"/></mm:setfield>
          </mm:node>
          <mm:remove referid="questioncorrect" />
        
          <mm:createrelation role="related" source="my_givenanswers" destination="my_answers"/>
        <% } %>
        
      </mm:relatednodes>
    </mm:present>
  </mm:compare>

  <%-- Multiple answers can be given --%>
  <mm:compare referid="type" value="1">
    <%-- correct unless counterevidence --%>
    <mm:import id="score">1</mm:import>

    <mm:relatednodes type="mcanswers" id="my_answers">
      <mm:field id="answer" name="number" write="false"/>
      <mm:field id="answertext" name="text" write="false"/>
      <mm:field id="correct" name="correct" write="false"/>
       <mm:import jspvar="answerText"><mm:write referid="answertext" escape="none"/></mm:import>
      <mm:import id="givenanswer"><mm:write referid="question"/>_<mm:field name="number"/></mm:import>

      
      <mm:import externid="$givenanswer" id="givenanswertext" jspvar="givenAnswerText"/>
      <% if (givenAnswerText.trim().equals(answerText.trim())) { %>

        <%-- Relate each given answer to the possible answers --%>
        <mm:createrelation role="related" source="my_givenanswers" destination="my_answers"/>
        <%-- when this is a false answer, the score is incorrect --%>
        <mm:compare referid="correct" value="0">
          <mm:remove referid="score"/>
          <mm:import id="score">0</mm:import>
        </mm:compare>
      <% } else { %> 
        <%-- when the student had to check the button of thge correct answer, the score is incorrect --%>
        <mm:compare referid="correct" value="1">
           <mm:remove referid="score"/>
           <mm:import id="score">0</mm:import>
        </mm:compare>
      <% } %>
      <mm:remove referid="correct"/>
      <mm:remove referid="givenanswer"/>
      <mm:remove referid="answer"/>
    </mm:relatednodes>

    <mm:node referid="my_givenanswers">
      <mm:setfield name="score"><mm:write referid="score"/></mm:setfield>
    </mm:node>
  </mm:compare>    

  
</mm:node>

</mm:cloud>
</mm:content>
