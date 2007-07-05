<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:cloud method="asis" >
  <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
        
  <mm:import id="scope" externid="scope"/>
  <mm:compare referid="scope" value="provider">
    
    <mm:import id="editcontextname" reset="true">cursuseditor</mm:import>
    <jsp:directive.include file="/education/wizards/roles_chk.jsp" />
    <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW"> 
      <div class="menuSeperator"></div>
      <div class="menuItem" id="coursemanagement">
        <a href="<mm:treefile page="/education/wizards/index.jsp" objectlist="$includePath" referids="$referids,user?" />" class="menubar">
          <di:translate key="education.coursemanagement" />
        </a>
      </div>
    </mm:islessthan>
  </mm:compare>
</mm:cloud>
