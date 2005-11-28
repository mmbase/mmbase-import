<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:import externid="loginsessionname" from="parameters" ></mm:import><%
%><mm:log jspvar="log"><%
// what to do if 'logout' is requested?
// return to the deeped backpage and clear the session.
log.debug("logout parameter given, clearing session");
session.removeAttribute(sessionKey);
log.debug("Redirecting to " + refer);
String refer = request.getAttribute("refer");
if (! refer.startsWith("http:")) {
	refer = response.encodeRedirectURL(request.getContextPath() + refer);
}
%><mm:redirect referids="loginsessionname" page="logout.jsp"><%
  %><mm:param name="refer" value="<%=refer%>" /><%
%></mm:redirect><%
%></mm:log>

