<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <di:html
      styleClass="login"
      css="/css/loginpage.css"
      type="text/html"
      rank="anonymous">

    <script language="javascript">
      addEventHandler(window, "onload", function() {
        var form = document.forms[0];
        for (var i=0; i &lt; form.elements.length; i++) {
          var elem = form.elements[i];
          // find first editable field
          var hidden = elem.getAttribute("type"); //.toLowerCase();
          if (hidden != "hidden") {
            elem.focus();
            break;
          }
        }
	   });
    </script>
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
        <!-- iframes are evil -->
        <mm:treefile page="/firstcontent.jsp"
                     objectlist="$includePath" referids="$referids" write="false">
          <iframe width="100%" height="100%" src="${_}" name="content" frameborder="0">
            <jsp:text> </jsp:text>
          </iframe>
        </mm:treefile>
      </div>
      <di:include page="/rightcolumn.jsp" />
    </div>
  </di:html>
</jsp:root>
