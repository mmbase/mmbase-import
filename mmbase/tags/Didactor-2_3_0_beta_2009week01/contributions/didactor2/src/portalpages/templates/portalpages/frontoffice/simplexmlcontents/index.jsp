<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <jsp:output omit-xml-declaration="yes" />
  <mm:content
      type="application/xml" postprocessor="none">
    <mm:node number="${param.object}">

      <di:background>

        <di:title field="title" />
        <di:rich field="body" />
      </di:background>

    </mm:node>
  </mm:content>
</jsp:root>
