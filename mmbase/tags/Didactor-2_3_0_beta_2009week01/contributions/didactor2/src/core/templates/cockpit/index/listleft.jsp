<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:node number="$user" notfound="skip">
    <mm:nodelistfunction name="educations">
      <mm:time id="now" write="false" time="now" precision="10 minute" />
      <mm:relatednodescontainer path="classrel,classes,mmevents" element="classes">
        <mm:constraint field="mmevents.start" operator="lt" value="${now}" />
        <mm:constraint field="mmevents.stop"  operator="gt" value="${now}" />
        <mm:relatednodes id="activeClasses" />
      </mm:relatednodescontainer>
      <mm:relatednodescontainer path="classrel,classes,classrel,people" element="classes">
        <mm:constraint field="number" referid="activeClasses" />
        <mm:constraint field="people.number"  referid="user" />
        <mm:relatednodes id="activeClassesForUser" />
      </mm:relatednodescontainer>
    </mm:nodelistfunction>
  </mm:node>
  <mm:present referid="activeClassesForUser">
    <mm:listnodes referid="activeClassesForUser">
      <nobr>
        <img src="${mm:treefile('/gfx/icon_course_notdone.gif', pageContext, includePath)}"
             width="13" height="11" border="0" title="" alt="" />
        <mm:treefile page="/education/index.jsp" objectlist="$includePath"
                     referids="$referids,_node@class">
          <mm:param name="education"><mm:relatednodes type="educations" role="classrel" max="1"><mm:field name="number" /></mm:relatednodes></mm:param>
          <a href="${_}"
             class="users">
            <mm:field name="name" escape="substring(0,7,...)" />
          </a>
        </mm:treefile>
      </nobr>
      <br/><!-- wtf -->
    </mm:listnodes>
  </mm:present>
</jsp:root>
