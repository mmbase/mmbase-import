<jsp:root version="1.2"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          xmlns:os="http://www.opensymphony.com/oscache"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:c="http://java.sun.com/jsp/jstl/core">
  <jsp:directive.page buffer="2000kb" />
  <di:html
      postprocessor="none"
      type="text/html"
      rank="didactor user"
      component="progress"
      title_key="progress.progresstitle">
    <!--
        This JSP actually remains one entire WTF
        - bloated
        - can produce ridiculously large results
        - is hard to understand, because of horrible switches,

    -->


    <di:has editcontext="docent schermen" inverse="true">
      <di:hasrole role="student">
        <!--
             aaah
        -->
        <jsp:forward page="student.jsp"/>
      </di:hasrole>
    </di:has>

    <di:getsetting id="sort" component="core" setting="personorderfield" write="false" />

    <di:has editcontext="docent schermen">

      <div class="rows">
        <div class="navigationbar"><di:translate key="progress.progresstitle" /></div>

        <div class="folders">
        <div class="folderHeader"><jsp:text> </jsp:text></div>
        <div class="mainContent">
          <div class="contentHeader"><jsp:text> </jsp:text></div>
          <div class="contentBodywit">
          <mm:node number="$education">
            <b title="${class}"><mm:field name="name" /></b>

            <table class="font">

              <mm:nodelistfunction name="tests" id="tests" />

              <mm:import externid="startAt" vartype="integer">0</mm:import>

              <tr>
                <th />
                <th><di:rotatedtext text="${di:translate('progress.progresstitle')}" /></th>
                <th><di:rotatedtext text="${di:translate('progress.logins')}" /></th>
                <th><di:rotatedtext text="${di:translate('progress.online')}" /></th>
                <th><di:rotatedtext text="${di:translate('progress.lastlogin')}" /></th>

                <mm:listnodes referid="tests">
                  <th><di:rotatedtext text="${_node.name}" /></th>
                </mm:listnodes>
              </tr>

              <!-- If the man is connected directly to education this man is a mega techer for this education -->
              <mm:isempty referid="class">
                <mm:node referid="education">
                  <tr>
                    <td colspan="100">
                      <b><di:translate key="progress.directconnection" />:</b>
                    </td>
                  </tr>
                  <mm:time id="now" time="now" write="false" precision="hours" />
                  <mm:time id="lastweek" time="now - 1 week" write="false" precision="hours" />
                  <os:cache time="600" key="progress-${education}-people">
                    <mm:timer name="people">
                      <mm:relatednodes role="classrel" type="people" orderby="$sort" >
                        <os:cache time="${_node.lastactivity lt lastweek ? 15000 : 300}" key="student-${education}-people-${_node}">
                          <di:hasrole role="student" referid="_node">
                            <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids,startAt,_node@student">
                              <mm:param name="direct_connection">true</mm:param>
                            </mm:treeinclude>
                          </di:hasrole>
                        </os:cache>
                      </mm:relatednodes>
                    </mm:timer>
                  </os:cache>

                  <mm:relatednodes role="classrel" type="classes" orderby="name" id="classNode">
                    <mm:relatednodescontainer type="mmevents">
                      <mm:constraint field="stop" operator="greater" value="$now" />
                      <mm:constraint field="start" operator="greater" value="today - 1 year" />
                      <mm:size id="current" write="false" />
                    </mm:relatednodescontainer>
                    <!-- if this is an 'old' class, then cache very long, otherwise, not so long -->
                    <os:cache time="${current gt 0 ? 300 : 15000}" key="progress-${education}-class-${classNode}">

                      <tr>
                        <td colspan="100">
                          <b><di:translate key="progress.class" />: <mm:field name="name"/></b>
                        </td>
                      </tr>
                      i                    <mm:relatednodes role="classrel" type="people" id="student" orderby="$sort" >
                      <di:hasrole role="student" referid="student">
                        <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids,startAt,student,classNode@class">
                          <mm:param name="direct_connection">false</mm:param>
                        </mm:treeinclude>
                      </di:hasrole>
                    </mm:relatednodes>
                    </os:cache>

                  </mm:relatednodes>
                </mm:node>
              </mm:isempty>

              <!--
                  If the user has role 'teacher' he may see all students in the current class.
              -->
              <di:hasrole role="teacher,systemadministrator">
                <mm:isnotempty referid="class">
                  <mm:node referid="class">
                    <p><mm:field name="name" /></p>
                    <mm:timer name="teacher_people">
                      <mm:relatednodes type="people" role="classrel" orderby="$sort" id="student">
                        <di:hasrole role="student" referid="student">
                          <mm:treeinclude page="/progress/progress_row.jsp"
                                          objectlist="$includePath"
                                          referids="$referids,startAt,class,student">
                            <mm:param name="direct_connection">false</mm:param>
                          </mm:treeinclude>
                        </di:hasrole>
                      </mm:relatednodes>
                    </mm:timer>
                  </mm:node>
                </mm:isnotempty>
              </di:hasrole>

              <!--
                  If the user has role 'coach' he may see all students in his workgroup.
              -->
              <di:hasrole role="teacher" inverse="true">
                <di:hasrole role="coach">
                  <mm:isnotempty referid="class">
                    <mm:node referid="class">
                      <p><mm:field name="name" /></p>
                      <mm:timer name="work">
                        <mm:relatednodes element="workgroups"
                                         path="workgroups,people"
                                         orderby="people.$sort" constraints="people.number='$user'">
                          <p><mm:field name="name" /></p>
                          <mm:relatednodes id="studentnumber" type="people" role="related" orderby="$sort">
                            <di:hasrole role="student" referid="studentnumber">
                              <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath"
                                              referids="$referids,startAt,class,studentnumber@student">
                                <mm:param name="direct_connection">false</mm:param>
                              </mm:treeinclude>
                            </di:hasrole>
                          </mm:relatednodes>
                          <c:if test="${fn:length(studentnumber) le 1}">
                            <p>No students</p>
                          </c:if>
                        </mm:relatednodes>
                      </mm:timer>
                    </mm:node>
                  </mm:isnotempty>
                </di:hasrole>
              </di:hasrole>
            </table>
          </mm:node>
        </div>
      </div>
        </div>
      </div>
    </di:has>
  </di:html>
</jsp:root>
