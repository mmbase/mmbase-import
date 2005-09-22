<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="EDITWIZARDS" /></title>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/calendar.css" objectlist="$includePath" referids="$referids"/>" />
    <style type="text/css">
      a {
        font-size: 11px;
      }
    </style>
  </mm:param>
</mm:treeinclude>
<div class="rows">
  <div class="navigationbar">
    <div class="titlebar">
      <img src="<mm:treefile page="/gfx/icon_agenda.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="EDITWIZARDS" />" />
      Editwizards:  <mm:treeinclude page="/education/wizards/tree_top_menu.jsp" objectlist="$includePath" />
    </div>
  </div>
  <div class="folders">
    <mm:treeinclude page="/education/wizards/code.jsp" objectlist="$includePath" />
  </div>
  <div class="mainContent">
    <iframe id="text" name="text" width="100%" height="90%" marginwidth="0" marginheight="0" border="1"></iframe>
  </div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids"/>
</mm:cloud>
</mm:content>
