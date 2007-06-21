<%@ page import = "java.util.*" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" 
%><mm:cloud method="delegate" jspvar="cloud">
   <jsp:directive.include file="/shared/setImports.jsp" />
   <jsp:directive.include file="/education/wizards/roles_defs.jsp" />

   <mm:import externid="e" jspvar="chosen_education" vartype="string">${education}</mm:import>
   <style type="text/css">
     .education_top_menu {
       font-weight: bold;
     }
     .education_top_menu.selected {
       background:#888586;
       }
   </style>

   <mm:import externid="mode">components</mm:import>

   <mm:node number="$user" >
     <mm:hasrank value="administrator">
       <mm:listnodes type="educations" id="educations" />
     </mm:hasrank>
     <mm:hasrank value="administrator" inverse="true">
       <mm:nodelistfunction name="educations" id="educations" />
     </mm:hasrank>
   </mm:node>


   <!-- Follows lots of code duplication, why not iterate over all modes or so? -->
   
   <mm:import id="editcontextname" reset="true">componenten</mm:import>
   <jsp:directive.include file="roles_chk.jsp" />
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <mm:link referids="e" page=".">
       <mm:param name="mode">components</mm:param>       
       <a class="${mode eq 'components' ? education_top_menu_selected : ''}"
          href="${_}">
         <di:translate key="education.educationmenucomponents" />
       </a>
     </mm:link>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">rollen</mm:import>
   <jsp:directive.include file="roles_chk.jsp" />
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <mm:link page="." referids="e">
       <mm:param name="mode">roles</mm:param>       
       <a class="education_top_menu ${mode eq 'roles' ? 'selected' : ''}"
          href="${_}"><di:translate key="education.educationmenupersons" /></a>
     </mm:link>

   </mm:islessthan>

   <mm:node number="component.pop" notfound="skipbody">
     <%// A user will see a Competence submenu only if POP component is switched ON %>
     <mm:relatednodes type="providers" constraints="providers.number=$provider">
       <mm:import id="editcontextname" reset="true">competentie</mm:import>
       <jsp:directive.include file="roles_chk.jsp" />
       <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
         <mm:link page="." referids="e">
           <mm:param name="mode">compentence</mm:param>       
           <a class="education_top_menu ${mode eq 'compentence' ? 'selected' : ''}"
              href="${_}">
           <di:translate key="education.educationmenucompetence" /></a>
         </mm:link>
       </mm:islessthan>
     </mm:relatednodes>
   </mm:node>

   <mm:node number="component.metadata" notfound="skipbody">
     <mm:import id="editcontextname" reset="true">metadata</mm:import>
     <jsp:directive.include file="roles_chk.jsp" />
     <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
       <mm:link page="." referids="e">
         <mm:param name="mode">metadata</mm:param>       
         <a class="education_top_menu ${mode eq 'metadata' ? 'selected' : ''}"
            href="${_}">
         <di:translate key="education.educationmenumetadata" /></a>
       </mm:link>
     </mm:islessthan>
   </mm:node>

   <mm:import id="editcontextname" reset="true">contentelementen</mm:import>
   <jsp:directive.include file="roles_chk.jsp" />
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <mm:link page="." referids="e">
       <mm:param name="mode">content_metadata</mm:param>       
       <a class="education_top_menu ${mode eq 'content_metadata' ? 'selected' : ''}"
          href="${_}">
       <di:translate key="education.educationmenucontentmetadata" /></a>
     </mm:link>          
   </mm:islessthan>

   
   <mm:import id="editcontextname" reset="true">filemanagement</mm:import>
   <jsp:directive.include file="roles_chk.jsp" />
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <mm:link page="." referids="e">
       <mm:param name="mode">filemanagement</mm:param>       
       <a class="education_top_menu ${mode eq 'filemanagement' ? 'selected' : ''}"
          href="${_}">
          <di:translate key="education.educationmenufilemanagement" /></a>
     </mm:link>
   </mm:islessthan>
   
   <!-- this is stupid -->

   <mm:node number="component.virtualclassroom" notfound="skipbody">
     <mm:import id="editcontextname" reset="true">virtualclassroom</mm:import>
     <jsp:directive.include file="roles_chk.jsp" />
     <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
       <a <mm:compare referid="mode" value="virtualclassroom">class="education_top_menu_selected"</mm:compare> href="?mode=virtualclassroom" style="font-weight:bold;"><di:translate key="virtualclassroom.virtualclassroom" /></a>
     </mm:islessthan>   
   </mm:node>  

   <mm:node number="component.proactivemail" notfound="skipbody">
     <mm:import id="editcontextname" reset="true">proactivemail</mm:import>
     <jsp:directive.include file="roles_chk.jsp" />
     <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
       <a <mm:compare referid="mode" value="proactivemail">class="education_top_menu_selected"</mm:compare> href="?mode=proactivemail" style="font-weight:bold;"><di:translate key="proactivemail.proactivemail" /></a>
     </mm:islessthan>   
   </mm:node>  

   <mm:import id="editcontextname" reset="true">toetsen</mm:import>
   <jsp:directive.include file="roles_chk.jsp" />
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <mm:link page="." referids="e">
       <mm:param name="mode">tests</mm:param>
       <a class="education_top_menu ${mode eq 'tests' ? 'selected' : ''}"
          href="${_}">
          <di:translate key="education.educationmenutests" /></a>
     </mm:link>
   </mm:islessthan>

   <mm:import id="editcontextname" reset="true">opleidingen</mm:import>
   <jsp:directive.include file="roles_chk.jsp" />
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <span class="education_top_menu ${mode eq 'educations' ? 'selected' : ''}">
       <mm:link page="." referids="e">
         <mm:param name="mode">educations</mm:param>
         <a href="${_}"><di:translate key="education.educationmenueducations" /></a>
       </mm:link>
       <c:if test="${fn:length(educations) ge 2}">
         <script>
           function chooseEducation(eid) {
           if (eid != 0) {
           document.location.href = "?mode=educations&e=" + eid;
           }
           }
         </script>
         <select name="course" id="course"
                 onchange="chooseEducation(this.value);">
           <option value="0">--------</option>
           <mm:listnodes referid="educations">
             <c:choose>
               <c:when test="${_node eq e}">
                 <option selected="selected" value="${_node}"><mm:field name="name" /></option>
               </c:when>
               <c:otherwise>
                 <option value="${_node}"><mm:field name="name" /></option>
               </c:otherwise>
             </c:choose>
           </mm:listnodes>
         </select>           
       </c:if>
     </span>
  </mm:islessthan>
</mm:cloud>
