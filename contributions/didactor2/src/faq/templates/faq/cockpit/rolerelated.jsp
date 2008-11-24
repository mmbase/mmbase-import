<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:content postprocessor="none">
    <mm:cloud method="delegate">

      <mm:listnodescontainer path="educations,faqnodes"
                             element="faqnodes" id="q">
        <mm:constraint field="educations.number" value="${education}" />
        <mm:listnodes id="faq">
          <mm:field id="faqname" name="name" write="false"/>
          <mm:relatednodes type="roles">
            <mm:field id="role" name="name" write="false" />
            <mm:notpresent referid="displayed">
              <mm:node number="$user">
                <di:hasrole role="${role}">
                  <div class="menuSeparatorApplicationMenubar">
                    <jsp:text> </jsp:text>
                  </div>
                  <div class="menuItemApplicationMenubar">
                    <mm:treefile page="/faq/frontoffice/index.jspx" objectlist="$includePath" referids="$referids,faq@node" write="false">
                      <a title="${faqname}" href="${_}"  class="menubar"><mm:write referid="faqname"/></a>
                    </mm:treefile>
                  </div>
                  <mm:import id="displayed" />
                </di:hasrole>
              </mm:node>
            </mm:notpresent>
          </mm:relatednodes>
        </mm:listnodes>
      </mm:listnodescontainer>
    </mm:cloud>
  </mm:content>
</jsp:root>
