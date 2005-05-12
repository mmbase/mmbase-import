<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.*" %>

<mm:content postprocessor="reducespace" expires="0">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">



<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>
<%@ include file="getids.jsp" %>

<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
  <div class="contentBody">

<mm:node number="$education">

<b>Preassessment test voor <mm:field name="name" write="true"/></b>

<table class="font">

  <% List questions = new ArrayList(); %>
  <mm:relatednodes type="tests" role="related">
    <mm:import id="testNo" reset="true"><mm:field name="number"/></mm:import>
    <mm:relatednodes type="questions" role="posrel" orderby="posrel.pos" directions="UP">
      <mm:field name="number" jspvar="questionNum" vartype="String">
        <% questions.add(questionNum); %>
      </mm:field>
    </mm:relatednodes>
  </mm:relatednodes>

<tr>

<th></th>

    <mm:node number="progresstextbackground">

    <th>

        <img src="<mm:image template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'Score')+rotate(90)"/>">

    </th>

     <th>

        <img src="<mm:image template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'As opposed to average')+rotate(90)"/>">

    </th>

    </mm:node>

<% Iterator questionIterator = questions.iterator();

   while (questionIterator.hasNext()) {

       String questionNum = (String) questionIterator.next();

        %>

       <mm:node number="<%= questionNum %>">

        <mm:field name="title" jspvar="title" vartype="String">

            <% title  = title.replaceAll("\\s+"," ").replaceAll("\"","''"); %>

         <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,"<%= title %>")+rotate(90)</mm:import>

         </mm:field>

         <mm:node number="progresstextbackground">

         <th><img src="<mm:image template="$template"/>"></th>

         </mm:node>

    </mm:node>

<% } %>

   <mm:node referid="class">
     <mm:relatednodes type="people">
       <mm:import id="studentnumber" reset="true"><mm:field name="number"/></mm:import>
       <di:hasrole role="student" referid="studentnumber">
         <mm:treeinclude page="/pop/coachpre_row.jsp" objectlist="$includePath" referids="$referids">
           <mm:param name="student"><mm:field name="number"/></mm:param>
           <mm:param name="testNo"><mm:write referid="testNo"/></mm:param>
         </mm:treeinclude>
       </di:hasrole>
     </mm:relatednodes>
   </mm:node> 

</table>

</mm:node>


  </div>
</fmt:bundle>

</mm:cloud>

</mm:content>

