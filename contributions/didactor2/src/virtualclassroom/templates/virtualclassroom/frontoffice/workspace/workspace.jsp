<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
  <mm:import externid="destination"/>
  <mm:treeinclude page="$destination" objectlist="$includePath" referids="$referids">
    <mm:param name="destination"><mm:write referid="destination"/></mm:param>
  </mm:treeinclude> 			           
</mm:cloud>
</mm:content>			           