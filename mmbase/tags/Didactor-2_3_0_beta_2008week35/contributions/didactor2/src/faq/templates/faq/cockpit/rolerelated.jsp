<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="delegate">
<%@include file="/shared/setImports.jsp" %>

<mm:listnodes type="faqcontainers">
  <mm:first>
    <mm:field id="faqcontainer" name="number" write="false" />
  </mm:first>
</mm:listnodes>

<mm:node number="$faqcontainer" notfound="skipbody">
  <mm:relatednodes type="faqnodes" id="faq">
    <mm:import id="faqname" reset="true"><mm:field name="name"/></mm:import>
    <mm:remove referid="faqnodeshown" />
    <mm:relatednodes type="roles">
      <mm:import id="role" jspvar="role" reset="true"><mm:field name="name"/></mm:import>
      <mm:node number="$user" notfound="skipbody">
        <di:hasrole role="<%=role%>">
          <mm:notpresent referid="faqnodeshown">
            <div class="menuSeperatorApplicationMenubar"></div>
            <div class="menuItemApplicationMenubar">
              <mm:treefile page="/faq/frontoffice/index.jspx" objectlist="$includePath" referids="$referids,faq@node" write="false">
                <a title="<mm:write referid="faqname"/>" href="${_}"  class="menubar"><mm:write referid="faqname"/></a>
              </mm:treefile>
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
