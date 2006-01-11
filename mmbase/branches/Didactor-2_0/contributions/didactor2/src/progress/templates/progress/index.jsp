<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.*" %>
<%@ page import = "nl.didactor.component.education.utils.EducationPeopleConnector" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>
<%@include file="/education/wizards/roles_defs.jsp" %>
<mm:import id="editcontextname" reset="true">docent schermen</mm:import>
<%@include file="/education/wizards/roles_chk.jsp" %>

<mm:islessthan referid="rights" referid2="RIGHTS_RW">
  <di:hasrole role="student">
    <jsp:forward page="student.jsp"/>
  </di:hasrole>
</mm:islessthan>

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

         <div class="contentBodywit">
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
             <th style="border-color:#000000; border-left:0px"><img src="<mm:image template="$template"/>"></th>
          </mm:node>
       </mm:node>

<% } %>


   <% //If the man is connected directly to education this man is a mega techer for this education %>
   <mm:compare referid="class" value="null">
      <mm:node referid="education" jspvar="nodeEducation">
         <% //We have to count number of tests for colspan in rows
            int iNumberOfColumns = 4;
         %>
         <mm:relatednodescontainer type="learnobjects" role="posrel">
            <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">
               <mm:import id="nodetype" reset="true"><mm:nodeinfo type="type" /></mm:import>
               <mm:compare referid="nodetype" value="tests">
                  <%
                     iNumberOfColumns++;
                  %>
               </mm:compare>
            </mm:tree>
         </mm:relatednodescontainer>

         <tr>
            <td style="border-color:#000000; border-top:0px; border-left:0px" colspan="<%= iNumberOfColumns %>"><b><di:translate key="progress.directconnection" />:</b></td>
         </tr>
         <mm:related path="classrel,people" orderby="people.lastname">
            <mm:node element="people">
               <mm:import id="list_student_number" reset="true"><mm:field name="number"/></mm:import>
               <di:hasrole role="student" referid="list_student_number">
                  <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids,startAt">
                     <mm:param name="student"><mm:field name="number"/></mm:param>
                     <mm:param name="direct_connection">true</mm:param>
                  </mm:treeinclude>
               </di:hasrole>
            </mm:node>
         </mm:related>
         <mm:related path="classrel,classes" orderby="classes.name">
            <mm:node element="classes">
               <tr>
                  <td style="border-color:#000000; border-top:0px; border-left:0px" colspan="<%= iNumberOfColumns %>"><b><di:translate key="progress.class" />: <mm:field name="name"/></b></td>
               </tr>
               <mm:import id="temp_class" reset="true"><mm:field name="number"/></mm:import>
               <mm:related path="classrel,people">
                  <mm:node element="people">
                     <mm:import id="list_student_number" reset="true"><mm:field name="number"/></mm:import>
                     <di:hasrole role="student" referid="list_student_number">
                        <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids,startAt,class">
                           <mm:param name="student"><mm:field name="number"/></mm:param>
                           <mm:param name="direct_connection">false</mm:param>
                           <mm:param name="class"><mm:write referid="temp_class"/></mm:param>
                           <mm:param name="temp_class"><mm:write referid="temp_class"/></mm:param>
                        </mm:treeinclude>
                     </di:hasrole>
                  </mm:node>
               </mm:related>
            </mm:node>
         </mm:related>
      </mm:node>
   </mm:compare>

   <%--
     If the user has role 'teacher' he may see all students in the current class. 
   --%>
   <di:hasrole role="teacher">
     <mm:compare referid="class" value="null" inverse="true">
       <mm:node referid="class">
         <mm:relatednodes type="people">
            <mm:import id="studentnumber" reset="true"><mm:field name="number"/></mm:import>
            <di:hasrole role="student" referid="studentnumber">
               <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids,startAt,class">
                  <mm:param name="student"><mm:field name="number"/></mm:param>
                  <mm:param name="direct_connection">false</mm:param>
               </mm:treeinclude>
            </di:hasrole>
         </mm:relatednodes>
       </mm:node>
     </mm:compare>
   </di:hasrole>

   <%--
     If the user has role 'coach' he may see all students in his workgroup.
   --%>
   <di:hasrole role="coach">
     <mm:compare referid="class" value="null" inverse="true">
       <mm:node referid="class">
         <mm:related path="workgroups,people" constraints="people.number='$user'">
           <mm:node element="workgroups">
             <mm:relatednodes type="people">
               <mm:import id="studentnumber" reset="true"><mm:field name="number"/></mm:import>
               <di:hasrole role="student" referid="studentnumber">
                 <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids,startAt,class">
                   <mm:param name="student"><mm:field name="number"/></mm:param>
                   <mm:param name="direct_connection">false</mm:param>
                 </mm:treeinclude>
               </di:hasrole>
             </mm:relatednodes>
           </mm:node>
         </mm:related>
       </mm:node>  
     </mm:compare>
   </di:hasrole>
</table>

<% if (showNextLink) { %>

<span style="float: right"><a href="<mm:treefile  page="/progress/index.jsp" objectlist="$includePath" referids="$referids">

    <mm:param name="startAt"><%= startAt + 15 %></mm:param>

</mm:treefile>"><di:translate key="progress.next15" /></a></span>

<%
   }

   if (showPrevLink)
   {
      %>
         <a href="<mm:treefile  page="/progress/index.jsp" objectlist="$includePath" referids="$referids">
                     <mm:param name="startAt"><%= startAt - 15 %></mm:param>
                  </mm:treefile>"><di:translate key="progress.previous15" /></a>
      <%
   }
%>

 </div>

</div>

</mm:node>

</mm:islessthan>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>

</mm:content>

