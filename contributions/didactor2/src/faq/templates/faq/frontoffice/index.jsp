<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:import externid="node"/>
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids"/>
  <div class="rows">
    <div class="navigationbar">
	  <div class="titlebar">
      </div>
	</div>
	<div class="folders">
	</div>
	<div class="contentHeader">
	</div>
	<div class="contentBodywit">
	  <mm:treeinclude page="/faq/frontoffice/show_faq.jsp" objectlist="$includePath" referids="$referids">
	    <mm:param name="node"><mm:write referid="node"/></mm:param>
	  </mm:treeinclude> 
	</div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath"  referids="$referids"/>
</mm:cloud>
</mm:content>
