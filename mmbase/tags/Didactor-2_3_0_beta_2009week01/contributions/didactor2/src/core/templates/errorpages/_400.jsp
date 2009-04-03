<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:scriptlet>response.setStatus(400)</jsp:scriptlet>
  <mm:cloud>
    <di:include page="/errorpages/400.jsp" />
  </mm:cloud>
</jsp:root>
