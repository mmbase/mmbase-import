<%@ include file="server.jsp" 
%><% response.setHeader("Content-Type", "audio/x-pn-realaudio");
%><%=thisServer%>mm/mediaedit/display.smil.jsp?source=<%=request.getParameter("source")%>