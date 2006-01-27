<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>
<div class="applicationMenubarCockpit" style="white-space: nowrap">
  <mm:node number="$user" notfound="skipbody">
    <div class="menuItemApplicationMenubar">
      <a title="<di:translate key="core.logout" />" href="<mm:treefile page="/logout.jsp" objectlist="$includePath" referids="$referids"/>" class="menubar"><di:translate key="core.logout" /> <mm:field name="firstname"/> <mm:field name="suffix"/> <mm:field name="lastname"/></a>
    </div>

    <div class="menuSeperatorApplicationMenubar"></div>

    <div class="menuItemApplicationMenubar">
      <a title="<di:translate key="core.configuration" />" href="<mm:treefile page="/admin/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="core.configuration" /></a>
    </div>

    <div class="menuSeperatorApplicationMenubar"></div>
    <div class="menuItemApplicationMenubar">
      <a title="<di:translate key="core.print" />" href="javascript:printThis();"  class="menubar"><di:translate key="core.print" /></a>
    </div>
    
	  <mm:node number="component.cmshelp" notfound="skipbody">
        <mm:treeinclude page="/cmshelp/cockpit/rolerelated.jsp" objectlist="$includePath" referids="$referids" />
	  </mm:node> 	    
	  
	  <mm:node number="component.faq" notfound="skipbody">
        <mm:treeinclude page="/faq/cockpit/rolerelated.jsp" objectlist="$includePath" referids="$referids" />
	  </mm:node> 	  
        
 <script language="JavaScript">
<!--
function printThis() {
    if (frames && frames['content']) {
        if (frames['content'].focus)
            frames['content'].focus();
        if (frames['content'].print)
            frames['content'].print();
    }
    else if (window.print) {
        window.print();
    }
}
//-->
</script>

</mm:node>
<!-- please do not remove &nbsp; otherwise there will be overlapping of divs if there is no cloud access -->

&nbsp;
  </div>
</mm:cloud>
