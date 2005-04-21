<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>POP</title>
    <link rel="stylesheet" type="text/css" href="css/pop.css" />
  </mm:param>
</mm:treeinclude>

<!-- TODO where are the different roles described -->
<!-- TODO different things to do with different roles? -->

<% boolean isEmpty = true; 
   String msgString = "";
%>

<%@ include file="getids.jsp" %>

<%@ include file="leftpanel.jsp" %>

<%-- right section --%>
<div class="mainContent">
<mm:compare referid="command" value="getinvite">
  <mm:import id="currentpop" reset="true">0</mm:import>
</mm:compare>
<mm:compare referid="currentpop" value="-1">
  <div class="contentBody"> 
    <p>Er is geen POP voor jou aangemaakt. Neem contact op met de systeembeheerder om een POP voor je aan te maken.</p>
  </div>
</mm:compare>
<mm:compare referid="currentpop" value="-1" inverse="true">
<mm:compare referid="currentfolder" value="-1">
  <div class="contentHeader">Competenties <mm:compare referid="currentprofile" value="-1" inverse="true"
      ><mm:node number="$currentprofile"><mm:field name="name"/></mm:node></mm:compare>
  </div>
  <%@ include file="todo.jsp" %>
  <mm:compare referid="command" value="continue">
    <mm:remove referid="command"/>
    <mm:import id="command">editcomp</mm:import>
  </mm:compare>
  <mm:compare referid="command" value="savecomp">
    <%@ include file="savecomp.jsp" %>
    <% msgString = "Uw zelfbeoordeling is aangemaakt"; %>
    <mm:remove referid="command"/>
    <mm:import id="command">no</mm:import>
  </mm:compare>
  <mm:compare referid="command" value="sendinvite">
    <%@ include file="sendinvite.jsp" %>
    <% msgString = "De uitnodiging voor een beoordeling over deze competentie is verstuurd"; %>
    <mm:remove referid="command"/>
    <mm:import id="command">editcomp</mm:import>
  </mm:compare>
  <mm:compare referid="command" value="invite">
    <%@ include file="invite.jsp" %>
    <mm:remove referid="command"/>
    <mm:import id="command">-1</mm:import>
  </mm:compare>
  <mm:compare referid="command" value="getinvite">
    <%@ include file="getinvite.jsp" %>
    <mm:remove referid="command"/>
    <mm:import id="command">-1</mm:import>
  </mm:compare>
  <mm:compare referid="command" value="sendfeedback">
    <%@ include file="sendfeedback.jsp" %>
    <mm:remove referid="command"/>
    <mm:import id="command">no</mm:import>
  </mm:compare>
  <mm:compare referid="command" value="editcomp">
    <jsp:include page="compedit.jsp">
      <jsp:param name="msg" value="<%= msgString %>"/>
    </jsp:include>
  </mm:compare>
  <mm:compare referid="command" value="no">
    <jsp:include page="comptable.jsp">
      <jsp:param name="msg" value="<%= msgString %>"/>
    </jsp:include>
  </mm:compare>
</mm:compare>
<mm:compare referid="currentfolder" value="1">
  <div class="contentHeader">Voortgangsmonitor</div>
  <mm:compare referid="command" value="intake">
    <mm:import id="competencies" jspvar="competencies" />
    <jsp:include page="intaketest.jsp">
      <jsp:param name="competencies" value="<%= competencies %>"/>
    </jsp:include>
    <mm:remove referid="command"/>
    <mm:import id="command">-1</mm:import>
  </mm:compare>
  <mm:compare referid="command" value="no">
    <jsp:include page="voortgang.jsp">
      <jsp:param name="msg" value="<%= msgString %>"/>
    </jsp:include>
  </mm:compare>
</mm:compare>
<mm:compare referid="currentfolder" value="2">
  <div class="contentHeader">Persoonlijke taken</div>
  <%@ include file="todo.jsp" %>
  <mm:compare referid="command" value="-1" inverse="true">
    <jsp:include page="todolist.jsp">
      <jsp:param name="msg" value="<%= msgString %>"/>
    </jsp:include>
  </mm:compare>
</mm:compare>
  </div>
</div>
</mm:compare>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</fmt:bundle>
</mm:cloud>
</mm:content>
