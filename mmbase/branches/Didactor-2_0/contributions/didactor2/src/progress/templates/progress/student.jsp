<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"%>

<%@page import="java.util.*"%>

<mm:content postprocessor="reducespace" expires="0">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">

  <%@include file="/shared/setImports.jsp"%>
  <%@include file="/education/tests/definitions.jsp"%>
  <%@include file="/education/wizards/roles_defs.jsp"%>
  <mm:import id="editcontextname" reset="true">docent schermen</mm:import>
  <%@include file="/education/wizards/roles_chk.jsp"%>

  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title><di:translate key="progress.progresstitle" /></title>
    </mm:param>
  </mm:treeinclude>


  <div class="rows">
  <div class="navigationbar">
  <div class="titlebar"><di:translate key="progress.progresstitle" /></div>
  </div>

  <div class="folders">
  <div class="folderHeader">&nbsp;</div>
  <div class="folderBody">&nbsp;</div>
  </div>

  <mm:import id="student" reset="true">
    <mm:write referid="user" />
  </mm:import> <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
    <mm:import id="student" externid="student" reset="true" />
  </mm:islessthan> <mm:isempty referid="student">
    <mm:import id="student" reset="true">
      <mm:write referid="user" />
    </mm:import>
  </mm:isempty>

  <div class="mainContent">
  <div class="contentHeader">
  <%--    Some buttons working on this folder--%>
  <mm:node referid="student">
    <mm:field name="firstname" />
    <mm:field name="lastname" />
    <mm:field name="username" id="usern" />
  </mm:node></div>

  <div class="contentBodywit"><mm:import externid="showfeedback" /> <mm:present
    referid="showfeedback">
    <mm:import externid="madetest" required="true" />
    <mm:import externid="tests" required="true" />

    <mm:node number="$tests">
      <b><di:translate key="progress.scoretest" /> <mm:field
        name="name" /></b>
      <br>

      <mm:field id="mayview" name="mayview" write="false" />
      <mm:field id="feedback" name="showfeedback" write="false" />

      <mm:compare referid="showfeedback" value="1">
        <mm:treeinclude page="/education/tests/feedback.jsp"
          objectlist="$includePath" referids="$referids">
          <mm:param name="madetes">
            <mm:write referid="madetest" />
          </mm:param>
          <mm:param name="tests">
            <mm:write referid="tests" />
          </mm:param>
        </mm:treeinclude>
      </mm:compare>
      <mm:compare referid="mayview" value="1">
        <mm:treeinclude page="/education/tests/viewanswers.jsp"
          objectlist="$includePath" referids="$referids">
          <mm:param name="testNo">
            <mm:write referid="tests" />
          </mm:param>
          <mm:param name="madetestNo">
            <mm:write referid="madetest" />
          </mm:param>
          <mm:param name="userNo">
            <mm:write referid="student" />
          </mm:param>
        </mm:treeinclude>
      </mm:compare>
    </mm:node>
  </mm:present> <mm:notpresent referid="showfeedback">

    <div style="float: right"><a
      href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                   <mm:param name="contact"><mm:write referid="student"/></mm:param>
                   </mm:treefile>"><img
      src="<mm:treefile page="/progress/gfx/portfolio.gif"  objectlist="$includePath" referids="$referids"/>"
      title="Portfolio" alt="Portfolio" border="0" /> </a> <mm:islessthan inverse="true"
      referid="rights" referid2="RIGHTS_RW">
      <a
        href="<mm:treefile page="/progress/index.jsp" objectlist="$includePath" referids="$referids"/>"><img
        src="<mm:treefile page="/progress/gfx/back.gif"  objectlist="$includePath" referids="$referids"/>"
        title="<di:translate key="progress.backtooverview" />" alt="<di:translate key="progress.backtooverview" />"
        border="0" /></a>
    </mm:islessthan></div>


    <table class="Font">
      <tr>
        <td><di:translate key="progress.perccompleted" /></td>
        <td><mm:import jspvar="progress" id="progress"
          vartype="Double">
          <mm:treeinclude page="/progress/getprogress.jsp"
            objectlist="$includePath" referids="$referids">
            <mm:param name="student">
              <mm:write referid="student" />
            </mm:param>

          </mm:treeinclude>
        </mm:import> <%=(int) (progress.doubleValue() * 100.0)%>%</td>

      </tr>

      <%//direct relation people-classrel-educations %>
      <mm:compare referid="class" value="null">
        <mm:list fields="classrel.number"
          path="people,classrel,educations"
          constraints="people.number=$student and educations.number=$education">
          <mm:field name="classrel.number" id="classrel" write="false" />
        </mm:list>
      </mm:compare>
      <%//people-classrel-class-related-educations %>
      <mm:compare referid="class" value="null" inverse="true">
        <mm:list fields="classrel.number"
          path="people,classrel,classes"
          constraints="people.number=$student and classes.number=$class">
          <mm:field name="classrel.number" id="classrel" write="false" />
        </mm:list>
      </mm:compare>

      <mm:node referid="classrel">
        <tr>
          <td><di:translate key="progress.logins" />:</td>
          <td><mm:field name="logincount" /></td>
        </tr>

        <tr>
          <td><di:translate key="progress.online" /></td>
          <td><mm:field name="onlinetime" jspvar="onlinetime"
            vartype="Integer" write="false">
            <%int hour = onlinetime.intValue() / 3600;
          int min = (onlinetime.intValue() % 3600) / 60;

          %>
            <%=hour%>:<%=min%>
          </mm:field></td>
        </tr>
      </mm:node>
    </table>

    <p><b><di:translate key="progress.educationplan" /></b></p>

    <table class="Font">
      <mm:node number="$education">
        <%boolean first = true;

          %>

        <mm:relatednodescontainer type="learnblocks" role="posrel">
          <mm:sortorder field="posrel.pos" direction="up" />

          <mm:tree type="learnblocks" role="posrel"
            searchdir="destination" orderby="posrel.pos"
            directions="up">
            <mm:import jspvar="depth" vartype="Integer">
              <mm:depth />
            </mm:import>
            <%if (depth.intValue() == 2) {

              %>
            <mm:first inverse="true">
              </td>
              </tr>
            </mm:first>
            <tr>
              <td><mm:field name="name" /></td>
              <td><%first = true;
          } else if (depth.intValue() > 2) {
              if (!first) {

              %>, <%} else {
                  first = false;
              }

          %><mm:field name="name" /><%}

          %> <mm:last></td>
            </tr>
            </mm:last>
          </mm:tree>
        </mm:relatednodescontainer>
      </mm:node>
    </table>


    <mm:node referid="student">
      <p><b><di:translate key="progress.testsof" /> <mm:field
        name="firstname" /> <mm:field name="lastname" /></b></p>


      <table class="listTable">
        <tr>
          <th class="listHeader"><di:translate
            key="progress.learnblock" /></th>
          <th class="listHeader"><di:translate key="progress.tests" /></th>
          <th class="listHeader"><di:translate
            key="progress.questions" /></th>
          <th class="listHeader"><di:translate key="progress.score" /></th>
          <th class="listHeader"><di:translate key="progress.correct" /></th>
          <th class="listHeader"><di:translate key="progress.needed" /></th>
          <th class="listHeader"><di:translate
            key="progress.succeeded" /></th>
        </tr>

        <%-- find copybook --%>
        <%@include file="find_copybook.jsp"%>


        <mm:node number="$education">
          <%List blockName = new ArrayList();

          %>

          <mm:relatednodescontainer type="learnblocks" role="posrel">
            <mm:sortorder field="posrel.pos" direction="up" />

            <mm:tree type="learnblocks" role="posrel"
              searchdir="destination" orderby="posrel.pos"
              directions="up">
              <mm:import jspvar="depth" vartype="Integer">
                <mm:depth />
              </mm:import>
              <%for (int i = blockName.size() - 1; i < depth.intValue() - 1; i++) {
              blockName.add(null);
          }
          while (depth.intValue() - 1 < blockName.size()) {
              blockName.remove(blockName.size() - 1);
          }

          %>

              <mm:field name="name" jspvar="thisBlockName"
                vartype="String">
                <%blockName.set(blockName.size() - 1, thisBlockName);

          %>
              </mm:field>

              <mm:relatednodescontainer type="tests" role="posrel">
                <mm:sortorder field="posrel.pos" direction="up" />
                <mm:relatednodes>
                  <mm:import id="testNo" reset="true">
                    <mm:field name="number" />
                  </mm:import>
                  <%int numberOfquestions = 0;

          %>
                  <mm:field id="feedback" name="feedbackpage"
                    write="false" />

                  <tr>

                    <mm:relatednodescontainer
                      path="madetests,copybooks" element="madetests">
                      <mm:constraint field="copybooks.number"
                        referid="copybookNo" />

                      <mm:relatednodes>
                        <mm:field id="madetestNo" name="number"
                          write="false" />
                      </mm:relatednodes>
                    </mm:relatednodescontainer>


                    <td class="listItem"><%for (int i = 0; i < blockName.size(); i++) {

              %><%=(String) blockName.get(i)%><%if (i < blockName.size() - 1) {

              %> &gt; <%}
          }

          %></td>

                    <%@include file="teststatus.jsp"%>

                    <td class="listItem"><mm:compare
                      referid="teststatus" value="incomplete">
                      <mm:field name="name" />
                    </mm:compare> <mm:compare referid="teststatus"
                      value="incomplete" inverse="true">
                      <a
                        href="<mm:treefile page="/progress/student.jsp" objectlist="$includePath" referids="$referids" >
                                                          <mm:param name="madetest"><mm:write referid="madetestNo"/></mm:param>
                                                          <mm:param name="tests"><mm:write referid="testNo"/></mm:param>
                                                          <mm:param name="showfeedback">true</mm:param>
                                                       </mm:treefile>" /><mm:field
                        name="name" /></a>

                    </mm:compare></td>

                    <td class="listItem"><mm:field
                      name="questionamount" write="false">

                      <mm:islessthan value="1">
                        <mm:countrelations type="questions"
                          write="true" id="amount" jspvar="proba">
                          <mm:import jspvar="amount">
                            <mm:write referid="amount" />
                          </mm:import>
                          <%
                            numberOfquestions = proba.intValue();
                          %>
                        </mm:countrelations>


                      </mm:islessthan>

                      <mm:isgreaterthan value="0">
                        <mm:write />
                      </mm:isgreaterthan>

                    </mm:field></td>





                    <td class="listItem"><mm:write
                      referid="save_madetestscore" /></td>

                    <%
                    int sum = 0;
                    %>

                    <mm:relatednodes type="questions">

                      <mm:relatednodes type="givenanswers">

                        <mm:field name="owner">

                          <mm:compare referid2="usern">
                            <mm:field name="owner" write="true" />



                            <mm:field name="score">
                              <mm:compare value="1">
                                <%
                                sum = sum + 1;
                                 %>
                              </mm:compare>

                            </mm:field>
                            

                          </mm:compare>

                        </mm:field>

                      </mm:relatednodes>


                    </mm:relatednodes>


                    <td class="listItem"><mm:compare referid="amount"
                      value="0" inverse="true">
                      
                      <%=((float) sum / numberOfquestions) * 100%>
                    </mm:compare> <mm:compare referid="amount"
                      value="0">
                      <di:translate key="progress.notQuestions" />
                    </mm:compare></td>



                    <td class="listItem"><mm:write
                      referid="requiredscore" /></td>

                    <mm:compare referid="teststatus" value="toberated">
                      <td class="listItem"><di:translate
                        key="progress.toberated" /></td>
                    </mm:compare>

                    <mm:compare referid="teststatus" value="passed">
                      <td class="listItem"><di:translate
                        key="progress.yes" /></td>
                    </mm:compare>

                    <mm:compare referid="teststatus" value="failed">
                      <td class="listItem"><di:translate
                        key="progress.no" /></td>
                    </mm:compare>

                    <mm:compare referid="teststatus"
                      value="incomplete">
                      <td class="listItem"><di:translate
                        key="progress.notcompleted" /></td>
                    </mm:compare>
                  </tr>

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
  </mm:notpresent></div>
  </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp"
    objectlist="$includePath" referids="$referids" />

  </mm:cloud>
</mm:content>

