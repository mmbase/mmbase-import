<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="true" />
  <!--
      This pages contains odd logic, which depending on certain installed components makes itself render differently
  -->
  <div class="columnLeft">
    <img src="${mm:treelink('/gfx/logo_didactor.gif',  includePath)}"
         width="100%" height="106" border="0" title="Didactor logo" alt="Didactor logo" />

    <mm:hasnode number="component.portalpages">
      <di:include page="/portalpages/frontoffice/index.jsp" />
    </mm:hasnode>
    <mm:hasnode number="component.portalpages" inverse="true">
      <!--  show login box on the left -->
      <div class="ListLeft">
        <mm:include page="loginbox.jsp" />
      </div>
    </mm:hasnode>
  </div>
</jsp:root>
