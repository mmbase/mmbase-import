<%@ page import = "java.util.HashSet" %>
<%@ page import = "java.util.Iterator" %>
<%@page import = "nl.didactor.component.education.utils.EducationPeopleConnector" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<mm:cloud jspvar="cloud" method="asis">
   <%@include file="/shared/setImports.jsp" %>
   <%@include file="/education/wizards/roles_defs.jsp" %>

   <%
      //education-people connector
      EducationPeopleConnector educationPeopleConnector = new EducationPeopleConnector(cloud);

      if(request.getParameter("mode") != null) {
         session.setAttribute("education_topmenu_mode", request.getParameter("mode"));
      }

      if(request.getParameter("course") != null) {
         session.setAttribute("education_topmenu_course", request.getParameter("course"));
      }

      if(session.getAttribute("education_topmenu_mode") == null) {//Default active element in education top menu
         session.setAttribute("education_topmenu_mode", "components");
      }
   %>

   <style type="text/css">
     .education_top_menu_selected {
       background:#888586;
     }
     .education_top_menu_nonselected {
       background:#DEDEDE;
     }
   </style>

   <mm:import id="education_top_menu"><%= session.getAttribute("education_topmenu_mode") %></mm:import>
   <%
      HashSet hsetEducations = null;
   %>
   <mm:node number="$user" jspvar="node">
      <%
         hsetEducations = educationPeopleConnector.relatedEducations("" + node.getNumber());
      %>
   </mm:node>


   <fmt:bundle basename="nl.didactor.component.education.EducationMessageBundle">
   <mm:import id="editcontextname" reset="true">componenten</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a href="?mode=components" style="font-weight:bold;"><fmt:message key="educationMenuComponents"/></a>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">rollen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a href="?mode=roles" style="font-weight:bold;"><fmt:message key="educationMenuPersons"/></a>
   </mm:islessthan>

   <mm:node number="component.pop" notfound="skip">
     <%// A user will see a Competence submenu only if POP component is switched ON %>
     <mm:relatednodes type="providers" constraints="providers.number=$provider">
       <mm:import id="editcontextname" reset="true">competentie</mm:import>
       <%@include file="/education/wizards/roles_chk.jsp" %>
       <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
         <a href="?mode=competence" style="font-weight:bold;"><fmt:message key="educationMenuCompetence"/></a>
       </mm:islessthan>
     </mm:relatednodes>
   </mm:node>

   <mm:import id="editcontextname" reset="true">metadata</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a href="?mode=metadata" style="font-weight:bold;"><fmt:message key="educationMenuMetadata"/></a>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">contentelementen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a href="?mode=content_metadata" style="font-weight:bold;"><fmt:message key="educationMenuContentMetadata"/></a>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">filemanagement</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a href="?mode=filemanagement" style="font-weight:bold;"><fmt:message key="educationMenuFilemanagement"/></a>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">toetsen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <a href="?mode=tests" style="font-weight:bold;"><fmt:message key="educationMenuTests"/></a>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">opleidingen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <% if (hsetEducations.size() < 2) { %>
       <a href="?mode=educations" style="font-weight:bold;"><fmt:message key="educationMenuEducations"/></a>
     <% } else { %>
       <fmt:message key="educationMenuEducations"/>
       <script>
         function chooseEducation(eid) {
           if (eid != 0) {
             document.location.href = "?mode=educations&education_topmenu_course=" + eid;
           }
         }
       </script>
       <select name="course" onChange="chooseEducation(this.value)">
         <option value="0">--------</option>
         <%
         for(Iterator it = hsetEducations.iterator(); it.hasNext();) {
           String sEducationID = (String) it.next();
           %>
           <option 
              <% if((session.getAttribute("education_topmenu_course") != null) && (session.getAttribute("education_topmenu_course").equals(sEducationID))) out.print(" selected=\"selected\" "); %> 
              value="<%=sEducationID%>">
                <mm:node number="<%=sEducationID%>"><mm:field name="name"/></mm:node>
           </option>
           <%
         }
       %>
       </select>
     <% } %>
  </mm:islessthan>
</fmt:bundle>
</mm:cloud>
