<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    >
  <mm:content type="application/xml" postprocessor="reducespace" expires="0">
    <mm:cloud rank="didactor user">

      <mm:import externid="learnobject" required="true"/>
      <mm:import externid="madetest" />

      <mm:import externid="page" vartype="integer">0</mm:import>

      <mm:node id="learnobject" referid="learnobject" />
      <di:copybook><mm:node id="copybookNo" /></di:copybook>

      <mm:present referid="copybookNo">

        <mm:notpresent referid="madetest" >
          <mm:remove referid="madetest" />
          <mm:import externid="clearmadetest">false</mm:import>
          <mm:node referid="copybookNo">
            <mm:nodefunction id="madetest" name="madetest" referids="learnobject@test,clearmadetest@clear" />
          </mm:node>
        </mm:notpresent>
      </mm:present>

      <jsp:text>&lt;!-- Made test: ${madetest} --&gt;</jsp:text>

      <div class="content learnenvironment tests">
        <!-- Take care: form name is used in JavaScript of the specific question jsp pages! -->
        <mm:treefile id="post" page="/education/tests/rate.jsp" objectlist="$includePath" referids="$referids,learnobject,madetest@thismadetest" write="false"/>
        <mm:node number="$learnobject" id="test">


          <form name="questionform"
                action="${post}"
                method="POST">

            <di:title field="name" />


            <mm:hasfield name="text">
              <mm:field name="text" escape="toxml"/>
            </mm:hasfield>

            <mm:present referid="copybookNo">
              <mm:node referid="madetest">
                <mm:compare referid="page" value="0">
                  <mm:setfield name="testpath" />
                </mm:compare>
                <mm:field name="testpath" write="false" vartype="list">
                  <c:choose>
                    <c:when test="${empty _}">
                      <mm:nodelistfunction node="test" name="questions" id="questions" referids="copybookNo@seed" />
                    </c:when>
                    <c:otherwise>
                      <mm:listnodes referid="_" id="questions" />
                    </c:otherwise>
                  </c:choose>
                </mm:field>
                <mm:relatednodes role="related"  type="givenanswers" id="givenanswers" />
              </mm:node>
            </mm:present>

            <mm:nodelistfunction name="questions" id="my_questions" referids="copybookNo?@seed,page" />

            <mm:write session="my_questions" referid="my_questions" />

            <jsp:text>&lt;!-- givenanswers: ${givenanswers}, my questions: ${my_questions} --&gt;</jsp:text>

            <mm:listnodes referid="my_questions">

              <mm:present referid="copybookNo">
                <mm:nodeinfo type="type">
                  <mm:treeinclude
                      debug="xml"
                      page="/education/${_}/index.jsp"
                      objectlist="$includePath" referids="$referids,_node@question,learnobject@testnumber,madetest" />
                </mm:nodeinfo>
              </mm:present>

              <mm:notpresent referid="copybookNo">
                <mm:nodeinfo type="type">
                  <div class="${_}">
                    <h1 ><mm:field name="title" /></h1>
                    <mm:field name="text" escape="toxml"/>
                  </div>
                </mm:nodeinfo>
              </mm:notpresent>
              <input type="hidden" name="shown${_node}" value="${_node}" />
            </mm:listnodes>


            <!-- Arguments for rating -->
            <input type="hidden" name="learnobject" value="${learnobject}" />
            <input type="hidden" name="thismadetest" value="${madetest}" />
            <input type="hidden" name="page" value="${page}" />
            <input type="hidden" name="command" value="next" />
            <mm:nodeinfo type="type">
              <input type="hidden" name="${_}" value="${_node}" />
            </mm:nodeinfo>
            <input type="hidden" name="testpath" value="${questions}"/>

            <c:if test="${fn:length(my_questions) lt 1}">
              <di:translate key="education.testwithoutquestions" />
            </c:if>

            <mm:present referid="copybookNo">
              <!-- Determine if all questions are showed -->
              <c:choose>
                <c:when test="${fn:length(my_questions) + fn:length(givenanswers) gt fn:length(questions)}">
                  <input type="button"
                         disabled="disabled"
                         value="${di:translate('education.buttontextdone')}"
                         class="formbutton"
                         onclick="document.forms.questionform.command.value='done'; postContent('${post}', document.forms.questionform);" />
                </c:when>
                <c:otherwise>
                  <c:if test="${page gt 0}">
                    <input type="button"
                           value="${di:translate('education.buttontextprev')}"
                           class="formbutton"
                           onclick="document.forms.questionform.command.value='back'; postContent('${post}', document.forms.questionform);" />
                  </c:if>
                  <c:if test="${learnobject.questionsperpage gt 0 and page * learnobject.questionsperpage lt fn:length(questions)}">
                    <input type="button"
                           value="${di:translate('education.buttontextnext')}"
                           class="formbutton"
                           onclick="postContent('${post}', questionform);" />
                  </c:if>
                  <c:if test="${learnobject.questionsperpage lt 1 or page * learnobject.questionsperpage ge fn:length(questions)}">
                    <input type="button"
                           value="${di:translate('education.buttontextdone')}"
                           class="formbutton"
                           onclick="document.forms.questionform.command.value='done'; postContent('${post}', document.forms.questionform);" />
                  </c:if>
                </c:otherwise>
              </c:choose>

            </mm:present>
          </form>

          <mm:notpresent referid="copybookNo">
            <di:translate key="education.nocopybookfound" />
          </mm:notpresent>

        <di:blocks classification="after_test" />
      </mm:node>

      </div>
    </mm:cloud>
  </mm:content>
</jsp:root>
