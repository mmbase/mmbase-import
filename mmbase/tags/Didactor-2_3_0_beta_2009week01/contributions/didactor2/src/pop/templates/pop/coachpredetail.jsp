<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.*" %>

<mm:content postprocessor="reducespace" expires="0">

<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>
<%@ include file="getids.jsp" %>
<% String classrels = ""; %>

<mm:compare referid="whatselected" value="class">
  <mm:list nodes="$class" path="classes,classrel,people">
    <mm:field name="classrel.number" jspvar="classrelNum" vartype="String">
      <% classrels += classrelNum + ","; %>
    </mm:field>
  </mm:list>
</mm:compare>

<mm:compare referid="whatselected" value="wgroup">
  <mm:import externid="wgroup"/>
  <mm:list nodes="$wgroup" path="workgroups,related,people,classrel,classes">
    <mm:field name="classrel.number" jspvar="classrelNum" vartype="String">
      <% classrels += classrelNum + ","; %>
    </mm:field>
  </mm:list>
</mm:compare>

<div class="contentBody">

<mm:list nodes="<%= classrels %>" path="classrel,copybooks,madetests,tests" fields="tests.number" distinct="true">
  <% List questions = new ArrayList(); %>
  <mm:node element="tests">
    <mm:import id="testNo"><mm:field name="number"/></mm:import>
    <b>Test resultaten voor <mm:field name="name"/></b>
    <mm:relatednodes type="questions" role="posrel" orderby="posrel.pos" directions="UP">
      <mm:field name="number" jspvar="questionNum" vartype="String">
        <% questions.add(questionNum); %>
      </mm:field>
    </mm:relatednodes>
    <table class="font">
      <tr>
        <th></th>
        <mm:node number="progresstextbackground">
          <th>
            <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'<di:translate key="pop.testresultscore" />')+rotate(90)</mm:import>
            <img src="<mm:image template="$template"/>">
          </th>
          <th>
            <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'<di:translate key="pop.testresultasaverage" />')+rotate(90)</mm:import>
            <img src="<mm:image template="$template"/>">
          </th>
        </mm:node>
        <% Iterator questionIterator = questions.iterator();
           while (questionIterator.hasNext()) {
               String questionNum = (String) questionIterator.next();
        %>
               <mm:node number="<%= questionNum %>">
                 <mm:field name="title" jspvar="title" vartype="String">
                   <% title  = title.replaceAll("\\s+","_").replaceAll("\"","''"); %>
                   <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,"<%= title %>")+rotate(90)</mm:import>
                 </mm:field>
                 <mm:node number="progresstextbackground">
                   <th><img src="<mm:image template="$template"/>"></th>
                 </mm:node>
               </mm:node>

        <% } %>
      </tr>
      <mm:list nodes="<%= classrels %>" path="classrel,copybooks,madetests,tests" constraints="tests.number='$testNo'">
        <mm:import id="madetestNo"><mm:field name="madetests.number"/></mm:import>
        <mm:import id="classrelNo"><mm:field name="classrel.number"/></mm:import>
        <mm:list path="classes,classrel,people,related,roles" constraints="classrel.number='$classrelNo' AND roles.name='student'">
          <mm:import id="studentNo"><mm:field name="people.number"/></mm:import>
          <mm:treeinclude page="/pop/coachpre_row.jsp" objectlist="$includePath" referids="$referids">
            <mm:param name="testNo"><mm:write referid="testNo"/></mm:param>
            <mm:param name="madetestNo"><mm:write referid="madetestNo"/></mm:param>
            <mm:param name="studentNo"><mm:write referid="studentNo"/></mm:param>
          </mm:treeinclude>
        </mm:list>
      </mm:list>
    </table>
    <br/><br/><br/>
  </mm:node>
</mm:list>







  </div>

</mm:cloud>

</mm:content>

