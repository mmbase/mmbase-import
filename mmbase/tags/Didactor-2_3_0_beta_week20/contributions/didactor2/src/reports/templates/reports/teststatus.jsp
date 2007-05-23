<%--<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %><mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">--%>
<%--
    input params:
        testNo - number of the test node
        copybookNo - number of the students relevant copybook

    sets: teststatus to

        "passed" if the test was passed
        "failed" if the test was failed
        "toberated" if the test was completed but needs teacher's input
        "incomplete" if the test wasn't completed

        madetestscore
        requiredscore
        goodanswers
        badanswers
        
<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>
<mm:import externid="copybookNo" required="true"/>
<mm:import externid="testNo" required="true"/>--%>
    <mm:node number="$testNo">
      <%-- Determine #questions to be answered --%>
        <mm:import id="questionamount" reset="true"><mm:field name="questionamount"/></mm:import>
        <mm:compare referid="questionamount" value="-1"> <%-- -1 means ALL --%>
          <mm:relatednodes type="questions" role="posrel" orderby="posrel.pos">
            <mm:remove referid="questionamount"/>
            <mm:import id="questionamount"><mm:size/></mm:import>
          </mm:relatednodes>
        </mm:compare>
        <mm:import id="done" reset="true">false</mm:import>
        <mm:import id="tbs"  reset="true">false</mm:import>
        <mm:import id="passed" reset="true">false</mm:import>
        <mm:remove referid="requiredscore"/>
        <mm:field id="requiredscore" name="requiredscore" write="false"/>
	<%-- all the made tests are traversed. Since a test can be made many times
             If one made test is passed, then THE test is passed. To save the score,
             we introduce: save_madetestscore
          --%>
	<mm:import id="save_madetestscore" reset="true"></mm:import>
        <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
          <mm:constraint field="madetests.score" referid="TESTSCORE_INCOMPLETE" inverse="true"/>
          <mm:constraint field="copybooks.number" referid="copybookNo"/>
          <mm:relatednodes>
            <mm:remove referid="done"/>
            <mm:import id="done">true</mm:import>

            <mm:field name="score" id="madetestscore" write="false"/>
             <mm:compare referid="madetestscore" referid2="TESTSCORE_TBS">
               <mm:remove referid="tbs"/>
               <mm:import id="tbs">true</mm:import>
            </mm:compare> 
            <mm:compare referid="madetestscore" referid2="TESTSCORE_TBS" inverse="true">
              <%-- if madestestscore larger or equal than requiredscore --%>
              <mm:islessthan referid="madetestscore" referid2="requiredscore" inverse="true">
                  <mm:remove referid="passed"/>
                  <mm:import id="passed">true</mm:import>
                  <mm:remove referid="save_madetestscore"/>
                  <mm:import id="save_madetestscore"><mm:write referid="madetestscore"/></mm:import>
              </mm:islessthan>
            </mm:compare>
<%--
gekregen: <mm:write referid="madetestscore"/><br/>
vereist: <mm:write referid="requiredscore"/><br/>
--%>

              <% int good =0;
                 int bad = 0; %>
                <mm:related path="givenanswers" constraints="givenanswers.score<= 0"><% bad++; %></mm:related>
                <mm:related path="givenanswers" constraints="givenanswers.score > 0"><% good++; %></mm:related>
              <mm:import id="goodanswers"><%= good %></mm:import>
              <mm:import id="falseanswers"><%= bad %></mm:import>


         </mm:relatednodes>
       </mm:relatednodescontainer>
       
       <mm:compare referid="done" value="false">
         <mm:import id="teststatus" reset="true">incomplete</mm:import>
       </mm:compare>
        
        <mm:compare referid="done" value="true">
            <mm:compare referid="tbs" value="true">
              <mm:import id="teststatus" reset="true">toberated</mm:import>
            </mm:compare>
            <mm:compare referid="tbs" value="true" inverse="true">
                <mm:compare referid="passed" value="true">
                    <mm:import id="teststatus" reset="true">passed</mm:import>
                </mm:compare>
                <mm:compare referid="passed" value="false">
                    <mm:import id="teststatus" reset="true">failed</mm:import>
                </mm:compare>
            </mm:compare>
        </mm:compare>
    </mm:node>
<%--</mm:cloud>
</mm:content>--%>
