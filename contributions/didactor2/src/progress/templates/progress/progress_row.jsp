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
        <mm:import id="testNo" reset="true"><mm:field  name="number" /></mm:import>
        <mm:field id="feedback" name="feedbackpage" write="false"/>
 
<%--        <mm:import id="teststatus" reset="true" jspvar="testStatus" escape="reducespace"><mm:treeinclude page="/progress/teststatus.jsp" objectlist="$includePath" referids="$referids"><mm:param name="copybookNo"><mm:write referid="copybookNo"/></mm:param><mm:param name="testNo"><mm:field name="number"/></mm:param></mm:treeinclude></mm:import>
         <%
             testStatus = testStatus.trim();
         %><mm:import id="teststatus" reset="true" jspvar="testStatus" escape="reducespace"><%= testStatus %></mm:import>
--%>
        <%@include file="teststatus.jsp"%>
       <mm:compare referid="teststatus" value="incomplete" inverse="true">
       
             <mm:compare referid="teststatus" value="toberated">
                 <di:hasrole role="teacher">
            	   <td class="td_test_tbs"><a href="<mm:treefile page="/education/tests/rateopen.jsp" objectlist="$includePath" referids="$referids">
                        <mm:param name="studentNo"><mm:write referid="studentNo"/></mm:param>
                        <mm:param name="testNo"><mm:write referid="testNo"/></mm:param>
                   </mm:treefile>">?</a></td>
                 </di:hasrole>
                 <di:hasrole role="teacher" inverse="true">
                     <td class="td_test_tbs">?</td>
                 </di:hasrole>
             </mm:compare>
            
             <mm:compare referid="teststatus" value="passed">
       	             <td class="td_test_tbs">
                        <mm:compare referid="feedback" value="0">S</mm:compare>
                        <mm:write referid="goodanswers"/>/<mm:write referid="falseanswers"/>
                    </td>
             </mm:compare>
             
             <mm:compare referid="teststatus" value="failed">
       	             <td class="td_test_failed">
                         <mm:compare referid="feedback" value="0">F</mm:compare>
                         <mm:write referid="goodanswers"/>/<mm:write referid="falseanswers"/></td>
       	     </mm:compare>
            
        </mm:compare>
         <mm:compare referid="teststatus" value="incomplete" >
           <td class="td_test_not_done">-</td>
         </mm:compare>
        <mm:remove referid="madetestscore"/>
         <mm:remove referid="save_madetestscore"/>
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
