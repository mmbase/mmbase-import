<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<mm:import id="studentNo" externid="userNoX" required="true"/>

<di:may component="education" action="isSelfOrTeacherOf" arguments="studentNo">
 
<mm:node number="$studentNo">
  <tr>
  <mm:import id="user_string"><mm:field name="html(firstname)"/> <mm:field name="html(lastname)"/></mm:import>
  <td><mm:write referid="user_string"/></td>

  <%-- find copybook --%>
  <mm:import id="copybookNo"/>
  <mm:relatedcontainer path="classrel,classes">
    <mm:constraint field="classes.number" value="$class"/>
    <mm:related>
      <mm:node element="classrel">
        <mm:relatednodes type="copybooks">
          <mm:remove referid="copybookNo"/>
          <mm:field id="copybookNo" name="number" write="false"/>
        </mm:relatednodes>
      </mm:node>
    </mm:related>  
  </mm:relatedcontainer>

<mm:node number="$education">
  <mm:relatednodescontainer type="learnobjects" role="posrel">
    <mm:sortorder field="posrel.pos" direction="up"/>
    <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" direction="up">

      <mm:import id="nodetype"><mm:nodeinfo type="type" /></mm:import>
      <mm:compare referid="nodetype" value="tests">
        <mm:field id="testNo" name="number" write="false"/>

      <%-- Determine #questions to be answered --%>
        <mm:import id="questionamount"><mm:field name="questionamount"/></mm:import>
        <mm:compare referid="questionamount" value="-1"> <%-- -1 means ALL --%>
          <mm:relatednodes type="questions" role="posrel" orderby="posrel.pos">
            <mm:remove referid="questionamount"/>
            <mm:import id="questionamount"><mm:size/></mm:import>
          </mm:relatednodes>
        </mm:compare>

        <mm:import id="done">false</mm:import>
        <mm:import id="tbs">false</mm:import>
        <mm:import id="passed">false</mm:import>
        <mm:field id="requiredscore" name="requiredscore" write="false"/>

	<%-- all the made tests are traversed. Since a test can be made many times
             If one made test is passed, then THE test is passed. To save the score,
             we introduce: save_madetestscore
          --%>
	<mm:import id="save_madetestscore"></mm:import>
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
         </mm:relatednodes>
       </mm:relatednodescontainer>

       <mm:compare referid="done" value="true">
       	 <mm:compare referid="passed" value="true">
       	 <td class="td_test_passed"><mm:write referid="save_madetestscore"/>/<mm:write referid="questionamount"/></td>
       	 </mm:compare>
       	  
       	     <mm:compare referid="passed" value="true" inverse="true">
       	       <mm:compare referid="tbs" value="true">
                 <di:hasrole role="teacher">
            	   <td class="td_test_tbs"><a href="<mm:treefile page="/education/tests/rateopen.jsp" objectlist="$includePath" referids="$referids">
                        <mm:param name="studentNo"><mm:write referid="studentNo"/></mm:param>
                        <mm:param name="testNo"><mm:write referid="testNo"/></mm:param>
                   </mm:treefile>">S</a></td>
                 </di:hasrole>
       	         <di:hasrole role="teacher" inverse="true">
       	           <td class="td_test_tbs">S</td>
        	     </di:hasrole>
       	       </mm:compare>
       	           
               <mm:compare referid="tbs" value="true" inverse="true">
       	         <td class="td_test_failed">F</td>
       	     </mm:compare>
           </mm:compare>
         </mm:compare>
         <mm:compare referid="done" value="true" inverse="true" >
           <td class="td_test_not_done">-</td>
         </mm:compare>
         <mm:remove referid="madetestscore"/>
         <mm:remove referid="save_madetestscore"/>
         <mm:remove referid="passed"/> 
         <mm:remove referid="tbs"/> 
         <mm:remove referid="done"/>
         <mm:remove referid="questionamount"/>
         <mm:remove referid="testNo"/>
      </mm:compare>
      <mm:remove referid="nodetype"/>
    </mm:tree>
  </mm:relatednodescontainer> <%-- learnobjects --%>
</mm:node> <%-- education --%>
</tr>
<mm:remove referid="copybookNo"/>
</mm:node> <%-- studentNo --%>
</di:may>

</mm:cloud>
</mm:content>
