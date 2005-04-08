<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<mm:import externid="student" required="true"/>
<mm:import externid="startAt" jspvar="startAt" vartype="Integer" required="true"/>

<di:may component="education" action="isSelfOrTeacherOf" arguments="student">
 
<mm:node number="$student">
  <tr>
    <td>
    <a href="<mm:treefile page="/progress/student.jsp" objectlist="$includePath" referids="$referids,student"/>"><mm:field name="firstname"/> <mm:field name="lastname"/></a></td>
    <td>
        <mm:import jspvar="progress" id="progress" vartype="Double"><mm:treeinclude page="/progress/getprogress.jsp" objectlist="$includePath" referids="$referids">
            <mm:param name="student"><mm:write referid="student"/></mm:param>
        </mm:treeinclude></mm:import>
        <%= (int)(progress.doubleValue()*100.0)%>%
    </td>

    <mm:list fields="classrel.number" path="people,classrel,classes" constraints="people.number=$student and classes.number=$class">
	<mm:field name="classrel.number" id="classrel" write="false"/>
    </mm:list>
    <mm:node referid="classrel">

    <td>
        <mm:field name="logincount"/>
    </td>
    <td>
        <mm:field name="onlinetime" jspvar="onlinetime" vartype="Integer" write="false">
        <%
        int hour = onlinetime.intValue() / 3600;
        int min = (onlinetime.intValue() % 3600) / 60;
        %>
        <%=hour%>:<%=min%>
        </mm:field>
        </mm:node>
    </td>
<% 
    int testCounter = 0;
%>
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
        <% if (testCounter >= startAt.intValue() && testCounter < startAt.intValue()+15) { %>

      
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
                        <mm:param name="studentNo"><mm:write referid="student"/></mm:param>
                        <mm:param name="testNo"><mm:write referid="testNo"/></mm:param>
                   </mm:treefile>"><img src="<mm:treefile page="/progress/gfx/question.gif" objectlist="$includePath" referids="$referids"/>" alt="?" border="0"></a></td>
                 </di:hasrole>
                 <di:hasrole role="teacher" inverse="true">
                     <td class="td_test_tbs"><img src="<mm:treefile page="/progress/gfx/question.gif" objectlist="$includePath" referids="$referids"/>" alt="?" border="0"></td>
                 </di:hasrole>
             </mm:compare>
            
             <mm:compare referid="teststatus" value="passed">
       	             <td class="td_test_tbs"><img src="<mm:treefile page="/progress/gfx/checked.gif" objectlist="$includePath" referids="$referids"/>" alt="Ok" border="0"></td>
             </mm:compare>
             
             <mm:compare referid="teststatus" value="failed">
       	             <td class="td_test_failed"><img src="<mm:treefile page="/progress/gfx/box.gif" objectlist="$includePath" referids="$referids"/>" alt="" border="0"></td>
       	     </mm:compare>
            
        </mm:compare>
         <mm:compare referid="teststatus" value="incomplete" >
           <td class="td_test_not_done"><img src="<mm:treefile page="/progress/gfx/box.gif" objectlist="$includePath" referids="$referids"/>" alt="" border="0"></td>
         </mm:compare>
        <mm:remove referid="madetestscore"/>
         <mm:remove referid="save_madetestscore"/>
         <mm:remove referid="testNo"/>
            <%
        } testCounter++;
         %>
         </mm:compare>
      <mm:remove referid="nodetype"/>
    </mm:tree>
  </mm:relatednodescontainer> <%-- learnobjects --%>
</mm:node> <%-- education --%>
<mm:remove referid="copybookNo"/>
</tr>
</mm:node> <%-- student --%>
</di:may>

</mm:cloud>
</mm:content>
