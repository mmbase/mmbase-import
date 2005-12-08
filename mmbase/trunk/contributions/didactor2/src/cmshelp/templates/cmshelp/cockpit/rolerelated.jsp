<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="asis">
 <%@include file="/shared/setImports.jsp" %>

 <mm:import jspvar="link" id="link"><mm:treefile page="/cmshelp/frontoffice/index.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>&node=</mm:import>
 <mm:listnodes type="helpcontainers">    
  <mm:first>  
     <mm:field id="helpcontainer" name="number" write="false" />
  </mm:first>   
 </mm:listnodes> 
 <mm:node number="$helpcontainer" notfound="skipbody">
	  <mm:relatednodes type="helpnodes">
	    <mm:import id="helpname" reset="true"><mm:field name="name"/></mm:import> 
	    <mm:import id="helpnumber" jspvar="helpNumber" reset="true"><mm:field name="number"/></mm:import>
	    <mm:relatednodes type="roles">
	       <mm:import id="role" jspvar="role" reset="true"><mm:field name="name"/></mm:import>
	       <mm:node number="$user" notfound="skipbody">
	       <di:hasrole role="<%=role%>">
				    <div class="menuSeperatorApplicationMenubar"></div>
				    <div class="menuItemApplicationMenubar">
				      <a title="<mm:write referid="helpname"/>" href="<%=link%><%=helpNumber%>"  class="menubar"><mm:write referid="helpname"/></a>
				    </div>
         </di:hasrole>	       
         </mm:node>
	    </mm:relatednodes>    
	  </mm:relatednodes> 
 </mm:node> 
</mm:cloud>
</mm:content>

						    