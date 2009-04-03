<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:cloud rank="basic user">
    <div>
      <jsp:directive.include file="../mode.include.jsp" />
      <mm:import externid="e">${education}</mm:import>

      <mm:link page="/core/pagereports/">
        <a href="${_}"
           target="text">
          Reports
        </a>
      </mm:link>
    </div>
  </mm:cloud>
</jsp:root>
