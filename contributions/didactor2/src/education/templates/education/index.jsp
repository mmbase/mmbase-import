<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <di:html
      expires="0" component="education" type="text/html">

    <!-- wtf -->
    <mm:hasnode number="component.drm">
      <di:include page="/drm/testlicense.jsp" />
    </mm:hasnode>

    <di:include page="/education/bookmark.jspx" />

    <div class="rows" id="rows">

      <di:include debug="html" page="/education/navigation.jspx" />
      <di:include debug="html" page="/education/main.jspx" />
    </div>

  </di:html>
</jsp:root>
