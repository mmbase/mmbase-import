<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:import externid="fragment" required="true"  jspvar="fragment" /><%@ include file="readconfig.jsp" 
%><% response.setHeader("Content-Type", "audio/x-pn-realaudio");
%><%="http://" + getHost() + getTemplatesDir() + "smil.jsp?fragment=" + fragment%>