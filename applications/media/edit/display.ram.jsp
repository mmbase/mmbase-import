<%-- Used by player --%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><% response.setHeader("Content-Type", "audio/x-pn-realaudio");
%><mm:import externid="source" required="true" />http://<%=request.getServerName()%><mm:url escapeamps="false" referids="source" page="/mediaedit/display.smil.jsp" />