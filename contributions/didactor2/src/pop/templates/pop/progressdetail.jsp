<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.*" %>

<mm:content postprocessor="reducespace" expires="0">

<mm:cloud method="delegate" jspvar="cloud">



<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>
<%@ include file="getids.jsp" %>

<mm:import externid="direct_connection" reset="true">false</mm:import>
  <div class="contentBody">

<mm:node number="$education">

<b><mm:field name="name" write="true"/></b>

<table class="font">

<% List tests = new ArrayList(); %>

<mm:relatednodescontainer type="learnobjects" role="posrel">

    <mm:sortorder field="posrel.pos" direction="up"/>

    <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">

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
        <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'<di:translate key="pop.progress" />')+rotate(90)</mm:import>
        <img src="<mm:image template="$template"/>">
    </th>

    <th>
        <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'<di:translate key="pop.numberofvisits" />')+rotate(90)</mm:import>
        <img src="<mm:image template="$template"/>">
    </th>

    <th>
        <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'<di:translate key="pop.timeofvisits" />')+rotate(90)</mm:import>
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

            <% name  = name.replaceAll("\\s+","_").replaceAll("\"","''"); %>

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
                <mm:param name="direct_connection"><mm:write referid="direct_connection"/></mm:param>
         </mm:treeinclude>
    </mm:node>



</table>

<% if (showNextLink) { %>

<span style="float: right"><a href="<mm:treefile  page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,currentfolder">

    <mm:param name="startAt"><%= startAt + 15 %></mm:param>
    <mm:param name="popcmd">detail</mm:param>
    <mm:param name="direct_connection"><mm:write referid="direct_connection"/></mm:param>

</mm:treefile>"><di:translate key="pop.nextresults" /></a></span>

<% }

   if (showPrevLink) { %>

<a href="<mm:treefile  page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,currentfolder">

    <mm:param name="startAt"><%= startAt - 15 %></mm:param>
    <mm:param name="popcmd">detail</mm:param>
    <mm:param name="direct_connection"><mm:write referid="direct_connection"/></mm:param>

</mm:treefile>"><di:translate key="pop.prevresults" /></a>

<% } %>

</mm:node>


  </div>

</mm:cloud>

</mm:content>

