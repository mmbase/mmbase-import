<%@ include file="jspbase.jsp" %>
<mm:import id="forumid" externid="forumid" jspvar="forumid" />
<mm:import externid="account" from="parameters" />
<mm:import externid="password" from="parameters" />
<mm:write referid="account" session="caf$forumid" />
<mm:write referid="password" session="cwf$forumid" />
<%response.sendRedirect("index.jsp?forumid="+forumid);%>
