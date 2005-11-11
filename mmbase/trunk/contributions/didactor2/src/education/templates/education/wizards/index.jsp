<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="education.editwizards" /></title>
    <style type="text/css">
      a {
        font-size: 11px;
      }
      .folderBodyTree {
         width: 20%;
         overflow: hidden;
      }
    </style>
  </mm:param>
</mm:treeinclude>
<div class="rows">
  <div class="navigationbar">
    <div class="titlebar">
      <img src="<mm:treefile page="/gfx/icon_agenda.gif" objectlist="$includePath" referids="$referids"/>" alt="<di:translate key="education.editwizards" />" />
      Editwizards:  <mm:treeinclude page="/education/wizards/tree_top_menu.jsp" objectlist="$includePath" />
    </div>
  </div>
  <div class="folders">
    <div class="folderBodyTree">
      <mm:treeinclude page="/education/wizards/code.jsp" objectlist="$includePath" />
    </div>
  </div>
  <div class="mainContent">
    <iframe id="text" name="text" width="100%" height="90%" marginwidth="0" marginheight="0" border="1" src="<mm:treefile page="/education/wizards/ok.jsp" objectlist="$includePath" referids="$referids"/>"></iframe>
  </div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids"/>
</mm:cloud>
</mm:content>
