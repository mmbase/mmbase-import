<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<%-- find user's copybook --%>
<mm:import id="copybookNo"/>
<mm:node number="$user" notfound="skip">
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


<% int nof_tests= 0;
   int nof_tests_passed= 0;
%>
<mm:node number="$education" notfound="skip">
  <mm:import id="previousnumber"><mm:field name="number"/></mm:import>
  <mm:relatednodescontainer type="learnobjects" role="posrel">
    <mm:sortorder field="posrel.pos" direction="up"/>
    <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" direction="up">

      <mm:import id="nodetype"><mm:nodeinfo type="type" /></mm:import>
      <mm:compare referid="nodetype" value="tests">

<% nof_tests= nof_tests+1;
%>
        <mm:import id="passed">false</mm:import>
        <mm:field id="requiredscore" name="requiredscore" write="false"/>

        <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
          <mm:constraint field="madetests.score" referid="TESTSCORE_INCOMPLETE" inverse="true"/>
          <mm:constraint field="madetests.score" referid="TESTSCORE_TBS" inverse="true"/>
          <mm:constraint field="copybooks.number" referid="copybookNo"/>
          <mm:relatednodes>
              <mm:field name="score" id="madetestscore" write="false"/>
              <%-- if madestestscore larger or equal than requiredscore --%>
              <mm:islessthan referid="madetestscore" referid2="requiredscore" inverse="true">
                <mm:remove referid="passed"/>
                <mm:import id="passed">true</mm:import>
              </mm:islessthan>
              <mm:remove referid="madetestscore"/>

          </mm:relatednodes>
        </mm:relatednodescontainer>
        <mm:compare referid="passed" value="true">
<%        nof_tests_passed= nof_tests_passed + 1;
%>
        </mm:compare>
        <mm:remove referid="passed"/> 
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
