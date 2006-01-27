<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="nl.didactor.component.Component, java.util.TreeMap, java.util.Iterator"%>

<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>
<div class="educationMenubar" style="white-space: nowrap">
<mm:isgreaterthan referid="user" value="0">

  <mm:present referid="education">
    <mm:node number="component.progress" notfound="skip">
      <mm:treeinclude page="/progress/cockpit/bar_connector.jsp" objectlist="$includePath" referids="$referids"/>
    </mm:node>
  </mm:present>
  <%
    TreeMap tm = new TreeMap();
  %>

  <div class="educationMenubarNav">
  <mm:present referid="education">
    <mm:node number="$education" notfound="skip">
      <mm:related path="settingrel,components">
        <mm:node element="components">
          <mm:field jspvar="cname" name="name" write="false" vartype="String">
            <% 
              Component c = Component.getComponent(cname);
              if ("education".equals(c.getTemplateBar())) {
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
        <mm:param name="scope">education</mm:param>
      </mm:treeinclude>
    <% } %>
  </mm:present>
  </div>
</mm:isgreaterthan>
</mm:cloud>
