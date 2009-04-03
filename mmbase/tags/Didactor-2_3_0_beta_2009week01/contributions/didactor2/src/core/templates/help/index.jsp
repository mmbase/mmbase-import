<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="core.help" /></title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_help.gif" objectlist="$includePath" />" width="25" height="13" border="0" title="<di:translate key="core.help" />" alt="<di:translate key="core.help" />" />
    <di:translate key="core.help" />
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
    <iframe src="<mm:url page="/help/helptop.jsp"/>" height="50" width="100%"></iframe>
    <iframe name="helpcontent" height="100%" width="100%"></iframe>
  </div>

</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath"  referids="$referids"/>
</mm:cloud>
</mm:content>
