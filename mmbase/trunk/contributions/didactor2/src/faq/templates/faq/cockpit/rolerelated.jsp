<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="delegate">
<%@include file="/shared/setImports.jsp" %>

<mm:import jspvar="link" id="link"><mm:treefile page="/faq/frontoffice/index.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>&node=</mm:import>
<mm:listnodes type="faqcontainers">    
  <mm:first>  
    <mm:field id="faqcontainer" name="number" write="false" />
  </mm:first>   
</mm:listnodes> 

<mm:node number="$faqcontainer" notfound="skipbody">
  <mm:relatednodes type="faqnodes">
    <mm:import id="faqname" reset="true"><mm:field name="name"/></mm:import> 
    <mm:import id="faqnumber" jspvar="faqNumber" reset="true"><mm:field name="number"/></mm:import>
    <mm:remove referid="faqnodeshown" />
    <mm:relatednodes type="roles">
      <mm:import id="role" jspvar="role" reset="true"><mm:field name="name"/></mm:import>
      <mm:node number="$user" notfound="skipbody">
        <di:hasrole role="<%=role%>">
          <mm:notpresent referid="faqnodeshown">
            <div class="menuSeperatorApplicationMenubar"></div>
            <div class="menuItemApplicationMenubar">
              <a title="<mm:write referid="faqname"/>" href="<%=link%><%=faqNumber%>"  class="menubar"><mm:write referid="faqname"/></a>
            </div>
            <mm:import id="faqnodeshown" />
          </mm:notpresent>
        </di:hasrole>
      </mm:node>
    </mm:relatednodes>
  </mm:relatednodes>
</mm:node> 
</mm:cloud>
</mm:content>
