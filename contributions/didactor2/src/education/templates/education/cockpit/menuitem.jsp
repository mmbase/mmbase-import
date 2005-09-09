<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<fmt:bundle basename="nl.didactor.component.education.EducationMessageBundle">
   <%@include file="/shared/setImports.jsp" %>
   <%@include file="/education/wizards/roles_defs.jsp" %>
        
   <mm:import id="scope" externid="scope"/>
   
   <mm:compare referid="scope" value="provider">

      <mm:import id="editcontextname" reset="true">cursuseditor</mm:import>
      <%@include file="/education/wizards/roles_chk.jsp" %>
      <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW"> 
         <div class="menuSeperator"></div>
         <div class="menuItem" id="coursemanagement">
            <a href="<mm:treefile page="/education/wizards/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar">
               <fmt:message key="coursemanagement"/>
            </a>
         </div>
      </mm:islessthan>
   </mm:compare>
</fmt:bundle>
</mm:cloud>
</mm:content>
