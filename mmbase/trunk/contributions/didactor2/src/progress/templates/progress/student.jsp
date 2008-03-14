<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@page import="java.util.*"
%><mm:content postprocessor="reducespace" expires="0">
  <mm:cloud rank="didactor user">

    <jsp:directive.include file="/education/tests/definitions.jsp" />
    <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
    <mm:import id="editcontextname" reset="true">docent schermen</mm:import>
    <jsp:directive.include file="/education/wizards/roles_chk.jsp" />

    <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="extraheader"><title><di:translate key="progress.progresstitle" /></title></mm:param>
    </mm:treeinclude>

    <div class="rows">
      <div class="navigationbar">
        <div class="titlebar"><di:translate key="progress.progresstitle" /></div>
      </div>

      <div class="folders">
        <div class="folderHeader">&nbsp;</div>
        <div class="folderBody">&nbsp;</div>
      </div>

      <!-- WTF is happening here?: -->
      <mm:import id="student"><mm:write referid="user" /></mm:import>

      <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
        <mm:import id="student" externid="student" reset="true" />
      </mm:islessthan>

      <mm:isempty referid="student">
        <mm:import id="student" reset="true"><mm:write referid="user" /></mm:import>
      </mm:isempty>
      <!-- /WTF -->

      <di:copybook student="${student}">
        <mm:node id="copybookNo" />
      </di:copybook>


      <div class="mainContent">
        <div class="contentHeader">
          <mm:node referid="student">
            <di:person />
            <mm:field name="username" id="usern" />
          </mm:node>
        </div>

        <div class="contentBodywit">
          <mm:import externid="showfeedback" />

          <mm:present referid="showfeedback">

            <mm:import externid="madetest" required="true" />
            <mm:import externid="tests" required="true" />

            <mm:node number="$tests">
              <b><di:translate key="progress.scoretest" /> <mm:field name="name" /></b>
              <br>
                <mm:field id="mayview" name="mayview" write="false" />
                <%-- <mm:field id="feedback" name="showfeedback" write="false" /> --%>

                <mm:compare referid="showfeedback" value="true">
                  <mm:treeinclude page="/education/tests/feedback.jsp" objectlist="$includePath"
                                  referids="$referids,tests,madetest" />
                </mm:compare>
                <mm:compare referid="mayview" value="1">
                  <mm:treeinclude page="/education/tests/viewanswers.jsp" objectlist="$includePath"
                                  referids="$referids,tests@testNo,madetest@madetestNo,student@userNo" />
                </mm:compare>
              </mm:node>
            </mm:present>

            <mm:notpresent referid="showfeedback">
              <div style="float: right">
                <mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids,student@contact" write="false">
                  <a href="${_}">
                    <img src="${mm:treefile('/progress/gfx/portfolio.gif', pageContext, includePath)}"
                         title="Portfolio" alt="Portfolio" border="0" />
                  </a>
                </mm:treefile>
                <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
                  <mm:treefile page="/progress/index.jsp" objectlist="$includePath" referids="$referids" write="false">
                    <a href="${_}">
                      <img src="${mm:treefile('/progress/gfx/back.gif', pageContext, includePath)}"
                           title="${di:translate('progress.backtooverview')}"
                           alt="${di:translate('progress.backtooverview')}"  border="0" />
                    </a>
                  </mm:treefile>
                </mm:islessthan>
              </div>


              <table class="Font">
                <tr>
                  <td><di:translate key="progress.perccompleted" /></td>
                  <td>
                    <mm:import id="progress" escape="trimmer"><mm:treeinclude page="/progress/getprogress.jsp" objectlist="$includePath" referids="$referids,student" /></mm:import>
                    <fmt:formatNumber value="${progress}" type="percent" />
                  </td>
                </tr>
                <mm:import externid="c">${class}</mm:import>

                <mm:log>Using class: ${c}</mm:log>

                <mm:node referid="student">
                  <%-- direct relation people-classrel-educations --%>
                  <mm:notpresent referid="c">
                    <mm:relatednodescontainer path="classrel,educations" element="classrel">
                      <mm:constraint field="educations.number" value="$education" />
                      <mm:relatednodes>
                        <mm:node id="classrel" />
                      </mm:relatednodes>
                    </mm:relatednodescontainer>
                  </mm:notpresent>
                  <%-- people-classrel-class-related-educations --%>
                  <mm:present referid="c">
                    <mm:relatednodescontainer path="classrel,classes" element="classrel">
                      <mm:log>${c}</mm:log>
                      <mm:constraint field="classes.number" value="${c}" />
                      <mm:relatednodes>
                        <mm:node id="classrel" />
                      </mm:relatednodes>
                    </mm:relatednodescontainer>
                  </mm:present>
                </mm:node>

                <mm:present referid="classrel"> <!-- can be not present if e.g. admin -->
                  <mm:node referid="classrel">
                    <tr>
                      <td><di:translate key="progress.logins" />:</td>
                      <td><mm:field name="logincount" /></td>
                    </tr>

                    <tr>
                      <td><di:translate key="progress.online" /></td>
                      <td>
                        <mm:field name="onlinetime">
                          <mm:time format="HH:mm" />
                        </mm:field>
                      </td>
                    </tr>
                  </mm:node>
                </mm:present>
              </table>
              <di:ifsetting component="progress" setting="showeducationplan">
                <p><b><di:translate key="progress.educationplan" /></b></p>

                <table class="Font">
                  <mm:node number="$education">
                    <%boolean first = true;%>
                    <mm:relatednodescontainer type="learnblocks" role="posrel">
                      <mm:sortorder field="posrel.pos" direction="up" />

                      <mm:tree type="learnblocks" role="posrel" searchdir="destination" orderby="posrel.pos"
                               directions="up">
                        <mm:import jspvar="depth" vartype="Integer"><mm:depth /></mm:import>
                        <%if (depth.intValue() == 2) { %>
                        <mm:first inverse="true"></td></tr></mm:first>
                        <tr>
                          <td>
                            <mm:field name="name" />
                          </td>
                          <td>
                            <%first = true;
                            } else if (depth.intValue() > 2) {
                            if (!first) {
                            %>, <%} else {
                            first = false;
                            }
                            %>
                            <mm:field name="name" />
                            <%} %>
                         <mm:last></td></tr></mm:last>
                      </mm:tree>
                    </mm:relatednodescontainer>
                  </mm:node>
                </table>
              </di:ifsetting>

              <mm:node referid="student">
                <p><b><di:translate key="progress.testsof" /> <di:person /></b></p>

                <table class="listTable">
                  <tr>
                    <th class="listHeader"><di:translate key="progress.learnblock" /></th>
                    <th class="listHeader"><di:translate key="progress.tests" /></th>
                    <di:ifsetting component="progress" setting="showquestionamount">
                      <th class="listHeader"><di:translate key="progress.questions" /></th>
                    </di:ifsetting>
                    <di:ifsetting component="progress" setting="showscore">
                      <th class="listHeader"><di:translate key="progress.score" /></th>
                    </di:ifsetting>
                    <di:ifsetting component="progress" setting="showcorrect">
                      <th class="listHeader"><di:translate key="progress.correct" /></th>
                    </di:ifsetting>
                    <di:ifsetting component="progress" setting="showneeded">
                      <th class="listHeader"><di:translate key="progress.needed" /></th>
                    </di:ifsetting>
                    <di:ifsetting component="progress" setting="showsucceeded">
                      <th class="listHeader"><di:translate key="progress.succeeded" /></th>
                    </di:ifsetting>
                  </tr>


                  <mm:node number="$education">
                    <%List blockName = new ArrayList(); %>

                    <mm:relatednodescontainer type="learnblocks" role="posrel">
                      <mm:sortorder field="posrel.pos" direction="up" />

                      <mm:tree type="learnblocks" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">
                        <mm:import jspvar="depth" vartype="Integer"><mm:depth /></mm:import>
                        <%for (int i = blockName.size() - 1; i < depth.intValue() - 1; i++) {
                        blockName.add(null);
                        }
                        while (depth.intValue() - 1 < blockName.size()) {
                        blockName.remove(blockName.size() - 1);
                        }

                        %>
                        <mm:field name="name" jspvar="thisBlockName" vartype="String">
                          <% blockName.set(blockName.size() - 1, thisBlockName); %>
                        </mm:field>

                        <mm:relatednodescontainer type="tests" role="posrel">
                          <mm:sortorder field="posrel.pos" direction="up" />
                          <mm:relatednodes>
                            <mm:import id="testNo" reset="true"><mm:field name="number" /></mm:import>
                            <%int numberOfquestions = 0;%>
                            <mm:field id="feedback" name="feedbackpage" write="false" />

                            <tr>
                              <mm:present referid="copybookNo">
                                <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
                                  <mm:constraint field="copybooks.number" referid="copybookNo" />
                                  <mm:relatednodes>
                                    <mm:node id="madetestNo" />
                                  </mm:relatednodes>
                                </mm:relatednodescontainer>
                              </mm:present>

                              <td class="listItem">
                                <%for (int i = 0; i < blockName.size(); i++) {
                                %><%=(String) blockName.get(i)%><%if (i < blockName.size() - 1) {
                                %> &gt;
                                <%} }%>
                              </td>

                              <jsp:directive.include file="teststatus.jspx" />

                              <td class="listItem">
                                <mm:compare referid="teststatus" value="incomplete">
                                  <mm:field name="name" />
                                </mm:compare>
                                <mm:compare referid="teststatus" value="incomplete" inverse="true">
                                  <a href="<mm:treefile page="/progress/student.jsp" objectlist="$includePath" referids="$referids,madetestNo?@madetest,testNo?@tests" ><mm:param name="showfeedback">true</mm:param></mm:treefile>" />
                                  <mm:field name="name" />
                                </a>
                              </mm:compare>
                            </td>

                            <di:ifsetting component="progress" setting="showquestionamount">
                              <td class="listItem">
                                <mm:field name="questionamount" write="false">
                                  <mm:islessthan value="1">
                                    <mm:countrelations type="questions" write="true" id="amount" jspvar="proba">
                                      <mm:import jspvar="amount"><mm:write referid="amount" /></mm:import>
                                <% numberOfquestions = proba.intValue(); %>
                              </mm:countrelations>
                            </mm:islessthan>

                            <mm:isgreaterthan value="0">
                              <mm:import id="amount"><mm:field name="questionamount" /></mm:import>
                              <mm:write />
                            </mm:isgreaterthan>
                          </mm:field>
                        </td>
                        </di:ifsetting>

                        <di:ifsetting component="progress" setting="showscore">
                        <td class="listItem">
                          <mm:write referid="save_madetestscore" />
                        </td>
                        </di:ifsetting>

                        <di:ifsetting component="progress" setting="showcorrect">
                        <% int sum = 0; %>
                        <mm:relatednodes type="questions">
                          <mm:relatednodes type="givenanswers">
                            <mm:field name="owner">
                              <mm:compare referid2="usern">
                                <mm:field name="score">
                                  <mm:compare value="1">
                                    <%sum++;%>
                                  </mm:compare>
                                </mm:field>
                              </mm:compare>
                            </mm:field>
                          </mm:relatednodes>
                        </mm:relatednodes>

                        <td class="listItem">
                          <mm:compare referid="amount" value="0" inverse="true">
                            <%=((float) sum / numberOfquestions) * 100 %>
                          </mm:compare>
                          <mm:compare referid="amount" value="0">
                            <di:translate key="progress.notQuestions" />
                          </mm:compare>
                        </td>
                        </di:ifsetting>

                        <di:ifsetting component="progress" setting="showneeded">
                        <td class="listItem">
                          <mm:write referid="requiredscore" />
                        </td>
                        </di:ifsetting>

                        <di:ifsetting component="progress" setting="showsucceeded">
                        <mm:compare referid="teststatus" value="toberated">
                          <td class="listItem"><di:translate key="progress.toberated" /></td>
                        </mm:compare>

                        <mm:compare referid="teststatus" value="passed">
                          <td class="listItem"><di:translate key="progress.yes" /></td>
                        </mm:compare>

                        <mm:compare referid="teststatus" value="failed">
                          <td class="listItem"><di:translate key="progress.no" /></td>
                        </mm:compare>

                        <mm:compare referid="teststatus" value="incomplete">
                          <td class="listItem"><di:translate key="progress.notcompleted" /></td>
                        </mm:compare>
                      </tr>
                      </di:ifsetting>

                      <mm:remove referid="madetestscore" />
                      <mm:remove referid="save_madetestscore" />
                      <mm:remove referid="testNo" />
                    </mm:relatednodes>
                    <%-- tests --%>
                  </mm:relatednodescontainer>

                </mm:tree>
              </mm:relatednodescontainer>
              <%-- learnblocks --%>
            </mm:node>
            <%-- education --%>

            <mm:remove referid="copybookNo" />
          </table>
        </mm:node>
      </mm:notpresent>

      <mm:import externid="reports" />
      <mm:present referid="reports">
        <br />
        <form>
           <input type="button" class="formbutton" id="goback" value="${di:translate('core.back')}"  onClick="history.back()"/><br/>
        </form>
      </mm:present>

    </div>
  </div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>

