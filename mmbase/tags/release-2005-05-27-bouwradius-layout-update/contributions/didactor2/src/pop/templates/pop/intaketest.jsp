<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<mm:content postprocessor="reducespace" expires="0">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">



<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>
<%@ include file="getids.jsp" %>

<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
  <div class="contentBody">

  <%

     final String SEPARATOR = "_";

  %> 
<%	String intakeCompetencies = ""; 
	String notpassedIntakes = ""; 
%>


<%-- find user's copybook --%>

<mm:import id="copybookNo"/>

<mm:node number="$user">

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

</mm:node> 

<mm:import id="gatekeeper" reset="true">1</mm:import>
<mm:import id="incompletetestNo" reset="true">-1</mm:import>
<mm:node number="$education">
  <mm:relatednodes type="tests" role="related">
    <mm:import id="testNo" reset="true"><mm:field name="number"/></mm:import>
    <%@include file="teststatus.jsp"%>
    <mm:compare referid="teststatus" value="incomplete">
       <mm:import id="incompletetestNo" reset="true"><mm:field name="number"/></mm:import>
    </mm:compare>
    <mm:compare referid="teststatus" value="failed">
       <mm:import id="gatekeeper" reset="true">0</mm:import>
    </mm:compare>
  </mm:relatednodes>
</mm:node>

<mm:compare referid="gatekeeper" value="0">
  <%-- "you failed the test for this education and are not allowed to continue, please contact your teacher / coach for advice" --%>
  <p>Je hebt de intake-toets voor deze opleiding niet gehaald en kunt daarom niet aan de opleiding beginnen. 
     Neem contact op met je coach of docent voor advies.</p>
<input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$referids,currentfolder"/>'" value="terug">
</mm:compare>
<mm:compare referid="gatekeeper" value="0" inverse="true">
  <mm:compare referid="incompletetestNo" value="-1" inverse="true">
    <%-- "you now start with test for this education" --%>
    <p>Om met deze opleiding te beginnen moet je de volgende intake-toets doen.</p>
    <mm:treeinclude page="/pop/buildtest.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>
    </mm:treeinclude> 
  </mm:compare>
  <mm:compare referid="incompletetestNo" value="-1">
    <%-- "you passed the test for this education and will now continue with the preassesment for the competencies developed 
         in this education" --%>
    <%@ include file="getprogress.jsp" %>
    
    <% if (!notpassedIntakes.equals("")) { %>

    <p>Je hebt de intake-toets voor deze opleiding gehaald. Je gaat nu door met de preassesment om te bepalen welke competenties je nog moet
       ontwikkelen binnen deze opleiding.</p>

    <%-- Take care: form name is used in JavaScript of the specific question jsp pages! --%>

    <form name="questionform" action="<mm:treefile page="/pop/intakerate.jsp" objectlist="$includePath" referids="$referids,currentfolder"/>" method="POST">
    <input type="hidden" name="command" value="intaketest">
    <mm:list nodes="<%= notpassedIntakes %>" path="tests">
      <mm:node element="tests">
        <mm:import id="testNo" reset="true"><mm:field name="number"/></mm:import>

        <h1><mm:field name="name"/></h1>
    
        <mm:relatednodes type="questions" max="1" comparator="SHUFFLE">

             <mm:import id="page">/education/<mm:nodeinfo type="type"/>/index.jsp</mm:import>

             <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">

               <mm:param name="question"><mm:field id="questionNo" name="number"/></mm:param>

	       <mm:param name="testnumber"><mm:write referid="testNo"/></mm:param>

             </mm:treeinclude>



             <input type="hidden" name="shown<mm:write referid="testNo"/>" value="<mm:field name="number"/>"/>

        </mm:relatednodes>

      <br/>

      <br/>
      </mm:node>
    </mm:list>
    <input type="hidden" name="intakes" value="<%= notpassedIntakes %>"/>
    <input type="submit" value="<di:translate id="buttontextdone">OK</di:translate>" class="formbutton"/>
    </form>

    <% } else { %>
      <p>Je bent klaar met de preassesment voor deze opleiding en kunt nu aan de opleiding beginnen</p>
      <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
          </mm:treefile>'" value="start" title="Begin met deze cursus">
    <% } %>

  </mm:compare>
</mm:compare>

</div>

</fmt:bundle>

</mm:cloud>

</mm:content>

