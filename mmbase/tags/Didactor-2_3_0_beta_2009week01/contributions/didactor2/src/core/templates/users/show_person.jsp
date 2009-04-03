<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:context>
    <mm:import id="body">
      <!-- Online/offline status is retrieved using the nl.didactor.builders.PeopleBuilder class  -->
      <mm:field name="isonline" id="isonline" write="false" />
      <mm:compare referid="isonline" value="0">
        <img src="${mm:treelink('/gfx/icon_offline.gif', includePath)}"
             width="6" height="12" border="0" title="offline" alt="offline" />
      </mm:compare>
      <mm:compare referid="isonline" value="1">
        <img src="${mm:treelink('/gfx/icon_online.gif', includePath
                  )}" width="6" height="12" border="0" title="online" alt="online" />
      </mm:compare>
      <mm:remove referid="isonline" />
      <di:person />
    </mm:import>
    <mm:node number="component.portfolio" notfound="skipbody">
      <mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="contact"><mm:field name="number"/></mm:param>
        <a href="${_}" class="users">
          <mm:write referid="body" escape="none" id="found"/>
        </a>
      </mm:treefile>
    </mm:node>
    <mm:notpresent referid="found">
      <mm:write referid="body" escape="none" />
    </mm:notpresent>
    <mm:remove referid="body" />
  </mm:context>
</jsp:root>

