<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <di:html
      styleClass="login"
      css="/css/loginpage.css"
      type="text/html"
      rank="anonymous">
    <!--
        This pages contains odd logic, which dpending on certain installed components makes itself render differently.
    -->

    <div class="educationMenubarCockpit" style="white-space: nowrap">
      <!-- sigh -->
      <mm:hasnode number="component.faq">
        <mm:include page="/faq/cockpit/general.jsp" />
      </mm:hasnode>

      <mm:hasnode number="component.cmshelp">
        <di:include page="/cmshelp/cockpit/general.jsp" />
      </mm:hasnode>

    </div>

    <div class="columns">
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
      <div class="columnMiddle">
        <di:include page="/firstcontent.jsp" />
      </div>
      <di:include page="/rightcolumn.jsp" />
    </div>
  </di:html>
</jsp:root>
