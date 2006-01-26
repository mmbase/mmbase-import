<%--
  Show all components that are related to the current provider. 
  There are several 'default' components that will be shown in the
  standard layout: as hyperlinks from left to right. All other
  components that are directly related to the provider object will
  be placed in a dropdown box.

--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>
<script language="JavaScript1.1" type="text/javascript">

function getUrl(url){
  var i = new Image();
  i.src = url;
  i = null;
}

function keepalive(){
  getUrl("<mm:treefile page="/shared/onlineReporter.jsp" objectlist="$includePath" referids="$referids" />");
  setTimeout("keepalive();",1000 * 60 * 2); // keep alive every 2 minutes
}

keepalive();
</script>
<%--
<div style="display:none;">
    <mm:treeinclude write="true" page="/shared/onlineReporter.jsp" objectlist="$includePath" referids="$referids" />
</div>
--%>
 

<div class="providerMenubar" style="white-space: nowrap">
<mm:isgreaterthan referid="user" value="0">
  <mm:import id="providerbaritems" vartype="list">search,pop,address,agenda,portfolio,email,workspace,cms</mm:import>
  
  <%-- first show all the items in a predefined order --%>
  <mm:stringlist referid="providerbaritems">
    <mm:import id="pname"><mm:write /></mm:import>
    <mm:node number="$provider">
      <mm:relatedcontainer path="settingrel,components">
        <mm:constraint field="components.name" referid="pname" />
        <mm:related>
          <mm:treeinclude page="/$pname/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
            <mm:param name="name"><mm:write referid="pname" /></mm:param>
            <mm:param name="number"><mm:field name="number" /></mm:param>
            <mm:param name="type">div</mm:param>
            <mm:param name="scope">provider</mm:param>
          </mm:treeinclude>
        </mm:related>
      </mm:relatedcontainer>
    </mm:node>
    <mm:remove referid="pname" />
  </mm:stringlist>
  
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
