<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="testNo" required="true"/>
<mm:import externid="studentNo" required="true"/>
<mm:import externid="madetest" required="true"/>

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<di:may component="education" action="rate" referids="studentNo@subject">

<mm:import id="correctiontext">
  <mm:node number="$madetest">
    <mm:relatednodes type="givenanswers">
      <mm:relatednodes type="questions">
        <%-- Set property with the kind of question --%>
        <mm:import id="questiontype"><mm:nodeinfo type="type"/></mm:import>
        <mm:field id="questiontext" name="text" write="false"/>
      </mm:relatednodes>
      <mm:compare referid="questiontype" value="openquestions">

        <mm:field id="score" name="score" write="false"/>
        <mm:compare referid="score" referid2="TESTSCORE_TBS">
          <mm:field id="givenanswerNo" name="number" write="false"/>
          <mm:import id="answer" externid="$givenanswerNo"/>
          <mm:present referid="answer" inverse="true">
            Score voor vraag <mm:write referid="questiontext" escape="none"/> niet gegeven.<br/>
            <mm:import id="incompleterating"/>
          </mm:present>
          <mm:present referid="answer">
            <mm:setfield name="score"><mm:write referid="answer"/></mm:setfield>
            <mm:remove referid="answer"/>
          </mm:present>

          <mm:import id="feedbackId">feedback<mm:write referid="givenanswerNo"/></mm:import>
          <mm:import id="feedbackText" externid="$feedbackId"/>
          <mm:present referid="feedbackText">
            <mm:isnotempty referid="feedbackText">
              <mm:createnode id="feedbackNo" type="feedback">
                 <mm:setfield name="text"><mm:write referid="feedbackText"/></mm:setfield>
              </mm:createnode>
              <mm:createrelation role="related" source="givenanswerNo" destination="feedbackNo"/>
              <mm:remove referid="feedbackNo"/>
            </mm:isnotempty>
            <mm:remove referid="feedbackText"/>
          </mm:present>
        </mm:compare>
        <mm:remove referid="score"/>
      </mm:compare>
      <mm:remove referid="questiontext"/>
    </mm:relatednodes>
  </mm:node>
</mm:import>

<%-- open answers are scored as well now --%>
<%-- recompute total score --%>
<%-- totalscore is resistent against score not open --%>
<mm:treeinclude page="/education/tests/totalscore.jsp"  objectlist="$includePath" referids="$referids">
  <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>
  <mm:param name="tests"><mm:write referid="testNo"/></mm:param>
</mm:treeinclude>

<mm:present referid="incompleterating">

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <!-- TODO, this is dutch -->
    <title>Voortgang -> Correctie</title>
  </mm:param>
</mm:treeinclude>

<div class="rows">
<div class="navigationbar"><div class="titlebar">Voortgang -> Correctie</div></div>
<div class="folders"><div class="folderHeader">&nbsp;</div><div class="folderBody">&nbsp;</div></div>
<div class="mainContent"><div class="contentHeader"></div><div class="contentBody">

  <mm:write referid="correctiontext" escape="none"/>
		<div class="button1">
                  <a href="<mm:treefile page="/education/tests/rateopen.jsp" objectlist="$includePath" referids="$referids">
               <mm:param name="testNo"><mm:write referid="testNo"/></mm:param>
               <mm:param name="studentNo"><mm:write referid="studentNo"/></mm:param>
              </mm:treefile>"><di:translate key="education.complete" /></a>
              </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</body>
</html>
</mm:present>

<!-- aarch, how can there follow code after </html> ? -->
<mm:notpresent referid="incompleterating">
  <mm:treeinclude page="/progress/index.jsp" objectlist="$includePath" referids="$referids"/>
</mm:notpresent>

</di:may>

</mm:cloud>
</mm:content>
