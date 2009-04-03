<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <mm:content
      type="application/xml" postprocessor="none">
    <mm:cloud method="asis">
      <div
          class="content">
        <mm:import externid="object" required="true"/>
        <mm:node referid="object">
          <h1><mm:field name="name" /></h1>

          <mm:field name="body" escape="tagstripper(xss)" />
        </mm:node>
      </div>
    </mm:cloud>
  </mm:content>
</jsp:root>
