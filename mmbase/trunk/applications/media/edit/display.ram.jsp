<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><% response.setHeader("Content-Type", "audio/x-pn-realaudio");
%><mm:import id="source">504</mm:import><mm:import externid="fragment" required="true"
/>http://<%=request.getServerName()%><mm:url escapeamps="false" referids="source,fragment" page="/mediaedit/display.smil.jsp" />