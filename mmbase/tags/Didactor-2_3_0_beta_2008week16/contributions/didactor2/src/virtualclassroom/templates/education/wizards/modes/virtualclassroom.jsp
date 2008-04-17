<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud method="delegate" authenticate="asis" jspvar="cloud">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="../mode.include.jsp" />
  <mm:hasnode number="component.virtualclassroom">
    <di:has role="virtualclassroom">
      <a href='javascript:clickNode("virtualclassroom_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_virtualclassroom_0'/></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/><span style='width:100px; white-space: nowrap'><a href='<mm:write referid="listjsp"/>&wizard=config/virtualclassroom/virtualclassroomsessions&nodepath=virtualclassroomsessions&fields=name&searchfields=name&orderby=name<mm:write referid="forbidtemplate" escape="text/plain" />' target="text"><di:translate key="virtualclassroom.virtualclassroomsession" /></a></span>
      <br />
      <div id='virtualclassroom_0' style='display: none'>
        <mm:treeinclude page="/virtualclassroom/backoffice/index.jsp" objectlist="$includePath" referids="$referids,wizardjsp">
          <mm:param name="wizardjsp"><mm:write referid="wizardjsp"/></mm:param>
        </mm:treeinclude>
      </div>
    </di:has>
  </mm:hasnode>
</mm:cloud>
