<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          xmlns:di-t="urn:jsptagdir:/WEB-INF/tags/di/core"
          >
  <di:html
      styleClass="education"
      type="text/html"
      title_key="education.learnenvironmenttitle"
      expires="0"
      component="education">
    <!--
         serving out as tex/html because of:
         https://bugzilla.mozilla.org/show_bug.cgi?id=456008
         Flash will not be visible then in FF 3.0.2. Bug is fixed. In a few months it can be removed again.

    -->

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
          <di:script  src="/content/js/open.jsp" />
          <di:include debug="html" page="/education/navigation.jspx" />
          <di:include debug="html" page="/education/main.jspx" />
        </c:otherwise>
      </c:choose>
    </div>

  </di:html>
</jsp:root>
