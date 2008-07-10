<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:directive.page isErrorPage="true" contentType="text/html" />
  <jsp:scriptlet>response.setStatus(500);</jsp:scriptlet>
  <di:include page="/errorpages/500.jsp" />
</jsp:root>
