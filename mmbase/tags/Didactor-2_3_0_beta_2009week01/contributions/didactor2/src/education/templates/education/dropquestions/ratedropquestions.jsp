<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.List, java.util.Iterator, java.util.ArrayList, java.util.Collections"%>

<mm:content postprocessor="reducespace">

<mm:cloud method="delegate" jspvar="cloud">



<mm:import externid="question" required="true"/>

<mm:import externid="madetest" required="true"/>

   

        

<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>

<mm:node number="$question" id="my_question" jspvar="qNum">

<mm:createnode type="givenanswers" id="my_givenanswers">

  <mm:setfield name="score">0</mm:setfield>

</mm:createnode>



<mm:createrelation role="related" source="madetest" destination="my_givenanswers"/>

<mm:createrelation role="related" source="question" destination="my_givenanswers"/>

  <% int dragCounter = 1; %>

  <% String answerText = ""; %>

  <% boolean allCorrect = true; %>

  <mm:list nodes="$question" path="dropquestions,dragimagerel,images" fields="images.number" orderby="dragimagerel.pos" distinct="true">

    <% String extId = "drop"+qNum.getNumber()+"."+dragCounter; %>

    <% boolean thisAnswer = false; %>

    <mm:import id="givenanswer" jspvar="givenAnswer" externid="<%= extId %>" reset="true" from="parameters"/>



    

    <mm:isgreaterthan referid="givenanswer" value="0">

        <mm:import id="dragimage" reset="true"><mm:field name="images.number"/></mm:import>

        <mm:node number="$dragimage">

          <mm:import id="dragtitle" reset="true"><%= dragCounter %>. <mm:field name="title" /></mm:import>

        </mm:node>

        

        <%-- has been dragged - store title of destination image --%>

        <mm:list nodes="$question" path="dropquestions,dropimagerel,images" constraints="dropimagerel.pos=$givenanswer">

            <mm:import id="droptitle" reset="true"><mm:write referid="givenanswer"/>. <mm:field name="images.title"/></mm:import>

        </mm:list>

        

        <mm:import id="thisanswertext" jspvar="thisAnswerText" reset="true"><mm:write referid="dragtitle"/> - <mm:write referid="droptitle"/></mm:import>

        <% 

           if (answerText.length() != 0) {

            answerText += ", ";

           } 

           answerText += thisAnswerText;

        %>

        

	<%-- // has been dragged - check if the answer is true --%>

	<% String constraints = "dropanswers.dropnumber = "+givenAnswer+ " AND dropanswers.dragnumber = "+dragCounter; %>

	<mm:list nodes="$question" path="dropquestions,dropanswers" constraints="<%= constraints %>" max="1">

	    <% thisAnswer = true; %>

	</mm:list>

    </mm:isgreaterthan>

    

    <mm:islessthan referid="givenanswer" value="1">

	<%-- has not been dragged - check if there is no target --%>

	<% thisAnswer = true; %>

	<% String constraints = "dropanswers.dragnumber = "+dragCounter; %>

        <mm:list nodes="$question" path="dropquestions,dropanswers" constraints="<%= constraints %>" max="1">

            <% thisAnswer = false; %>

	</mm:list>

    </mm:islessthan>



    <% if (thisAnswer == false) {

	allCorrect = false;

    }

    dragCounter++;

    %>

    </mm:list>	

	  

       <mm:node referid="my_givenanswers">

          <mm:setfield name="score"><%= allCorrect ? "1" : "0" %></mm:setfield>

          <mm:setfield name="text"><%= answerText %></mm:setfield>

        </mm:node>

        <mm:remove referid="questioncorrect" />

</mm:node>



</mm:cloud>

</mm:content>

