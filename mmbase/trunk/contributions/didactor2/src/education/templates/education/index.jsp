<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <di:html
      styleClass="education"
      title_key="education.learnenvironmenttitle"
      expires="0"
      component="education">

    <!-- wtf -->
    <mm:hasnode number="component.drm">
      <di:include page="/drm/testlicense.jsp" />
    </mm:hasnode>

    <di:include page="/education/bookmark.jspx" />

    <div class="rows" id="rows">
      <c:choose>
        <c:when test="${empty education}">
          NO EDUCATION YET
        </c:when>
        <c:otherwise>
          <mm:link page="/content/js/open.jsp">
            <script type="text/javascript" src="${_}">
              <jsp:text><!-- help IE --> </jsp:text>
            </script>
          </mm:link>
          <di:include debug="html" page="/education/navigation.jspx" />
          <di:include debug="html" page="/education/main.jspx" />
        </c:otherwise>
      </c:choose>
    </div>

  </di:html>
</jsp:root>
