<jsp:root version="1.2"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          xmlns:os="http://www.opensymphony.com/oscache"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:c="http://java.sun.com/jsp/jstl/core">
  <mm:content postprocessor="none">
    <mm:cloud rank="didactor user">

      <jsp:directive.include file="/education/tests/definitions.jsp" />
      <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
      <mm:import id="editcontextname" reset="true">docent schermen</mm:import>
      <jsp:directive.include file="/education/wizards/roles_chk.jsp" />

      <mm:import externid="student"           required="true"/>
      <mm:import externid="startAt"           jspvar="startAt" vartype="Integer" required="true"/>
      <mm:import externid="direct_connection" required="true"/>
      <mm:import externid="class" reset="true">${requestScope.class}</mm:import>


      <mm:node number="$student">
        <tr>
          <td style="border-color:#000000; border-top:0px; border-left:0px">
            <mm:treefile page="/progress/student.jsp" objectlist="$includePath" referids="class,student,class@c" write="false">
              <a href="${_}">
                <di:person />

              </a>
            </mm:treefile>
          </td>
          <td style="border-color:#000000; border-top:0px; border-left:0px">
            <mm:import id="progress" vartype="Double">
              <mm:treeinclude page="/progress/getprogress.jsp" objectlist="$includePath" referids="$referids,student" />
            </mm:import>
            ${progress * 100}
          </td>

          <!-- direct relation people-classrel-educations -->
          <mm:compare referid="direct_connection" value="true">
            <mm:list fields="classrel.number" path="people,classrel,educations" constraints="people.number=$student and educations.number=$education">
              <mm:node element="classrel" id="classrel" />
            </mm:list>
          </mm:compare>
          <mm:isnotempty referid="class">
            <!-- people-classrel-class-related-educations -->
            <mm:compare referid="direct_connection" value="true" inverse="true">
              <mm:list fields="classrel.number" path="people,classrel,classes" constraints="people.number=$student and classes.number=$class">
                <mm:node element="classrel" id="classrel" />
              </mm:list>
            </mm:compare>
          </mm:isnotempty>


          <mm:present referid="classrel">
            <mm:node referid="classrel">
              <td style="border-color:#000000; border-top:0px; border-left:0px">
                <mm:field name="logincount"/>
              </td>
              <td style="border-color:#000000; border-top:0px; border-left:0px">
                <mm:field name="onlinetime" />
              </td>
              <td style="border-color:#000000; border-top:0px; border-left:0px">
                <mm:node number="$student">
                  <mm:field name="gui(lastactivity)" />
                </mm:node>
              </td>
            </mm:node>
          </mm:present>
          <mm:notpresent referid="classrel">
            <td /><td />
          </mm:notpresent>

          <di:copybook student="${student}">
            <mm:present referid="copybookNo">
              <mm:remove referid="copybookNo" />
            </mm:present>
            <mm:node id="copybookNo" />
          </di:copybook>

          <mm:node number="$education">
            <mm:nodelistfunction name="tests" id="testNo">
              <jsp:directive.include file="teststatus.jspx" />
              <mm:compare referid="teststatus" value="incomplete" inverse="true">
                <mm:compare referid="teststatus" value="toberated">
                  <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
                    <td class="td_test_tbs" style="border-color:#000000; border-top:0px; border-left:0px">
                      <mm:treefile page="/education/tests/rateopen.jsp"
                                   objectlist="$includePath" referids="$referids,student,_node@testNo" write="false">
                        <a href="${_}">
                          <img src="${mm:treefile('/progress/gfx/question.gif', pageContext,  includePath)}"
                               title="?" alt="?" border="0" />
                        </a>
                      </mm:treefile>
                    </td>
                  </mm:islessthan>
                  <mm:islessthan referid="rights" referid2="RIGHTS_RW">
                    <td class="td_test_tbs" style="border-color:#000000; border-top:0px; border-left:0px">
                      <img src="${mm:treefile('/progress/gfx/question.gif', pageContext, includePath)}"  title="?" alt="?" border="0" />
                    </td>
                  </mm:islessthan>
                </mm:compare>

                <mm:compare referid="teststatus" value="passed">
                  <td class="td_test_tbs" style="border-color:#000000; border-top:0px; border-left:0px">
                    <img src="${mm:treefile('/progress/gfx/checked.gif', pageContext, includePath)}" title="Ok" alt="Ok" border="0" />
                  </td>
                </mm:compare>

                <mm:compare referid="teststatus" value="failed">
                  <td class="td_test_failed" style="border-color:#000000; border-top:0px; border-left:0px">
                    <img src="${mm:treefile('/progress/gfx/box.gif', pageContext, includePath)}" alt="" border="0" />
                  </td>
                </mm:compare>
              </mm:compare>

              <mm:compare referid="teststatus" value="incomplete" >
                <td class="td_test_not_done" style="border-color:#000000; border-top:0px; border-left:0px">
                  <img src="${mm:treefile('/progress/gfx/box.gif', pageContext, includePath)}" alt="" border="0" />
                </td>
              </mm:compare>

            </mm:nodelistfunction>
          </mm:node>
        </tr>
      </mm:node>
    </mm:cloud>
  </mm:content>
</jsp:root>
