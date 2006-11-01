<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest" required="true"/>

<%@include file="/shared/setImports.jsp" %>

<mm:node number="$question">

  <%int counter = 0;%>
  
  <mm:relatednodes type="couplinganswers" id="my_answers"/>
  
  <%-- Iterate answers to identify givenanswers --%>
  <mm:relatednodes referid="my_answers">

    <% counter++; %>
    
    <mm:import id="my_leftanswer"><mm:field name="number"/></mm:import>
    
    <mm:import id="givenanswer"><mm:write referid="question"/>_<mm:field name="number"/></mm:import>
    <mm:import id="givenanswertext" externid="$givenanswer"/>
    
    <%-- Iterate possible answers to relate them with the given answers --%>
    <mm:relatednodes referid="my_answers">
    
      <mm:import id="my_rightanswer"><mm:field name="number"/></mm:import>
      <mm:import id="answertext"><mm:field name="text2"/></mm:import>
      
      <%-- Check if you have the answer couple --%>
      <mm:compare referid="answertext" referid2="givenanswertext">
    
        <%-- Save the answer --%>
        <mm:notpresent referid="my_givenanswers">
          <%-- Create only one givenanswer node --%>
          <mm:createnode type="givenanswers" id="my_givenanswers">
          <mm:setfield name="score">1</mm:setfield>
	  </mm:createnode>
          <mm:createrelation role="related" source="madetest" destination="my_givenanswers"/>
          <mm:createrelation role="related" source="question" destination="my_givenanswers"/>
        </mm:notpresent>

        <%-- Relate each given correct answer to the possible answers --%>
        <mm:createrelation role="leftanswer" source="my_givenanswers" destination="my_leftanswer">
          <mm:setfield name="pos"><%=counter%></mm:setfield>
        </mm:createrelation>
      
        <mm:createrelation role="rightanswer" source="my_givenanswers" destination="my_rightanswer">
          <mm:setfield name="pos"><%=counter%></mm:setfield>
        </mm:createrelation>

        <mm:compare referid="my_leftanswer" referid2= "my_rightanswer" inverse="true">
           <mm:node referid="my_givenanswers">
             <mm:setfield name="score">0</mm:setfield>
           </mm:node>
        </mm:compare>

      </mm:compare>
 
      <mm:remove referid="my_rightanswer"/>
      <mm:remove referid="answertext"/>
      
    </mm:relatednodes>

    <mm:remove referid="givenanswer"/>
    <mm:remove referid="givenanswertext"/>
     
    <mm:remove referid="my_leftanswer"/>
    
  </mm:relatednodes>
</mm:node>
</mm:cloud>
</mm:content>
