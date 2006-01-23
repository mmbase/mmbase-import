<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import id="scope" externid="scope"/>   

<mm:compare referid="scope" value="education">
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
      <mm:relatednodes type="educations">
        <mm:import id="education" jspvar="education" reset="true"><mm:field name="name"/></mm:import>
        <mm:node number="$user" notfound="skipbody">
          <mm:relatednodes type="educations" constraints="educations.name='${education}'">
            <div class="menuSeperator"></div>
            <div class="menuItem">
              <a title="<mm:write referid="faqname"/>" href="<%=link%><%=faqNumber%>"  class="menubar"><mm:write referid="faqname"/></a>
            </div>
          </mm:relatednodes>       
        </mm:node>
      </mm:relatednodes>    
    </mm:relatednodes> 
  </mm:node>          
</mm:compare>      
</mm:cloud>
</mm:content>
