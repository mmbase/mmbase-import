<jsp:root version="1.2"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          xmlns:os="http://www.opensymphony.com/oscache"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:c="http://java.sun.com/jsp/jstl/core">
  <di:frame
      postprocessor="none"
      type="application/xhtml+xml"
      rank="didactor user"
      component="progress"
      title_key="progress.progresstitle">
    <!--
        This JSP actually remains one entire WTF
        - bloated
        - can produce ridiculously large results
        - is hard to understand, because of horrible switches,

    -->


    <jsp:directive.include file="/education/tests/definitions.jsp" />
    <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
    <mm:import id="editcontextname" reset="true">docent schermen</mm:import>
    <jsp:directive.include file="/education/wizards/roles_chk.jsp" />

    <mm:islessthan referid="rights" referid2="RIGHTS_RW">
      <di:hasrole role="student">
        <!-- aaah -->
        <jsp:forward page="student.jsp"/>
      </di:hasrole>
    </mm:islessthan>

    <di:getsetting id="sort" component="core" setting="personorderfield" write="false" />


    <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">

      <mm:node number="$education">
        <b><mm:field name="name" write="true"/></b>

        <!-- WTF, style in JSP -->
        <table class="font">

          <mm:nodelistfunction name="tests" id="tests" />

          <mm:import externid="startAt" vartype="integer">0</mm:import>

          <tr>
            <th />
            <mm:node number="progresstextbackground">
              <th>
                <mm:import id="tr_progresstitle"><di:translate key="progress.progresstitle" /></mm:import>
                <mm:image mode="img"
                          alt="${tr_progresstitle}"
                          template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,'$tr_progresstitle')+rotate(90)"/>
              </th>
              <th>
                <mm:import id="tr_logins"><di:translate key="progress.logins" /></mm:import>
                <mm:image mode="img"
                          alt="${tr_logins}"
                          template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,'$tr_logins')+rotate(90)"/>
              </th>
              <th>
                <mm:import id="tr_online"><di:translate key="progress.online"/></mm:import>
                <mm:image mode="img"
                          alt="${tr_online}"
                          template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,'$tr_online')+rotate(90)"/>
              </th>
              <th>
                <mm:import id="tr_lastlogin"><di:translate key="progress.lastlogin"/></mm:import>
                <mm:image mode="img"
                          alt="${tr_lastlogin}"
                          template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,'$tr_lastlogin')+rotate(90)"/>
              </th>
            </mm:node>

            <mm:listnodes referid="tests">
              <mm:escaper id="title" type="regexps">
                <mm:param name="mode">entire</mm:param>
                <mm:param name="patterns">
                  <mm:param name="\s+">_</mm:param>
                  <mm:param name='"'>''</mm:param>
                </mm:param>
              </mm:escaper>
              <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,"<mm:field name="name" escape="title" />")+rotate(90)</mm:import>
              <mm:node number="progresstextbackground">
                <th>
                  <mm:image
                      alt="${test.name}"
                      template="$template" mode="img" />
                </th>
              </mm:node>
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
                  <mm:relatednodes role="classrel" type="people" id="student" orderby="$sort" >
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
  </mm:islessthan>
</di:frame>
</jsp:root>
