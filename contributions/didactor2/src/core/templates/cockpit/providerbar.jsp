<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <!--
      Show all components that are related to the current provider.
      There are several 'default' components that will be shown in the
      standard layout: as hyperlinks from left to right. All other
      components that are directly related to the provider object will
      be placed in a dropdown box.

  -->
  <jsp:directive.page import="nl.didactor.component.Component,java.util.*" />
  <mm:cloud method="asis">
    <div class="providerMenubar">
    <script  type="text/javascript">

      function getUrl(url){
      var i = new Image();
      i.src = url;
      i = null;
      }

      function keepalive(){
      getUrl("<mm:treefile page="/shared/onlineReporter.jsp" objectlist="$includePath"
      escape="js-double-quotes" referids="$referids"
      escapeamps="${empty param.escapeamps ?  false : param.escapeamps}" />");
      setTimeout("keepalive();",1000 * 60 * 2); // keep alive every 2 minutes
      }

      keepalive();
    </script>
      <mm:hasrank minvalue="didactor user">
        <mm:node referid="provider">
          <mm:functioncontainer>
            <mm:param name="bar">provider</mm:param>
            <mm:listfunction name="components" id="c">
              <mm:treeinclude page="/${c.name}/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
                <mm:param name="name">${c.name}</mm:param>
                <mm:param name="number">${c.number}</mm:param>
                <mm:param name="type">div</mm:param>
                <mm:param name="scope">provider</mm:param>
              </mm:treeinclude>
            </mm:listfunction>
          </mm:functioncontainer>
        </mm:node>

        <!-- If the user has the rights, then always show the management link. That allows us to enable/disable components after install on an empty database -->
        <mm:treeinclude page="/education/cockpit/menuitem.jsp"
                        objectlist="$includePath" referids="$referids">
          <mm:param name="name">education</mm:param>
          <mm:param name="type">div</mm:param>
          <mm:param name="scope">provider</mm:param>
        </mm:treeinclude>
      </mm:hasrank>

      <mm:hasrank value="anonymous">
        <div class="provideranonymous">
          <mm:node referid="provider">
            <mm:nodeinfo type="gui" />
          </mm:node>
        </div>
      </mm:hasrank>
    </div>
  </mm:cloud>
</jsp:root>
