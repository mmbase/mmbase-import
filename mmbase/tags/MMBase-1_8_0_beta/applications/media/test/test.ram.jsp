<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><% response.setHeader("Content-Type", "audio/x-pn-realaudio");
%><mm:import externid="source" required="true" 
/><mm:import externid="fragment" 
/>http://<%=request.getServerName()%><mm:url escapeamps="false" referids="source,fragment" page="/test/mediatest/test.smil.jsp" />