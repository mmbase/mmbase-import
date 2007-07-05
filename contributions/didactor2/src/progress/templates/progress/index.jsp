<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.opensymphony.com/oscache" prefix="os"
%><%@page import="java.util.*" 
%><mm:content postprocessor="none">

<mm:cloud rank="didactor user">

  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="/education/tests/definitions.jsp" />
  <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
  <mm:import id="editcontextname" reset="true">docent schermen</mm:import>
  <jsp:directive.include file="/education/wizards/roles_chk.jsp" />

  <mm:islessthan referid="rights" referid2="RIGHTS_RW">
    <di:hasrole role="student">
      <jsp:forward page="student.jsp"/>
    </di:hasrole>
  </mm:islessthan>

  <di:getsetting id="sort" component="core" setting="personorderfield" write="false" />

  <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
    <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="extraheader">
        <title><di:translate key="progress.progresstitle"/></title>
      </mm:param>
    </mm:treeinclude>



    <div class="rows">
      <div class="navigationbar">
        <div class="titlebar"><di:translate key="progress.progresstitle"/></div>
      </div>
      
      <div class="folders">
        <div class="folderHeader">&nbsp;</div>
        <div class="folderBody">&nbsp;</div>
      </div>
      
      <div class="mainContent">
        <div class="contentHeader">
          <%--    Some buttons working on this folder--%>
        </div>
        
        <div class="contentBodywit"><%-- wit is neither english, nor symbolical. It means 'white', which it probably isn't. --%>
        <mm:node number="$education">
          <b><mm:field name="name" write="true"/></b>
          <table class="font" border="1" cellspacing="0" style="border-color:#000000; border-bottom:0px; border-top:0px; border-right:0px">
            <% List tests = new ArrayList(); %>
            <mm:relatednodescontainer type="learnobjects" role="posrel">
              <mm:sortorder field="posrel.pos" direction="up"/>
              <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">
                <mm:import id="nodetype" reset="true"><mm:nodeinfo type="type" /></mm:import>
                <mm:compare referid="nodetype" value="tests">
                  <mm:field name="number" jspvar="testNum" vartype="String">
                    <% tests.add(testNum); %>
                  </mm:field>
                </mm:compare>
              </mm:tree>
            </mm:relatednodescontainer>

            <mm:import id="startAt" externid="startAt" jspvar="sStartAt" vartype="Integer">0</mm:import>

            <tr>
              <th style="border-color:#000000; border-left:0px">&nbsp;</th>
              <mm:node number="progresstextbackground">
                <th style="border-color:#000000; border-left:0px">
                  <mm:import id="tr_progresstitle" vartype="String"><di:translate key="progress.progresstitle" /></mm:import>
                  <img src="<mm:image template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,'$tr_progresstitle')+rotate(90)"/>">
                </th>
                <th style="border-color:#000000; border-left:0px">
                  <mm:import id="tr_logins" vartype="String"><di:translate key="progress.logins" /></mm:import>
                  <img src="<mm:image template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,'$tr_logins')+rotate(90)"/>">
                </th>
                <th style="border-color:#000000; border-left:0px">
                  <mm:import id="tr_online" vartype="String"><di:translate key="progress.online"/></mm:import>
                  <img src="<mm:image template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,'$tr_online')+rotate(90)"/>">
                </th>
                <th style="border-color:#000000; border-left:0px">
                  <mm:import id="tr_lastlogin" vartype="String"><di:translate key="progress.lastlogin"/></mm:import>
                  <img src="<mm:image template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,'$tr_lastlogin')+rotate(90)"/>">
                </th>
              </mm:node>
<%
   int testCounter = 0;
   int startAt = sStartAt.intValue();
   boolean showPrevLink = false;

   if (startAt > 0)
   {
       showPrevLink = true;
   }

   boolean showNextLink = false;

   Iterator testIterator = tests.iterator();

   while (testIterator.hasNext())
   {
       String testNum = (String) testIterator.next();

       if ( testCounter++ < startAt )
       {
           continue;
       }

       if ( testCounter > startAt + 15)
       {
           showNextLink = true;
           break;
       }

        %>

              <mm:node number="<%= testNum %>">
                <mm:field name="name" jspvar="name" vartype="String">
                  <% name  = name.replaceAll("\\s+","_").replaceAll("\"","''"); %>
                  <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(10,10,"<%= name %>")+rotate(90)</mm:import>
                </mm:field>
                <mm:node number="progresstextbackground">
                  <th style="border-color:#000000; border-left:0px">
                    <mm:image template="$template" mode="img" />
                  </th>
                </mm:node>
              </mm:node>

<% } %>


            <% //If the man is connected directly to education this man is a mega techer for this education %>

              <mm:compare referid="class" valueset=",null"> <%-- This regexp trick is  a bit silly, but I don't know where the 'null' comes from --%>
                <mm:node referid="education" jspvar="nodeEducation">
                  <% //We have to count number of tests for colspan in rows
                    int iNumberOfColumns = 4;
                  %>
                  <mm:relatednodescontainer type="learnobjects" role="posrel">
                    <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">
                      <mm:import id="nodetype" reset="true"><mm:nodeinfo type="type" /></mm:import>
                      <mm:compare referid="nodetype" value="tests">
                         <% iNumberOfColumns++; %>
                      </mm:compare>
                    </mm:tree>
                  </mm:relatednodescontainer>

                  <tr>
                    <td style="border-color:#000000; border-top:0px; border-left:0px" colspan="<%= iNumberOfColumns %>"><b><di:translate key="progress.directconnection" />:</b></td>
                  </tr>
                  <mm:time id="now" time="now" write="false" precision="hours" />
                  <mm:time id="lastweek" time="now - 1 week" write="false" precision="hours" />
                  <os:cache time="600" key="progress-${education}-people">
                    <mm:timer name="people">
                      <mm:relatednodes role="classrel" type="people" orderby="$sort" >
                        <mm:import id="list_student_number" reset="true"><mm:field name="number"/></mm:import>
                        <mm:field name="lastactivity" id="lastactivity" write="false" />
                        <os:cache time="${lastactivity lt lastweek ? 15000 : 300}" key="student-${education}-people-${list_student_number}">
                          <di:hasrole role="student" referid="list_student_number">
                            <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids,startAt">
                              <mm:param name="student"><mm:field name="number"/></mm:param>
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
                      <mm:size id="current" write="false" />
                    </mm:relatednodescontainer>
                    <%-- if this is an 'old' class, then cache very long, otherwise, not so long --%>
                    <os:cache time="${current gt 0 ? 300 : 15000}" key="progress-${education}-class-${classNode}">
                      
                    <tr>
                      <td style="border-color:#000000; border-top:0px; border-left:0px" colspan="<%= iNumberOfColumns %>"><b><di:translate key="progress.class" />: <mm:field name="name"/></b></td>
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
              </mm:compare>

           <%--
             If the user has role 'teacher' he may see all students in the current class. 
           --%>
              <di:hasrole role="teacher">
                <mm:compare referid="class" regexp="|null" inverse="true">
                  <mm:node referid="class">
                    <mm:timer name="teacher_people">
                      <mm:relatednodes type="people" role="classrel" orderby="$sort" id="student">
                        <di:hasrole role="student" referid="student">
                          <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids,startAt,class,student">
                            <mm:param name="direct_connection">false</mm:param>
                          </mm:treeinclude>
                        </di:hasrole>
                      </mm:relatednodes>
                    </mm:timer>
                 </mm:node>
                </mm:compare>
              </di:hasrole>
              
           <%--
             If the user has role 'coach' he may see all students in his workgroup.
           --%>
           <di:hasrole role="teacher" inverse="true">
              <di:hasrole role="coach">
                <mm:compare referid="class" valueset=",null" inverse="true">
                  <mm:node referid="class">
                    <mm:timer name="work">
                      <mm:related path="workgroups,people" orderby="people.$sort" constraints="people.number='$user'">
                        <mm:node element="workgroups">
                          <!-- oddddd -->
                          <mm:relatednodes type="people" role="related" orderby="$sort">
                            <mm:import id="studentnumber" reset="true"><mm:field name="number"/></mm:import>
                            <di:hasrole role="student" referid="studentnumber">
                              <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids,startAt,class,studentnumber@student">
                                <mm:param name="direct_connection">false</mm:param>
                              </mm:treeinclude>
                            </di:hasrole>
                          </mm:relatednodes>
                        </mm:node>
                    </mm:related>
                    </mm:timer>
                  </mm:node>  
                </mm:compare>
              </di:hasrole>
           </di:hasrole>
            </tr>
          </table>

          <% if (showNextLink) { %>
            <span style="float: right">
              <a href="<mm:treefile  page="/progress/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="startAt"><%= startAt + 15 %></mm:param></mm:treefile>">
                <di:translate key="progress.next15" />
              </a>
            </span>
          <%
          }
          if (showPrevLink)
          {
          %>
            <a href="<mm:treefile  page="/progress/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="startAt"><%= startAt - 15 %></mm:param></mm:treefile>">
              <di:translate key="progress.previous15" />
            </a>
          <%
          }
          %>
        </mm:node>
      </div><%-- class="contentBodywit" --%>
    </div><%-- class="mainContent" --%>
  </div><%-- class="rows" --%>
</mm:islessthan>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>

