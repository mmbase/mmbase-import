<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:import externid="simplecontents"/>
  <mm:import externid="node"/>
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <link rel="stylesheet" type="text/css" href="<mm:treefile page="/cmshelp/css/cmshelp.css" objectlist="$includePath" referids="$referids" />" />
    </mm:param>
  </mm:treeinclude>
  <div class="rows">
    <div class="navigationbar">
      <div class="titlebar">
      </div>
    </div>
    <div class="folders">
    </div>
    <div class="contentHeader">
    </div>
    <div class="contentBodywit" style="">
      <div class="learnenvironment">
    	  <mm:treeinclude page="/cmshelp/frontoffice/show_help.jsp" objectlist="$includePath" referids="$referids">
    	    <mm:param name="node2"><mm:write referid="simplecontents"/></mm:param>
          <mm:param name="node"><mm:write referid="node"/></mm:param>
    	  </mm:treeinclude>
      </div> 	  
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids"/>
</mm:cloud>
</mm:content>
