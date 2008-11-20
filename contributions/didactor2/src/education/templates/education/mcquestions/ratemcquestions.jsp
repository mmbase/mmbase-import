<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0" >
  <mm:content
      expires="0"
      postprocessor="reducespace">

    <mm:cloud method="delegate">

      <mm:import externid="question" required="true"/>
      <mm:import externid="madetest" required="true"/>

      <mm:import externid="answernode" />


      <!--
      Multiple choice questions
      types:
      0: only 1 answer can be selected
      1: multiple answers can be selected
      -->

    <mm:node number="$question" id="my_question">

      <mm:log>Rating for ${madetest}</mm:log>

      <mm:notpresent referid="answernode">
        <mm:remove referid="answernode" />
        <mm:createnode id="answernode" type="givenanswers" />
        <mm:createrelation role="related" source="madetest" destination="answernode" />
        <mm:createrelation role="related" source="question" destination="answernode" />
      </mm:notpresent>
      <mm:present referid="answernode">
        <mm:node id="answernode" referid="answernode">
          <mm:listrelations type="mcanswers">
            <mm:deletenode />
          </mm:listrelations>
        </mm:node>
      </mm:present>

      <mm:node referid="answernode" id="my_givenanswers">
        <mm:setfield name="score">0</mm:setfield>
      </mm:node>


      <mm:field name="type" write="false">
        <c:choose>
          <c:when test="${_ eq 0}">
            <!-- Only 1 answer is given -->

            <mm:import externid="${question}" id="givenanswer" />


            <mm:present referid="givenanswer">
              <mm:node referid="givenanswer">
                <mm:field id="questioncorrect" name="correct" write="false"/>
              </mm:node>
              <mm:node referid="my_givenanswers">
                <mm:setfield name="score"><mm:write referid="questioncorrect"/></mm:setfield>
              </mm:node>
              <mm:createrelation role="related" source="my_givenanswers" destination="givenanswer"/>
            </mm:present>
          </c:when>

          <c:when test="${_ eq 1}">
            <!-- Multiple answers can be given -->

            <!-- correct unless counterevidence -->
            <mm:import id="score">1</mm:import>

            <mm:relatednodes type="mcanswers" id="my_answers">
              <mm:field id="correct" name="correct" write="false"/>

              <mm:import externid="${question}_${_node}" id="givenanswer" />
.
              <mm:present referid="givenanswer">
                <!-- Relate each given answer to the possible answers -->
                <mm:createrelation id="r" role="related" source="my_givenanswers" destination="my_answers"/>
                <!-- when this is a false answer, the score is incorrect -->
                <mm:compare referid="correct" value="0">
                  <mm:remove referid="score"/><mm:import id="score">0</mm:import>
                </mm:compare>
              </mm:present>
              <mm:notpresent referid="givenanswer">
                <!-- when the student had to check the button of thge correct answer, the score is incorrect -->
                <mm:compare referid="correct" value="1">
                  <mm:remove referid="score"/>
                  <mm:import id="score">0</mm:import>
                </mm:compare>
              </mm:notpresent>

              <mm:remove referid="givenanswer" />

            </mm:relatednodes>

            <mm:node referid="my_givenanswers">
              <mm:setfield name="score"><mm:write referid="score"/></mm:setfield>
            </mm:node>

          </c:when>
          <c:when test="${_ eq 2}">
            This seems to be possible answer, but should really nothgin happen
          </c:when>
          <c:otherwise>
            UNKONWN TYPE
          </c:otherwise>
        </c:choose>
      </mm:field>
    </mm:node>

    </mm:cloud>
  </mm:content>
</jsp:root>
