<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@ page import="java.text.*,java.util.*" contentType="application/xml;charset=UTF-8"
%>
<mm:content postprocessor="reducespace" type="application/xml">
  <mm:cloud rank="didactor user">
    <mm:import id="testNo" externid="learnobject" required="true"/>
    <jsp:directive.include file="/education/tests/definitions.jsp"  />

    <%-- remember this page --%>
    <mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>
      <mm:param name="learnobjecttype">tests</mm:param>
    </mm:treeinclude>
    
    <di:copybook><mm:node id="copybookNo" /></di:copybook>
    
    <mm:node number="$testNo">
      <mm:present referid="copybookNo">
        <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
          <mm:constraint field="score"  referid="TESTSCORE_INCOMPLETE" inverse="true"/>
          <mm:constraint field="copybooks.number"  value="$copybookNo" />
          <mm:relatednodes>
            <mm:field id="madetestNo" name="number" write="false"/>
            <mm:field id="madetestscore" name="score" write="false"/>
          </mm:relatednodes>
        </mm:relatednodescontainer>
      </mm:present>
      
      <mm:booleanfunction name="online" inverse="true">
        <div class="learnenvironment">
          <di:translate key="education.testnotyetavailable" /> 
          <mm:field name="online_date"><mm:time format=":FULL" /></mm:field> - <mm:field name="offline_date"><mm:time format=":FULL" /></mm:field>
          <mm:import id="testCantBeShowed" />
        </div>
      </mm:booleanfunction>

      <mm:notpresent referid="testCantBeShowed">
        <mm:present referid="madetestNo">
          <div class="learnenvironment">
            <mm:field name="showtitle">
              <mm:compare value="1">
                <h1><mm:field name="name"/></h1>
              </mm:compare>
            </mm:field>
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
                  <%-- if madestestscore larger or equal than requiredscore --%>
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
                            <mm:treefile page="/education/tests/buildtest.jsp" objectlist="$includePath" write="false"
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
                            <mm:treefile page="/education/tests/buildtest.jsp" objectlist="$includePath" write="false"
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
        </mm:present>
      </mm:notpresent>
    </mm:node>

    <mm:notpresent referid="testCantBeShowed">
      <mm:present referid="madetestNo" inverse="true">
        <mm:treeinclude 
            debug="html"
            page="/education/tests/buildtest.jsp" objectlist="$includePath" referids="$referids">
          <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>
        </mm:treeinclude>
      </mm:present>
    </mm:notpresent>


    <mm:node number="$testNo">
      <jsp:directive.include file="../includes/component_link.jsp" />
    </mm:node>


  </mm:cloud>
</mm:content>
