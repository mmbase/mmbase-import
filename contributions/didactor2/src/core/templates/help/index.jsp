<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="HELP" /></title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_help.gif" objectlist="$includePath" />" width="25" height="13" border="0" alt="<fmt:message key="HELP" />" />
    <fmt:message key="HELP" />
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
  <mm:treeinclude page="/help/didactor_help.htm" objectlist="$includePath" referids="$referids"/>


  </div>

</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath"  referids="$referids"/>
</mm:cloud>
</mm:content>