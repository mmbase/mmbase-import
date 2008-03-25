<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="testNo" required="true"/>
<mm:import externid="studentNo" required="true"/>

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>


<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Voortgang -> Correctie</title>
  </mm:param>
</mm:treeinclude>

<div class="rows">
<div class="navigationbar">
  <div class="titlebar">
    <!-- TODO, this is dutch -->
    Voortgang -&gt; Correctie
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
    &nbsp;
  </div>
  <div class="folderBody">
    &nbsp;
  </div>
</div>



<div class="mainContent">
  <div class="contentHeader">

<%--    Some buttons working on this folder--%>
  </div>
  <div class="contentBody">

<di:may component="education" action="rate" referids="studentNo@subject">

<%-- find user's copybook --%>
<mm:node number="$studentNo">
   <%@include file="find_copybook.jsp"%>
</mm:node>


<%-- Take care: form name is used in JavaScript of the specific question jsp pages! --%>
<form name="scoreform" action="<mm:treefile page="/education/tests/rateopen2.jsp" objectlist="$includePath" referids="$referids"/>" method="POST">


<mm:node number="$testNo">
  <mm:field name="showtitle">
    <mm:compare value="1">
      <h1><mm:field name="name"/></h1>
    </mm:compare>
  </mm:field>
<%-- Get all the questions of the test --%>
  <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
    <mm:constraint field="madetests.score" referid="TESTSCORE_TBS"/>
    <mm:constraint field="copybooks.number" referid="copybookNo"/>
    <mm:relatednodes>
      <input type="hidden" name="madetest" value="<mm:field name="number"/>"/>
      <mm:relatednodes type="givenanswers">
        <mm:relatednodes type="questions">
          <mm:import id="questiontype"><mm:nodeinfo type="type"/></mm:import>
          <mm:field id="questiontext" name="text" write="false"/>
        </mm:relatednodes>
        <mm:compare referid="questiontype" value="openquestions">
          <mm:field id="score" name="score" write="false"/>
          <%-- THIS IS IN DUTCH --%>
          <mm:compare referid="score" referid2="TESTSCORE_TBS">
            <mm:field id="givenanswerNo" name="number" write="false"/>
            Vraag: <mm:write referid="questiontext" escape="none"/><br/>
            Gegeven antwoord: <mm:field name="text"/><br/>
            <input type="radio" name="<mm:write referid="givenanswerNo"/>" value="1"/>Goed
            <br/>
            <input type="radio" name="<mm:write referid="givenanswerNo"/>" value="0"/>Fout
            <br/>
            <input type="text" size="100" name="feedback<mm:write referid="givenanswerNo"/>"/>
            <p/> <%-- WTF --%>
            <mm:remove referid="givenanswerNo"/>
          </mm:compare>
          <mm:remove referid="score"/>
        </mm:compare>
        <mm:remove referid="questiontext"/>
        <mm:remove referid="questiontype"/>
      </mm:relatednodes>
    </mm:relatednodes>
  </mm:relatednodescontainer>
</mm:node>

<input type="hidden" name="testNo" value="<mm:write referid="testNo"/>"/>
<input type="hidden" name="studentNo" value="<mm:write referid="studentNo"/>"/>
<input type="submit" value="<di:translate key="education.ok" />"/>

<%--
TODO: make this operate
<div class="button1">
<a href="javascript:submitForm();"><di:translate key="education.ok" /></a>
</div>
--%>


</form>
 </div>
</div>
</di:may>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>

