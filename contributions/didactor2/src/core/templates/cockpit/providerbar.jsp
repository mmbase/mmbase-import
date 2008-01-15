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
      <jsp:scriptlet>Map tm = new HashMap();</jsp:scriptlet>
      <mm:hasrank minvalue="didactor user">
        <mm:node referid="provider">
          <mm:relatednodes role="settingrel" type="components">
            <mm:field name="name" jspvar="cname" write="false" vartype="String">
              <jsp:scriptlet>
                Component c = Component.getComponent(cname);
                if (c == null) System.err.println("No component '" + cname + "'");
                if (c != null &amp;&amp; "provider".equals(c.getTemplateBar())) {
                int a = c.getBarPosition() * 100;
                while (tm.containsKey(new Integer(a))) {
                a++; // make sure we have unique positions
                }
                tm.put(new Integer(a), c);
                }
              </jsp:scriptlet>
            </mm:field>
          </mm:relatednodes>
        </mm:node>

        <jsp:scriptlet>
          Iterator i = tm.values().iterator();
          while (i.hasNext()) {
          Component c = (Component)i.next();
        </jsp:scriptlet>
        <mm:import id="componentname" reset="true"><jsp:expression>c.getName()</jsp:expression></mm:import>
        <mm:treeinclude page="/$componentname/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
          <mm:param name="name"><jsp:expression>c.getName()</jsp:expression></mm:param>
          <mm:param name="number"><jsp:expression>c.getNumber()</jsp:expression></mm:param>
          <mm:param name="type">div</mm:param>
          <mm:param name="scope">provider</mm:param>
        </mm:treeinclude>

        <jsp:scriptlet>}</jsp:scriptlet>

        <!-- If the user has the rights, then always show the management link. That allows us to enable/disable components after
             install on an empty database -->
        <mm:treeinclude page="/education/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
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
