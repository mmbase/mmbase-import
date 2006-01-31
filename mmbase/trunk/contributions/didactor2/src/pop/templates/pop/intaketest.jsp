<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>
<%@ include file="getids.jsp" %>
  <div class="contentBody">

  <%

     final String SEPARATOR = "_";

  %> 
<%	String intakeCompetencies = ""; 
	String notpassedIntakes = ""; 
%>

<%-- find user's copybook --%>
<mm:node number="$student">
   <%@include file="find_copybook.jsp"%>
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
  <p><di:translate key="pop.intakemsgyoufailedtest" /></p>
<input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,currentfolder"/>'" value="terug">
</mm:compare>
<mm:compare referid="gatekeeper" value="0" inverse="true">
  <mm:compare referid="incompletetestNo" value="-1" inverse="true">
    <p><di:translate key="pop.intakemsgyounowstartwithintake" /></p>
    <mm:treeinclude page="/pop/buildtest.jsp" objectlist="$includePath" referids="$popreferids">
      <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>
    </mm:treeinclude> 
  </mm:compare>
  <mm:compare referid="incompletetestNo" value="-1">
    <%@ include file="getprogress.jsp" %>
    
    <% if (!notpassedIntakes.equals("")) { %>

    <p><di:translate key="pop.intakemsgyoupassedandcontinue" /></p>

    <%-- Take care: form name is used in JavaScript of the specific question jsp pages! --%>

    <form name="questionform" action="<mm:treefile page="/pop/intakerate.jsp" objectlist="$includePath" referids="$popreferids,currentfolder"/>" method="POST">
    <input type="hidden" name="command" value="intaketest">
    <mm:list nodes="<%= notpassedIntakes %>" path="tests">
      <mm:node element="tests">
        <mm:import id="testNo" reset="true"><mm:field name="number"/></mm:import>

        <h1><mm:field name="name"/></h1>
    
        <mm:relatednodes type="questions" max="1" comparator="SHUFFLE">

             <mm:import id="page">/education/<mm:nodeinfo type="type"/>/index.jsp</mm:import>

             <mm:treeinclude page="$page" objectlist="$includePath" referids="$popreferids">

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
    <input type="submit" value="<di:translate key="education.buttontextdone" />" class="formbutton"/>
    </form>

    <% } else { %>
      <p><di:translate key="pop.intakemsgyouready" /></p>
      <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$popreferids">
          </mm:treefile>'" value="start" title="<di:translate key="pop.begincoursebutton" />">
    <% } %>

  </mm:compare>
</mm:compare>

</div>


</mm:cloud>

</mm:content>

