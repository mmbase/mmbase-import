<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is 0; show always new content --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="core.configuration" /></title>
  </mm:param>
</mm:treeinclude>
<div class="rows">
<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_settings.gif" objectlist="$includePath" />" width="25" height="13" border="0" alt="<di:translate key="core.configuration" />" />
    <di:translate key="core.configuration" />
  </div>
</div>
<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>
<div class="mainContent">
  <div class="contentHeader">
  </div>
  <div class="contentBodywit">
    <mm:treeinclude page="/admin/body.jsp" objectlist="$includePath" referids="$referids"/>
  </div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
