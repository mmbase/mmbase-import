<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <di:html
      styleClass="portalpages"
      type="application/xhtml+xml"
      expires="0"
      component="portalpages">


    <div class="rows" id="rows">
      <c:choose>
        <c:when test="${empty education}">
          NO EDUCATION YET
        </c:when>
        <c:otherwise>
          <di:script  src="/content/js/open.jsp" />
          <di:include debug="html" page="/portalpages/navigation.jspx" />
          <di:include debug="html" page="/portalpages/main.jspx" />
        </c:otherwise>
      </c:choose>
    </div>

  </di:html>
</jsp:root>
