<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="delegate">
<%@include file="/shared/setImports.jsp" %>

<mm:import jspvar="link" id="link"><mm:treefile page="/cmshelp/frontoffice/index.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>&node=</mm:import>
<mm:listnodes type="helpcontainers">    
  <mm:first>  
    <mm:field id="helpcontainer" name="number" write="false" />
  </mm:first>   
</mm:listnodes> 

<mm:node number="$helpcontainer" notfound="skipbody">
  <mm:relatednodes type="helpnodes">
  
<!--     in application bar should be shown only help with roles set education not set. -->
    
    <mm:remove referid="educationRelated" />
    <mm:relatednodes type="educations">
      <mm:first>
        <mm:import id="educationRelated" />
      </mm:first>
    </mm:relatednodes>
  
    <mm:notpresent referid="educationRelated">        
      <mm:import id="helpname" reset="true"><mm:field name="name"/></mm:import> 
      <mm:import id="helpnumber" jspvar="helpNumber" reset="true"><mm:field name="number"/></mm:import>
      <mm:relatednodes type="roles">
        <mm:import id="role" jspvar="role" reset="true"><mm:field name="name"/></mm:import>
        <mm:node number="$user" notfound="skipbody">
          <di:hasrole role="<%=role%>">
            <div class="menuSeparatorApplicationMenubar"></div>
            <div class="menuItemApplicationMenubar">
              <a title="<mm:write referid="helpname"/>" href="<%=link%><%=helpNumber%>"  class="menubar"><mm:write referid="helpname"/></a>
            </div>
          </di:hasrole>
        </mm:node>         
      </mm:relatednodes>
    </mm:notpresent>
  </mm:relatednodes> 
</mm:node>
 
</mm:cloud>
</mm:content>
