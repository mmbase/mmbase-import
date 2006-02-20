<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:cloud jspvar="cloud" method="delegate" authenticate="asis">
<%@include file="/shared/setImports.jsp" %>
<div class="educationMenubarCockpit" style="white-space: nowrap">
<mm:isgreaterthan referid="user" value="0">
  <mm:present referid="education">
    <mm:node number="$education" notfound="skip">
      <mm:related path="settingrel,components">
        <mm:node element="components">
          <mm:field id="name" name="name" write="false" />
          <mm:treeinclude page="/$name/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
            <mm:param name="name"><mm:field name="name" /></mm:param>
            <mm:param name="number"><mm:field name="number" /></mm:param>
            <mm:param name="type">div</mm:param>
            <mm:param name="scope">education</mm:param>
          </mm:treeinclude>
          <mm:remove referid="name" />
        </mm:node>
      </mm:related>
    </mm:node>
  </mm:present>
  <%-- MMbob section - link to class forum --%>
  <mm:notpresent referid="education">
    <mm:node number="$provider">
      <mm:relatedcontainer path="settingrel,components">
        <mm:constraint field="components.name" value="mmbob"/>
        <mm:related>
          <mm:node element="components">
            <mm:field id="name" name="name" write="false" />
            <mm:treeinclude page="/$name/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
              <mm:param name="name"><mm:field name="name" /></mm:param>
              <mm:param name="number"><mm:field name="number" /></mm:param>
              <mm:param name="type">div</mm:param>
              <mm:param name="scope">education</mm:param>
            </mm:treeinclude>
            <mm:remove referid="name" />
          </mm:node>
        </mm:related>
      </mm:relatedcontainer>
    </mm:node>
  </mm:notpresent>
</div>
</mm:isgreaterthan>
</mm:cloud>
