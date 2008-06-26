<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    >
  <mm:import externid="madetest" required="true" />
  <mm:import externid="learnobject" required="true" />
  <mm:node referid="madetest" id="madetest" />

  <jsp:directive.include file="/education/tests/definitions.jsp"  />

  <div class="content learnenvironment testresults">

    <di:title field="name" />

    <mm:field id="maychange" name="maychange"    write="false"/>
    <mm:field id="mayview"   name="mayview"      write="false"/>
    <mm:field id="feedback"  name="feedbackpage" write="false"/>

    <mm:import externid="justposted" />

    <c:if test="${justposted ne _node.number}">

      <c:choose>
        <c:when test="${madetest.score eq TESTSCORE_TBS}">
          <jsp:text>&lt;!-- Score is ${madetest.score}, which means that is still has to be evaluated --&gt;</jsp:text>
          <p><di:translate key="education.alreadymade_tobescored" /></p>
        </c:when>
        <c:otherwise>
          <jsp:text>&lt;!-- Show feedback, score is ${madetest.score}  (required ${_node.requiredscore}) --&gt;</jsp:text>


          <c:choose>
            <c:when test="${_node.feedbackpage eq 1}">
              <di:translate key="education.alreadymade" /> <p/>
            </c:when>
            <c:when test="${_node.feedbackpage lt 1}">
              <c:choose>
                <c:when test="${madetestscore ge _node.requiredscore }">
                  <di:translate key="education.alreadymade_success" /><p/>
                </c:when>
                <c:otherwise>
                  <di:translate key="education.alreadymade_fail" /><p/>
                </c:otherwise>
              </c:choose>
            </c:when>
          </c:choose>


          <table>
            <tr>
              <mm:compare referid="mayview" value="1">
                <td>
                  <div class="button1">
                    <mm:treefile page="/education/tests/viewanswersframe.jsp" objectlist="$includePath"  write="false"
                                 referids="$referids,learnobject@testNo,madetest@madetestNo,user@userNo"
                                 >
                      <a href="${_}" onclick="requestContent('${_}'); return false;"><di:translate key="education.view" /></a>
                    </mm:treefile>
                  </div>
                </td>
              </mm:compare>

              <mm:compare referid="maychange" value="1">
                <td>
                  <div class="button1">
                    <mm:treefile
                        page="/education/tests/buildtest.jsp" objectlist="$includePath" write="false"
                        referids="$referids,learnobject">
                      <a href="${_}"
                         onclick="requestContent('${_}'); return false;"
                         >
                        <mm:compare referid="feedback" value="1"><di:translate key="education.again" /></mm:compare>
                        <mm:compare referid="feedback" value="0"><di:translate key="education.retry" /></mm:compare>
                      </a>
                    </mm:treefile>
                  </div>
                </td>
                <td>
                  <div class="button1">
                    <mm:treefile
                        page="/education/tests/buildtest.jsp" objectlist="$includePath" write="false"
                        referids="$referids,learnobject">
                      <mm:param name="clearmadetest">true</mm:param>
                      <a href="${_}"
                         onclick="requestContent('${_}'); return false;"
                         ><di:translate key="education.clear" /></a>
                    </mm:treefile>
                  </div>
                </td>
              </mm:compare>
            </tr>
          </table>
        </c:otherwise>
      </c:choose>
    </c:if>

    <di:blocks classification="after_test" />

  </div>
</jsp:root>
