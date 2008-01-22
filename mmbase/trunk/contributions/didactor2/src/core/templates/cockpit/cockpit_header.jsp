<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:import externid="extraheader" />
  <mm:import externid="extrabody" />
  <mm:cloud method="asis">
    <![CDATA[<html xmlns="http://www.w3.org/1999/xhtml">]]><!-- UGLY: opened, but not closed in this file -->
    <di:head>
      <mm:write referid="extraheader" escape="none" />
    </di:head>
    <![CDATA[<body class="componentbody"]]><!-- UGLY: opened, but not closed in this file -->
    <mm:write referid="extrabody" escape="none" />
    <![CDATA[>]]>
    <div class="">
      <di:include page="/cockpit/applicationbar.jsp" />
      <di:include page="/cockpit/providerbar.jsp"  />
      <di:include page="/cockpit/educationbar.jsp" />
    </div>
  </mm:cloud>
</jsp:root>
