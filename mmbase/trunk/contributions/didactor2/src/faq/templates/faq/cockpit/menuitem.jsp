<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import id="scope" externid="scope"/>

<mm:compare referid="scope" value="education">
  <mm:listnodes type="faqcontainers">
    <mm:first>
      <mm:field id="faqcontainer" name="number" write="false" />
    </mm:first>
  </mm:listnodes>
  <mm:node number="$faqcontainer" notfound="skipbody">
    <mm:relatednodes type="faqnodes" id="faq">
      <mm:import id="faqname" reset="true"><mm:field name="name"/></mm:import>
      <mm:related path="educations,people" constraints="people.number='$user'">
          <div class="menuSeperator"></div>
          <div class="menuItem">
            <mm:treefile page="/faq/frontoffice/index.jsp" objectlist="$includePath" referids="$referids,faq@node">
              aaaaaaaaaa
              <a title="<mm:write referid="faqname"/>" href="${_}" class="menubar"><mm:write referid="faqname"/></a>
            </mm:treefile>
          </div>
      </mm:related>
    </mm:relatednodes>
  </mm:node>
</mm:compare>
</mm:cloud>
</mm:content>
