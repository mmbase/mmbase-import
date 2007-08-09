<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><mm:cloud rank="didactor user"
>
  <jsp:directive.include file="/shared/setImports.jsp" />

  <mm:content postprocessor="reducespace" expires="0" language="$language">
    <mm:import externid="testNo"     required="true"  />
    <mm:import externid="madetestNo" required="true"  />
    <mm:import externid="userNo"     required="true"  />
    <mm:import externid="feedback"   required="false" />

    <jsp:directive.include file="/education/tests/definitions.jsp" />

    <di:may component="education" action="isSelfOrTeacherOf" arguments="userNo">
      <mm:node referid="madetestNo">
        <mm:field name="number" />
        <mm:relatednodes role="related" orderby="related.number" type="givenanswers" directions="up" id="answer">
          <p>
          <mm:relatednodes type="questions">
            <mm:import id="questiontype"><mm:nodeinfo type="type"/></mm:import>
            <div class="view_answer">
              <b><di:translate key="education.question" />:</b>
              <mm:field name="text" escape="none" />
              <br/>
              <mm:node id="question" />
            </div>
          </mm:relatednodes>

          <mm:notpresent referid="questiontype">
            <b>questiontype not found </b>
            <br/>
            <mm:import id="questiontype">none</mm:import>
          </mm:notpresent>

          <div class="view_question">
            <mm:haspage page="/education/${questiontype}/view.jspx">
              <mm:include page="/education/${questiontype}/view.jspx" referids="question" />
            </mm:haspage>
            <mm:haspage page="/education/${questiontype}/view.jspx" inverse="true">
              <p>No delegate /education/${questiontype}/view.jspx</p>
            </mm:haspage>
          </div>
          <br/>

          <mm:field name="score">
            <c:choose>
              <c:when test="${_ eq 1}">
                <b><di:translate key="education.answer_correct" /></b>
              </c:when>
              <c:when test="${_ eq 0}">
                <b><di:translate key="education.answer_incorrect" /></b>
              </c:when>
              <c:when test="${_ eq TESTSCORE_TBS}">
                <b><di:translate key="education.answer_tobescored" /></b>
              </c:when>
              <c:otherwise>
                <!-- ?? -->
              </c:otherwise>
            </c:choose>
          </mm:field>

          <br/>
          <!-- Feedback (from the question) -->
          <mm:node referid="question">
            <mm:relatednodescontainer type="feedback">
              <mm:constraint field="maximalscore" value="${answer.score}" operator=">="/>
              <mm:constraint field="minimalscore" value="${answer.score}" operator="<="/>
              <mm:relatednodes>
                <b><di:translate key="education.feedback" />: <mm:field name="name"/></b><br/>

                <mm:relatednodes type="images">
                  <mm:image mode="img" template="s(150x150)"  />
                  <mm:last><br/></mm:last>
                </mm:relatednodes>

                <mm:include page="/education/tests/view_feedback.jspx" />

              </mm:relatednodes>
            </mm:relatednodescontainer>
          </p>
        </mm:node>
      </mm:relatednodes>
    </mm:node>
  </di:may>
</mm:content>
</mm:cloud>
