<%@page language="java" contentType="text/html;charset=UTF-8" errorPage="error.jsp"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.applications.editwizard.action.*"
%><mm:import externid="loginsessionname" from="parameters" ></mm:import><%
%><mm:import externid="loginmethod" from="parameters">loginpage</mm:import><%
/**
 * settings.jsp
 *
 * @since    MMBase-1.6
 * @version  $Id: settings.jsp,v 1.1 2005-11-28 10:09:28 nklasens Exp $
 * @author   Kars Veling
 * @author   Pierre van Rooden
 * @author   Michiel Meeuwissen
 * @author   Vincent van der Locht
 */
Controller controller = Controller.getInstance(pageContext);
if (controller.init()) {
	return;
}
%><mm:content type="text/html" expires="0"><%
%><mm:cloud method="$loginmethod"  loginpage="login.jsp" jspvar="cloud" sessionname="$loginsessionname"
><mm:log jspvar="log"><%
String forwardUri = controller.processRequest(cloud);
%><jsp:forward page="<%=forwardUri%>"/></mm:log></mm:cloud></mm:content>
