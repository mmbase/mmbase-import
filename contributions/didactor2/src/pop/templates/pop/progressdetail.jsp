<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@page import="java.util.*" %>

<mm:content postprocessor="reducespace" expires="0">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">



<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>
<%@ include file="getids.jsp" %>

   <%

      String bundlePOP = null;

   %>

   <mm:write referid="lang_code" jspvar="sLangCode" vartype="String" write="false">

      <%

         bundlePOP = "nl.didactor.component.pop.PopMessageBundle_" + sLangCode;

      %>

   </mm:write>

<fmt:bundle basename="<%= bundlePOP %>">
  <div class="contentBody">

<mm:node number="$education">

<b><mm:field name="name" write="true"/></b>

<table class="font">

<% List tests = new ArrayList(); %>

<mm:relatednodescontainer type="learnobjects" role="posrel">

    <mm:sortorder field="posrel.pos" direction="up"/>

    <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" direction="up">

      <mm:import id="nodetype" reset="true"><mm:nodeinfo type="type" /></mm:import>

      <mm:compare referid="nodetype" value="tests">

        <mm:field name="number" jspvar="testNum" vartype="String">

            <% tests.add(testNum); %>

        </mm:field>

     </mm:compare>

    </mm:tree>

</mm:relatednodescontainer>

<mm:import id="startAt" externid="startAt" jspvar="sStartAt" vartype="Integer">0</mm:import>

<tr>

<th></th>

    <mm:node number="progresstextbackground">

    <th>
        <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'<fmt:message key="Progress"/>')+rotate(90)</mm:import>
        <img src="<mm:image template="$template"/>">
    </th>

    <th>
        <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'<fmt:message key="NumberOfVisits"/>')+rotate(90)</mm:import>
        <img src="<mm:image template="$template"/>">
    </th>

    <th>
        <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'<fmt:message key="TimeOfVisits"/>')+rotate(90)</mm:import>
        <img src="<mm:image template="$template"/>">
    </th>

    </mm:node>

<% int testCounter = 0;

   int startAt = sStartAt.intValue();

   boolean showPrevLink = false;

   if (startAt > 0) {

       showPrevLink = true;

   }

   boolean showNextLink = false;

   Iterator testIterator = tests.iterator();

   while (testIterator.hasNext()) {

       String testNum = (String) testIterator.next();

       if ( testCounter++ < startAt ) {

           continue;

       }

       if ( testCounter > startAt + 15) {

           showNextLink = true;

           break;

       }

        %>

       <mm:node number="<%= testNum %>">

        <mm:field name="name" jspvar="name" vartype="String">

            <% name  = name.replaceAll("\\s+"," ").replaceAll("\"","''"); %>

         <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,"<%= name %>")+rotate(90)</mm:import>

         </mm:field>

         <mm:node number="progresstextbackground">

         <th><img src="<mm:image template="$template"/>"></th>

         </mm:node>

    </mm:node>

<% } %>

    <mm:node number="$student">

         <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$popreferids,startAt">

                <mm:param name="user"><mm:field name="number"/></mm:param>

         </mm:treeinclude>

    </mm:node>



</table>

<% if (showNextLink) { %>

<span style="float: right"><a href="<mm:treefile  page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,currentfolder">

    <mm:param name="startAt"><%= startAt + 15 %></mm:param>
    <mm:param name="command">detail</mm:param>

</mm:treefile>"><fmt:message key="NextResults"/></a></span>

<% }

   if (showPrevLink) { %>

<a href="<mm:treefile  page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,currentfolder">

    <mm:param name="startAt"><%= startAt - 15 %></mm:param>
    <mm:param name="command">detail</mm:param>

</mm:treefile>"><fmt:message key="PrevResults"/></a>

<% } %>

</mm:node>


  </div>
</fmt:bundle>

</mm:cloud>

</mm:content>

