<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
   <%@include file="/shared/setImports.jsp" %>
   <%@include file="/education/wizards/roles_defs.jsp" %>
        
   <mm:import id="scope" externid="scope"/>
   
   <mm:compare referid="scope" value="provider">

      <mm:import id="editcontextname" reset="true">cursuseditor</mm:import>
      <%@include file="/education/wizards/roles_chk.jsp" %>
      <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW"> 
         <div class="menuSeperator"></div>
         <div class="menuItem" id="coursemanagement">
            <a href="<mm:treefile page="/education/wizards/index.jsp" objectlist="$includePath" referids="$referids">
                        <mm:present referid="user"><mm:param name="user"><mm:write referid="user"/></mm:param></mm:present>
                     </mm:treefile>" class="menubar">
               <di:translate key="education.coursemanagement" />
            </a>
         </div>
      </mm:islessthan>
   </mm:compare>
</mm:cloud>
</mm:content>
