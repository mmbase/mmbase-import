<%--
  Show all components that are related to the current provider. 
  There are several 'default' components that will be shown in the
  standard layout: as hyperlinks from left to right. All other
  components that are directly related to the provider object will
  be placed in a dropdown box.

--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="nl.didactor.component.Component, java.util.TreeMap, java.util.Iterator"%>

<mm:cloud jspvar="cloud" method="delegate" authenticate="asis">
<%@include file="/shared/setImports.jsp" %>
<script language="JavaScript1.1" type="text/javascript">

function getUrl(url){
  var i = new Image();
  i.src = url;
  i = null;
}

function keepalive(){
  getUrl("<mm:treefile page="/shared/onlineReporter.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>");
  setTimeout("keepalive();",1000 * 60 * 2); // keep alive every 2 minutes
}

keepalive();
</script>
 
<%
  TreeMap tm = new TreeMap();
%>

<div class="providerMenubar" style="white-space: nowrap">
<mm:isgreaterthan referid="user" value="0">
  <mm:node number="$provider">
    <mm:relatedcontainer path="settingrel,components">
      <mm:related>
        <mm:node element="components">
          <mm:field name="name" jspvar="cname" write="false" vartype="String">
            <% 
              Component c = Component.getComponent(cname);
              if ("provider".equals(c.getTemplateBar())) {
                  int a = c.getBarPosition() * 100;
                  while (tm.containsKey(new Integer(a))) {
                      a++; // make sure we have unique positions
                  }
                  tm.put(new Integer(a), c);
              }
            %>
          </mm:field>
        </mm:node>
      </mm:related>
    </mm:relatedcontainer>
  </mm:node>

  <%
    Iterator i = tm.values().iterator();
    while (i.hasNext()) {
        Component c = (Component)i.next();
  %>
    <mm:import id="componentname" reset="true"><%=c.getName()%></mm:import>
    <mm:treeinclude page="/$componentname/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="name"><%=c.getName()%></mm:param>
      <mm:param name="number"><%=c.getNumber()%></mm:param>
      <mm:param name="type">div</mm:param>
      <mm:param name="scope">provider</mm:param>
    </mm:treeinclude>
  	
  <% } %>
  
  <%-- If the user has the rights, then always show the management link. That allows us to enable/disable components after
       install on an empty database --%>
  <mm:treeinclude page="/education/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="name">education</mm:param>
    <mm:param name="type">div</mm:param>
    <mm:param name="scope">provider</mm:param>
  </mm:treeinclude>
</mm:isgreaterthan>
</div>
</mm:cloud>
