<%@ page import = "java.util.HashSet" %>
<%@ page import = "java.util.Iterator" %>
<%@page import = "nl.didactor.component.education.utils.EducationPeopleConnector" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud jspvar="cloud" method="delegate">
   <%@include file="/shared/setImports.jsp" %>
   <%@include file="/education/wizards/roles_defs.jsp" %>

   <%
      //education-people connector
      EducationPeopleConnector educationPeopleConnector = new EducationPeopleConnector(cloud);
   %>

   <style type="text/css">
     .education_top_menu_selected {
       background:#888586;
     }
     .education_top_menu_nonselected {
       background:#DEDEDE;
     }
   </style>

   <mm:import externid="mode">components</mm:import>
   <%
      HashSet hsetEducations = null;
   %>
   <mm:node number="$user" jspvar="node">
      <%
         hsetEducations = educationPeopleConnector.relatedEducations("" + node.getNumber());
      %>
   </mm:node>


   <mm:import id="editcontextname" reset="true">componenten</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a <mm:compare referid="mode" value="components">class="education_top_menu_selected"</mm:compare> href="?mode=components" style="font-weight:bold;"><di:translate key="education.educationmenucomponents" /></a>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">rollen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a <mm:compare referid="mode" value="roles">class="education_top_menu_selected"</mm:compare> href="?mode=roles" style="font-weight:bold;"><di:translate key="education.educationmenupersons" /></a>
   </mm:islessthan>

   <mm:node number="component.pop" notfound="skipbody">
     <%// A user will see a Competence submenu only if POP component is switched ON %>
     <mm:relatednodes type="providers" constraints="providers.number=$provider">
       <mm:import id="editcontextname" reset="true">competentie</mm:import>
       <%@include file="/education/wizards/roles_chk.jsp" %>
       <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
         <a <mm:compare referid="mode" value="competence">class="education_top_menu_selected"</mm:compare> href="?mode=competence" style="font-weight:bold;"><di:translate key="education.educationmenucompetence" /></a>
       </mm:islessthan>
     </mm:relatednodes>
   </mm:node>

   <mm:node number="component.metadata" notfound="skipbody">
     <mm:import id="editcontextname" reset="true">metadata</mm:import>
     <%@include file="/education/wizards/roles_chk.jsp" %>
     <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
       <a <mm:compare referid="mode" value="metadata">class="education_top_menu_selected"</mm:compare> href="?mode=metadata" style="font-weight:bold;"><di:translate key="education.educationmenumetadata" /></a>
     </mm:islessthan>
   </mm:node>

   <mm:import id="editcontextname" reset="true">contentelementen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a <mm:compare referid="mode" value="content_metadata">class="education_top_menu_selected"</mm:compare> href="?mode=content_metadata" style="font-weight:bold;"><di:translate key="education.educationmenucontentmetadata" /></a>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">filemanagement</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a <mm:compare referid="mode" value="filemanagement">class="education_top_menu_selected"</mm:compare> href="?mode=filemanagement" style="font-weight:bold;"><di:translate key="education.educationmenufilemanagement" /></a>
   </mm:islessthan>
   
   <mm:node number="component.virtualclassroom" notfound="skipbody">
     <mm:import id="editcontextname" reset="true">virtualclassroom</mm:import>
     <%@include file="/education/wizards/roles_chk.jsp" %>
     <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
       <a <mm:compare referid="mode" value="virtualclassroom">class="education_top_menu_selected"</mm:compare> href="?mode=virtualclassroom" style="font-weight:bold;"><di:translate key="virtualclassroom.virtualclassroom" /></a>
     </mm:islessthan>   
   </mm:node>  

   <mm:import id="editcontextname" reset="true">toetsen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a <mm:compare referid="mode" value="tests">class="education_top_menu_selected"</mm:compare> href="?mode=tests" style="font-weight:bold;"><di:translate key="education.educationmenutests" /></a>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">opleidingen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <span <mm:compare referid="mode" value="educations">class="education_top_menu_selected"</mm:compare>>
     <% if (hsetEducations.size() < 2) { %>
       <a href="?mode=educations" style="font-weight:bold;"><di:translate key="education.educationmenueducations" /></a>
     <% } else { %>
       <span class="menu_font"><di:translate key="education.educationmenueducations" /></span>
       <script>
         function chooseEducation(eid) {
           if (eid != 0) {
             document.location.href = "?mode=educations&education_topmenu_course=" + eid;
           }
         }
       </script>
       <style>
         .navigationbar {
           height: 25px;
         }
         .folders {
           top: 30px;
         }
         .maincontent {
           top: 30px;
         }
       </style>
       <select name="course" id="course">
         <option value="0">--------</option>
         <%
         for(Iterator it = hsetEducations.iterator(); it.hasNext();) {
           String sEducationID = (String) it.next();
           %>
           <option
              <% if((request.getParameter("education_topmenu_course") != null) && (request.getParameter("education_topmenu_course").equals(sEducationID))) out.print(" selected=\"selected\" "); %>
              value="<%=sEducationID%>">
                <mm:node number="<%=sEducationID%>"><mm:field name="name"/></mm:node>
           </option>
           <%
         }
       %>
       </select>
       <img src="gfx/ga.gif" title="Ga" alt="Ga" onclick="chooseEducation(document.getElementById('course').value)" />
     <% } %>
     </span>
  </mm:islessthan>
</mm:cloud>
