<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:cloud method="asis">
    <mm:node referid="provider">
      <mm:countrelations type="flashpages" write="false">

        <mm:islessthan value="1">
          <h1><di:translate key="core.welcome" /></h1>
          <h3><di:translate key="core.welcomemessage" /></h3>
        </mm:islessthan>

      </mm:countrelations>

      <di:include page="/shared/related.flashpages.jsp" />

    </mm:node>
  </mm:cloud>
</jsp:root>

