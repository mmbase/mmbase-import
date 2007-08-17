<%-- report either the current user's progress, or the one given by "student" argument --%>

<mm:import externid="student" id="student" reset="true"><mm:write referid="user"/></mm:import>

<%-- find student's copybook --%>
<mm:node number="$student" notfound="skip">
   <%@include file="find_copybook.jsp"%>


<%	
// get the needed competencies related to this education tree
String neededCompetencies = "";
String intakeCompetencies = ""; 
%>
<mm:node number="$education">
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
</mm:node>
<%
// for all needed competencies: if the intake test related to the competence is passed, set the havecomp relation to the current pop
if (neededCompetencies.length() != 0) { %>
  <mm:list nodes="<%= neededCompetencies %>" path="competencies">
    <% boolean needIntake = true;
       boolean passed = true; 
    %>
    <mm:node element="competencies" id="thiscompetency">
      <mm:related path="havecomp,pop" constraints="pop.number='$currentpop'">
        <% needIntake = false; %>
      </mm:related>
      <% if (needIntake) { 
           needIntake = false; %>
        <mm:relatednodes type="tests" role="comprel">
          <mm:import id="testNo" jspvar="thisIntake" reset="true"><mm:field name="number"/></mm:import>
          <%@include file="teststatus2.jsp"%>
          <mm:compare referid="teststatus" value="passed" inverse="true">
            <% needIntake = true; %>
          </mm:compare>
        </mm:relatednodes>
        <% if (!needIntake) { %>
          <mm:createrelation role="havecomp" source="currentpop" destination="thiscompetency" />
        <% } 
      } %>
    </mm:node>
  </mm:list>
<% } %>
</mm:node>

