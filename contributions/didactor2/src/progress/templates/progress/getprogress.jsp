<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>


<%-- report either the current user's progress, or the one given by "student" argument --%>
<mm:import externid="student" id="student"><mm:write referid="user"/></mm:import>


<mm:node number="$student" notfound="skip">

   <%-- find user's copybook --%>
   <%@include file="find_copybook.jsp"%>

   <%
      int nof_tests= 0;
      int nof_tests_passed= 0;
   %>

   <mm:node number="$education" notfound="skip">
      <mm:import id="previousnumber"><mm:field name="number"/></mm:import>

      <mm:relatednodescontainer type="learnobjects" role="posrel">
         <mm:sortorder field="posrel.pos" direction="up"/>

         <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">
            <mm:import id="nodetype"><mm:nodeinfo type="type" /></mm:import>

            <mm:compare referid="nodetype" value="tests">
               <%
                  nof_tests++;
               %>
<%--            <mm:import id="teststatus" reset="true" jspvar="testStatus" escape="reducespace"><mm:treeinclude page="/progress/teststatus.jsp" objectlist="$includePath" referids="$referids"><mm:param name="copybookNo"><mm:write referid="copybookNo"/></mm:param><mm:param name="testNo"><mm:field name="number"/></mm:param></mm:treeinclude></mm:import>

            <%

                testStatus = testStatus.trim();

            %><mm:import id="teststatus" reset="true" jspvar="testStatus" escape="reducespace"><%= testStatus %></mm:import>--%>

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
         //  System.err.println("tests_passed="+nof_tests_passed+", nof_tests="+nof_tests+", progress =" +progress);
      %>
      <%=progress%>
   </mm:node>
</mm:node>

</mm:cloud>
</mm:content>

