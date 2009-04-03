<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="true" />
  <div class="columnMiddle">
    <div id="contentFrame">
      <mm:import externid="object" />
      <mm:notpresent referid="object">
        <di:include page="/login/firstcontent.jsp" />
      </mm:notpresent>
      <mm:present referid="object">
        <mm:include page="/content/" />
      </mm:present>
    </div>
  </div>
</jsp:root>
