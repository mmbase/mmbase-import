<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


<mm:content postprocessor="reducespace" expires="0">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">



<mm:import externid="intakes" jspvar="intakes" required="true"/>

<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>
<%@ include file="getids.jsp" %>

<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
  <div class="contentBody">

  <%

     final String SEPARATOR = "_";

  %> 


<html>

<head>

   <title>Content</title>

   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath"/>"/>

</head>

<body>





<%-- Take care: form name is used in JavaScript of the specific question jsp pages! --%>

<form name="questionform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$referids,currentfolder"/>" method="POST">
<input type="hidden" name="command" value="intaketest">
<mm:list nodes="<%= intakes %>" path="tests">
  <mm:node element="tests">
    <mm:import id="testNo" reset="true"><mm:field name="number"/></mm:import>

    <h1><mm:field name="name"/></h1>

    <mm:relatednodes type="questions" max="1" comparator="SHUFFLE">

             <mm:import id="page">/education/<mm:nodeinfo type="type"/>/index.jsp</mm:import>

             <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">

               <mm:param name="question"><mm:field name="number"/></mm:param>

	       <mm:param name="testnumber"><mm:write referid="testNo"/></mm:param>

             </mm:treeinclude>



             <input type="hidden" name="shown<mm:field name="number"/>" value="<mm:field name="number"/>"/>



             <%-- See default value of questionsShowed --%>


  </mm:relatednodes>

  <br/>

  <br/>
  </mm:node>
</mm:list>
<input type="submit" value="<di:translate id="buttontextdone">OK</di:translate>" class="formbutton"/>
</form>
  </div>



</body>

</html>

</fmt:bundle>

</mm:cloud>

</mm:content>

