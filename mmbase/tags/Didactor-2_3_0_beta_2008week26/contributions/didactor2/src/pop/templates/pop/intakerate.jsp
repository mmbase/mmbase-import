<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="intakes" jspvar="intakes" required="true"/>


<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>
<%@include file="/education/wizards/roles_defs.jsp" %>
<mm:import id="editcontextname" reset="true">docent schermen</mm:import>
<%@include file="/education/wizards/roles_chk.jsp" %>

<mm:import externid="student" reset="true"><mm:write referid="user"/></mm:import>
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title>POP</title>
      <link rel="stylesheet" type="text/css" href="css/pop.css" />
    </mm:param>
  </mm:treeinclude>

  <% boolean isEmpty = true; 
     String msgString = "";
  %>

  <%@ include file="getids.jsp" %>

  <div class="rows">
    <div class="navigationbar">
      <div class="titlebar">
        <img src="<mm:treefile write="true" page="/gfx/icon_pop.gif" objectlist="$includePath" />" 
            width="25" height="13" border="0" title="<di:translate key="pop.popfull" />" alt="<di:translate key="pop.popfull" />" /> <di:translate key="pop.popfull" />
      </div>		
    </div>
    <%@include file="leftpanel.jsp" %> 

<%-- find student's copybook --%>
<mm:node number="$student">
   <%@include file="find_copybook.jsp" %>
</mm:node>


    <%-- right section --%>
    <div class="mainContent">
<div class="contentHeader"><di:translate key="pop.progressmonitor" />
  <%@include file="nameintitle.jsp" %>
</div>
  <div class="contentBody">


   

<mm:createnode type="madetests" id="madetest">
  <% long currentDate = System.currentTimeMillis() / 1000; %>
  <mm:setfield name="date"><%=currentDate%></mm:setfield>
  <mm:setfield name="score"><mm:write referid="TESTSCORE_INCOMPLETE"/></mm:setfield>
</mm:createnode>



<%-- build list of all shown intakes until now --%>

<mm:import id="list" jspvar="list" vartype="List"><mm:write referid="intakes"/></mm:import>

<%

    Iterator i = list.iterator();

    while (i.hasNext()) {

        String testNumber = (String) i.next();

        %>

  <%-- Examine different questions and save the given answers --%>

  <mm:import id="question" reset="true">shown<%= testNumber %></mm:import>
  <mm:import externid="$question" id="shownquestion" reset="true"/>
  <mm:node number="<%= testNumber %>" id="thistest">

    <% boolean hasIntakeresult = false; %>
    <mm:relatedcontainer path="intakeresults,copybooks">
      <mm:constraint field="copybooks.number" value="$copybookNo"/>
      <mm:related>
        <% hasIntakeresult = true; %>
      </mm:related>  
    </mm:relatedcontainer>

    <% if (!hasIntakeresult) { %>

      <mm:import id="question" reset="true">shown<mm:field name="number"/></mm:import>
      <mm:import externid="$question" id="shownquestion" jspvar="shownQuestion" reset="true"/>

    
      <mm:node number="<%= shownQuestion %>">
        <mm:import id="page" reset="true">/education/<mm:nodeinfo type="type"/>/rate<mm:nodeinfo type="type"/>.jsp</mm:import>

        <mm:treeinclude page="$page" objectlist="$includePath" referids="$popreferids">
          <mm:param name="question"><mm:write referid="shownquestion"/></mm:param>
          <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>
        </mm:treeinclude>
        <mm:remove referid="page"/>

        <mm:createnode type="intakeresults" id="intakeresult">
          <% long currentDate = System.currentTimeMillis() / 1000; %>
          <mm:setfield name="date"><%=currentDate%></mm:setfield>
        </mm:createnode>
        <mm:createrelation role="related" source="thistest" destination="intakeresult"/>
        <mm:node number="$copybookNo" id="thiscopybook"/>
        <mm:createrelation role="related" source="thiscopybook" destination="intakeresult"/>
 
        <mm:relatedcontainer path="givenanswers,madetests">
          <mm:constraint field="madetests.number" value="$madetest"/>
          <mm:related>
            <mm:field name="givenanswers.score" id="givenanswerscore" write="false" />
            <mm:node referid="intakeresult">
              <mm:compare referid="givenanswerscore" value="0">
                <mm:setfield name="score">0</mm:setfield>
              </mm:compare>
              <mm:compare referid="givenanswerscore" value="1">
                <mm:setfield name="score">1</mm:setfield>
              </mm:compare>
            </mm:node>
          </mm:related>  
        </mm:relatedcontainer>
      </mm:node> 
    <% } %>
  </mm:node> 
<% } %>

  <mm:node referid="madetest">
    <mm:relatednodescontainer type="givenanswers">
      <mm:relatednodes>
        <mm:maydelete>
          <mm:deletenode deleterelations="true"/>
        </mm:maydelete>
      </mm:relatednodes>
    </mm:relatednodescontainer>
    <mm:maydelete>
      <mm:deletenode deleterelations="true"/>
    </mm:maydelete>
  </mm:node>

  <%@ include file="intakecheck.jsp" %>

  <p><di:translate key="pop.intakemsgyouready" /></p>
  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$popreferids">
      </mm:treefile>'" value="start" title="<di:translate key="pop.begincoursebutton" />">
  </div>
</div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$popreferids" />
</mm:cloud>
</mm:content>
