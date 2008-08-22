<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <div class="columnRight">
    <div class="titlefield2">
      <di:translate key="core.news" />
    </div>
    <mm:hasnode number="component.portalpages" inverse="true">
      <div class="ListRight">
        <mm:node number="component.news" notfound="skipbody">
          <di:include page="/news/frontoffice/index.jsp" />
        </mm:node>
      </div>
    </mm:hasnode>
    <mm:hasnode number="component.portalpages">
      <div class="ListRightHalf">
        <mm:node number="component.news" notfound="skipbody">
          <di:include page="/news/frontoffice/index.jsp" />
        </mm:node>
      </div>
      <div class="ListRight">
        <mm:include page="loginbox.jsp"/>
      </div>
    </mm:hasnode>
  </div>
</jsp:root>
