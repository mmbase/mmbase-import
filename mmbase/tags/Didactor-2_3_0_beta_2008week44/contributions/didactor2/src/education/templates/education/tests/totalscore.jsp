<jsp:root
    version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0" >

  <jsp:directive.include file="/education/tests/definitions.jsp"  />

  <!--
       now recalculate the score of the made test, using the score in the given answers objects
       TODO, this could typically be implemented in eventhandlers or so. JSP is not a very robust location.
  -->

  <mm:import externid="tests" required="true"/>
  <mm:import externid="madetest" required="true"/>

  <jsp:scriptlet>
    int points= 0;
  </jsp:scriptlet>

  <mm:import id="totalscore"><mm:write referid="TESTSCORE_INCOMPLETE"/></mm:import>

  <mm:node referid="madetest">
    <mm:relatednodes type="givenanswers">
      <mm:field name="score" write="false" vartype="integer" jspvar="score">
        <c:choose>
          <c:when test="${_ ge 1}">
            <jsp:scriptlet>points += score;</jsp:scriptlet>
          </c:when>
          <c:when test="${_ eq TESTSCORE_TBS}">
            <mm:log>SCORE is sTBS, resetting complete score</mm:log>
            <mm:remove referid="totalscore"/>
            <mm:import id="totalscore"><mm:write referid="TESTSCORE_TBS"/></mm:import>
          </c:when>
        </c:choose>
      </mm:field>
    </mm:relatednodes>


    <mm:compare referid="totalscore" referid2="TESTSCORE_TBS">
      <mm:setfield name="score"><mm:write referid="TESTSCORE_TBS"/></mm:setfield>
    </mm:compare>
    <mm:compare referid="totalscore" referid2="TESTSCORE_TBS" inverse="true">
      <mm:setfield name="score"><jsp:expression>points</jsp:expression></mm:setfield>
    </mm:compare>
  </mm:node>
</jsp:root>
