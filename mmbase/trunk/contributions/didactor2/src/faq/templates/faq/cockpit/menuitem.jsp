<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:content postprocessor="none">
    <mm:cloud method="delegate">
      <mm:import id="scope" externid="scope" />
      <mm:compare referid="scope" value="education">
        <mm:listnodescontainer path="faqnodes,educations,people" element="faqnodes" id="q">
          <mm:constraint field="people.number" value="${user}" />
          <mm:constraint field="educations.number" value="${education}" />
          <mm:listnodes max="1">
            <div class="menuSeparator">
              <jsp:text> </jsp:text>
            </div>
            <div class="menuItem">
              <mm:treefile page="/faq/frontoffice/index.jsp" objectlist="$includePath"
                           referids="$referids,_node@node">
                <a title="${faqname}" href="${_}" class="menubar"><mm:field name="name"/></a>
              </mm:treefile>
            </div>
          </mm:listnodes>
        </mm:listnodescontainer>
      </mm:compare>
    </mm:cloud>
  </mm:content>
</jsp:root>
