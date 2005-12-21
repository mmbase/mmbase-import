<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest" required="true"/>

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<mm:node number="$question" id="my_question">

  <%-- Which answer has been given on the question --%>
  <mm:import externid="$question" id="givenanswer" />


	<mm:field name="type_of_score" id="type">
	
		<mm:compare value="0">	
  			<%-- Save the answer if type_of_score=0 --%>
  			<mm:createnode type="givenanswers" id="my_givenanswers">
    			<mm:setfield name="text"><mm:write referid="givenanswer"/></mm:setfield>
    			<mm:setfield name="score"><mm:write referid="TESTSCORE_TBS"/></mm:setfield>
  			</mm:createnode>
		</mm:compare> 
		
		<mm:compare value="2">
		<%-- Save the answer if type_of_score=2 --%>
  			<mm:createnode type="givenanswers" id="my_givenanswers">
    			<mm:setfield name="text"><mm:write referid="givenanswer"/></mm:setfield>
    			<mm:setfield name="score"><mm:write referid="TESTSCORE_COR"/></mm:setfield>
  			</mm:createnode>
		</mm:compare> 
<		
		<mm:compare value="1">
		 <%-- Save the answer if type_of_score=1 --%> 
			<mm:relatednodes type="openanswers" id="openanswers">
				<mm:field name="text" id="text">
					<mm:compare referid="givenanswer" referid2="openanswers">
						<mm:createnode type="givenanswers" id="my_givenanswers">
    						<mm:setfield name="text"><mm:write referid="givenanswer"/></mm:setfield>
    						<mm:setfield name="score"><mm:write referid="TESTSCORE_COR"/></mm:setfield>
  						</mm:createnode>
					</mm:compare>
					<mm:compare referid="givenanswer" referid2="openanswers" inverse="true">
					<mm:createnode type="givenanswers" id="my_givenanswers">
    						<mm:setfield name="text"><mm:write referid="givenanswer"/></mm:setfield>
    						<mm:setfield name="score"><mm:write referid="TESTSCORE_WR"/></mm:setfield>
  						</mm:createnode>
					</mm:compare>
				</mm:field>
			</mm:relatednodes>
			
		</mm:compare>
		 
  	</mm:field>
  	
  <mm:createrelation role="related" source="madetest" destination="my_givenanswers"/>
  <mm:createrelation role="related" source="question" destination="my_givenanswers"/>
          
</mm:node>

</mm:cloud>
</mm:content>
