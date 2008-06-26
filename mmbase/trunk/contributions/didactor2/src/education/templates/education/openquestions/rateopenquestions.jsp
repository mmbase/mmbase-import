<jsp:root
    version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0" >
  <mm:content postprocessor="none">
    <mm:cloud rank="didactor user">

      <mm:import externid="question" required="true"/>
      <mm:import externid="madetest" required="true"/>

      <jsp:directive.include file="/education/tests/definitions.jsp" />

      <mm:node number="$question" id="my_question">

        <!-- Which answer has been given on the question -->
        <mm:import externid="$question" id="givenanswer" />

        <mm:relatednodes type="openanswers" id="openanswers" />

        <mm:field name="type_of_score" write="false">
          <c:choose>
            <c:when test="${_ eq 2}">
              <!-- Save the answer if type_of_score=2, no scoring -->
              <mm:createnode type="givenanswers" id="my_givenanswers">
                <mm:setfield name="text"><mm:write referid="givenanswer"/></mm:setfield>
                <mm:setfield name="score"><mm:write referid="TESTSCORE_COR"/></mm:setfield>
              </mm:createnode>
            </c:when>
            <c:when test="${_ eq 0 or fn:length(openanswers) eq 0}">
              <!-- Save the answer if type_of_score=0, will be checked by coach-->
              <mm:createnode type="givenanswers" id="my_givenanswers">
                <mm:setfield name="text"><mm:write referid="givenanswer"/></mm:setfield>
                <mm:setfield name="score"><mm:write referid="TESTSCORE_TBS"/></mm:setfield>
              </mm:createnode>
            </c:when>
            <c:otherwise>
              <!-- Save the answer if type_of_score=1, will be checked using example answers -->
              <mm:listnodes referid="openanswers">
                <mm:field name="text" id="text">
                  <mm:compare referid="givenanswer" referid2="text">
                    <mm:createnode type="givenanswers" id="my_givenanswers">
                      <mm:setfield name="text"><mm:write referid="givenanswer"/></mm:setfield>
                      <mm:setfield name="score"><mm:write referid="TESTSCORE_COR"/></mm:setfield>
                    </mm:createnode>
                  </mm:compare>
                  <mm:compare referid="givenanswer" referid2="text" inverse="true">
                    <mm:createnode type="givenanswers" id="my_givenanswers">
                      <mm:setfield name="text"><mm:write referid="givenanswer"/></mm:setfield>
                      <mm:setfield name="score"><mm:write referid="TESTSCORE_WR"/></mm:setfield>
                    </mm:createnode>
                  </mm:compare>
                </mm:field>
              </mm:listnodes>
            </c:otherwise>
          </c:choose>

        </mm:field>

        <mm:createrelation role="related" source="madetest" destination="my_givenanswers"/>
        <mm:createrelation role="related" source="question" destination="my_givenanswers"/>

      </mm:node>

    </mm:cloud>
  </mm:content>
</jsp:root>
