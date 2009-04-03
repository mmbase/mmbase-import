<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0" >
  <mm:content postprocessor="none" type="application/xml" expires="0">
    <mm:cloud rank="didactor user">

<di:background>
      <mm:import id="testNo" externid="learnobject" required="true"/>
      <jsp:directive.include file="/education/tests/definitions.jsp"  />

      <!-- remember this page -->
      <mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>
        <mm:param name="learnobjecttype">tests</mm:param>
      </mm:treeinclude>

      <di:copybook><mm:node id="copybookNo" /></di:copybook>

      <mm:node number="$testNo">

        <mm:function name="online" write="false">
          <c:choose>
            <c:when test="${_ eq false}">
              <!-- Test not online, show that -->
              <div class="learnenvironment copybook_${copybookNo}">
                <di:translate key="education.testnotyetavailable" />
                <mm:field name="online_date">
                  <mm:time format=":FULL" /></mm:field> - <mm:field name="offline_date"><mm:time format=":FULL" />
                </mm:field>
                <di:blocks classification="after_test" />
              </div>
            </c:when>
            <c:otherwise>
              <!-- Test is indeed online -->

              <mm:present referid="copybookNo">
                <!-- Determin wether test was made already -->
                <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
                  <mm:constraint field="score"  referid="TESTSCORE_INCOMPLETE" inverse="true"/>
                  <mm:constraint field="copybooks.number"  value="$copybookNo" />
                  <mm:relatednodes>
                    <mm:node id="madetest" />
                  </mm:relatednodes>
                </mm:relatednodescontainer>
              </mm:present>

              <jsp:text>&lt;!-- made test no: ${madetestNo} --&gt;</jsp:text>

              <mm:present referid="madetest">
                <!-- Made already, show the result -->
                <mm:treeinclude
                    debug="html"
                    page="/education/tests/buildtestresult.jsp" objectlist="$includePath"
                    referids="$referids,testNo@learnobject,madetest" />
              </mm:present>


              <mm:present referid="madetest" inverse="true">
                <!-- Not made already, build the test, and let the user make it -->
                <mm:treeinclude
                    debug="html"
                    page="/education/tests/buildtest.jsp" objectlist="$includePath"
                    referids="$referids,testNo@learnobject" />
              </mm:present>

            </c:otherwise>
          </c:choose>
        </mm:function>
      </mm:node>


      <mm:node number="$testNo">
        <jsp:directive.include file="../includes/component_link.jsp" />
      </mm:node>
</di:background>

    </mm:cloud>
  </mm:content>
</jsp:root>
