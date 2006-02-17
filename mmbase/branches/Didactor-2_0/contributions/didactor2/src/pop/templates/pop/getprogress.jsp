<%-- report either the current user's progress, or the one given by "student" argument --%>
<mm:import externid="student" id="student" reset="true"><mm:write referid="user"/></mm:import>


<mm:node number="$student" notfound="skip">

   <%-- find user's copybook --%>
   <mm:notpresent referid="copybookNo">
      <%@include file="find_copybook.jsp"%>
   </mm:notpresent>

   <%
      int nof_tests= 0;
      int nof_tests_passed= 0;
   %>

   <mm:node number="$education" notfound="skip">

      <mm:relatednodescontainer type="learnobjects" role="posrel">
         <mm:sortorder field="posrel.pos" direction="up"/>

         <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">
            <mm:import id="nodetype" reset="true"><mm:nodeinfo type="type" /></mm:import>

            <mm:compare referid="nodetype" value="tests">
            <% nof_tests++; %>

               <mm:import id="testNo" reset="true"><mm:field name="number"/></mm:import>

               <%@include file="teststatus.jsp"%>

               <mm:compare referid="teststatus" value="passed">
                  <% nof_tests_passed++; %>
               </mm:compare>
            </mm:compare>
         </mm:tree>
      </mm:relatednodescontainer>

      <%
         double progress= (double)nof_tests_passed / (double)nof_tests;
         if (nof_tests==0) progress=0;
      %>

<mm:import id="progress" reset="true"><%=progress%></mm:import>
<mm:import id="startflag" reset="true"><% if (nof_tests>0) { %>1<% } else {%>0<% } %></mm:import>
<mm:import id="finished" reset="true"><% if (nof_tests_passed == nof_tests) { %>1<% } else {%>0<% } %></mm:import>

</mm:node>

<mm:import id="intake" reset="true">0</mm:import>
<mm:import id="gatekeeper" reset="true">1</mm:import>

<%	String neededCompetencies = "";
	intakeCompetencies = ""; 
	notpassedIntakes = ""; 
%>
<mm:node number="$education">
  <mm:relatednodes type="tests" role="related">
    <mm:import id="testNo" reset="true"><mm:field name="number"/></mm:import>
    <%@include file="teststatus.jsp"%>
    <mm:compare referid="teststatus" value="failed">
       <mm:import id="gatekeeper" reset="true">0</mm:import>
    </mm:compare>
  </mm:relatednodes>
  <mm:compare referid="gatekeeper" value="1">
    <mm:import id="intake" reset="true">1</mm:import>
    <mm:relatednodescontainer type="learnobjects" role="posrel">
      <mm:sortorder field="posrel.pos" direction="up"/>
      <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">
        <mm:related path="developcomp,competencies">
          <mm:field name="competencies.number" jspvar="thisCompetencie" vartype="String">
            <% neededCompetencies += thisCompetencie + ","; %>
          </mm:field>
        </mm:related>
      </mm:tree> 
    </mm:relatednodescontainer> 
  </mm:compare>
</mm:node>
<mm:compare referid="gatekeeper" value="1">
<% if (neededCompetencies.length() != 0) { %>
  <mm:list nodes="<%= neededCompetencies %>" path="competencies">
    <% boolean needIntake = true; %>
    <mm:node element="competencies">
      <mm:related path="havecomp,pop" constraints="pop.number='$currentpop'">
        <% needIntake = false; %>
      </mm:related>
      <mm:field name="number" jspvar="thisCompetencie">
        <% if (needIntake) intakeCompetencies += thisCompetencie + ","; %>
      </mm:field> 
      <mm:relatednodes type="tests" role="comprel">
        <mm:import id="testNo" jspvar="thisIntake" reset="true"><mm:field name="number"/></mm:import>
        <%@include file="teststatus2.jsp"%>
        <mm:compare referid="teststatus" value="passed" inverse="true">
          <% if (notpassedIntakes.equals("")) { notpassedIntakes = thisIntake; } else { notpassedIntakes += thisIntake + ","; } %>
        </mm:compare>
        <mm:compare referid="teststatus" value="incomplete">
          <mm:import id="intake" reset="true">0</mm:import>
        </mm:compare>
      </mm:relatednodes>
    </mm:node>
  </mm:list>
<% } %>
</mm:compare>
</mm:node>
