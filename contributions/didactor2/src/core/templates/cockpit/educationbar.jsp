<jsp:root
    version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:directive.page session="false" />
  <jsp:directive.page import="java.util.*,nl.didactor.component.*" />
  <mm:cloud method="asis">
    <div class="educationMenubar">
      <mm:isgreaterthan referid="user" value="0">

        <mm:present referid="education">
          <mm:hasnode number="component.progress">
            <mm:treefile page="/progress/cockpit/bar_connector.jspx" objectlist="$includePath" referids="$referids" write="false" escapeamps="true">
              <script type="text/javascript"> function reloadProgress() { $("#progressMeter").load('${_}'); } </script>
            </mm:treefile>
            <div id="progressMeter">
              <mm:treeinclude page="/progress/cockpit/bar_connector.jspx" objectlist="$includePath" referids="$referids" />
            </div>

          </mm:hasnode>
        </mm:present>
        <jsp:scriptlet>
          // Too much java code in this JSP
          //  This stuff must be gone.
          TreeMap tm = new TreeMap();
        </jsp:scriptlet>

        <div class="educationMenubarNav">

          <mm:present referid="education">
            <mm:node number="$education">
              <mm:related path="settingrel,components">
                <mm:node element="components">
                  <mm:field jspvar="cname" name="name" write="false" vartype="String">
                    <jsp:scriptlet>
                      Component c = Component.getComponent(cname);
                      if ("education".equals(c.getTemplateBar())) {
                      int a = c.getBarPosition() * 100;
                      while (tm.containsKey(new Integer(a))) {
                      a++; // make sure we have unique positions
                      }
                      tm.put(new Integer(a), c);
                      }
                    </jsp:scriptlet>
                  </mm:field>
                </mm:node>
              </mm:related>
            </mm:node>
            <jsp:scriptlet>
              Iterator i = tm.values().iterator();
              while (i.hasNext()) {
              Component c = (Component)i.next();
            </jsp:scriptlet>
            <mm:import id="componentname" reset="true">
              <jsp:expression>c.getName()</jsp:expression>
            </mm:import>

            <c:catch var="ex">
              <mm:treeinclude  page="/$componentname/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
                <mm:param name="name"><jsp:expression>c.getName()</jsp:expression></mm:param>
                <mm:param name="number"><jsp:expression>c.getNumber()</jsp:expression></mm:param>
                <mm:param name="type">div</mm:param>
                <mm:param name="scope">education</mm:param>
              </mm:treeinclude>
            </c:catch>
            ${ex.message}
            <jsp:scriptlet>}</jsp:scriptlet>
          </mm:present>
        </div>
      </mm:isgreaterthan>
    </div>
  </mm:cloud>
</jsp:root>
