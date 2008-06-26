<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    >
  <mm:content postprocessor="none" expires="0">
    <mm:import externid="madetestscore" required="true" />
    <jsp:directive.include file="/education/tests/definitions.jsp"  />

    <mm:cloud rank="didactor user">
      <div class="learnenvironment">
        <di:title field="name" />

        <mm:field id="maychange" name="maychange"    write="false"/>
        <mm:field id="mayview"   name="mayview"      write="false"/>
        <mm:field id="feedback"  name="feedbackpage" write="false"/>
        <mm:import externid="justposted" />

        <mm:field name="number">
          <mm:compare referid2="justposted" inverse="true">
            <mm:compare referid="madetestscore" referid2="TESTSCORE_TBS">
              <p><di:translate key="education.alreadymade_tobescored" /></p>
            </mm:compare>

            <mm:compare referid="madetestscore" referid2="TESTSCORE_TBS" inverse="true">
              <!-- if madestestscore larger or equal than requiredscore -->
              <mm:field id="requiredscore" name="requiredscore" write="false"/>

              <mm:islessthan referid="feedback" value="1">
                <mm:islessthan referid="madetestscore" referid2="requiredscore" inverse="true">
                  <di:translate key="education.alreadymade_success" /><p/>
                </mm:islessthan>
                <mm:islessthan referid="madetestscore" referid2="requiredscore">
                  <di:translate key="education.alreadymade_fail" /><p/>
                </mm:islessthan>
              </mm:islessthan>

              <mm:compare referid="feedback" value="1">
                <di:translate key="education.alreadymade" /> <p/>
              </mm:compare>

              <table>
                <tr>
                  <mm:compare referid="mayview" value="1">
                    <td>
                      <div class="button1">
                        <mm:treefile page="/education/tests/viewanswersframe.jsp" objectlist="$includePath"  write="false"
                                     referids="$referids,testNo,madetestNo,user@userNo"
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
                            referids="$referids,testNo@learnobject">
                          <a href="${_}">
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
                            referids="$referids,testNo@learnobject">
                          <mm:param name="clearmadetest">true</mm:param>
                          <a href="${_}"><di:translate key="education.clear" /></a>
                        </mm:treefile>
                      </div>
                    </td>
                  </mm:compare>
                </tr>
              </table>
            </mm:compare>
          </mm:compare>
        </mm:field>
      </div>
    </mm:cloud>
  </mm:content>
</jsp:root>
