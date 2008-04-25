<%@page session="false" %><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="audio/x-pn-realaudio" encoding="">
<mm:import externid="fragment" required="true"  jspvar="fragment" />
<mm:import externid="source" required="true"    jspvar="source" />
<%@ include file="readconfig.jsp"  %>
<%="http://" + getHost() + getTemplatesDir() + "smil.jsp?fragment=" + fragment + "&source=" + source%>
</mm:content>