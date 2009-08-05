<%@page language="java" contentType="text/html;charset=UTF-8" errorPage="error.jsp"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.applications.editwizard.Controller"
%><mm:import externid="loginsessionname" from="parameters" ></mm:import><%
%><mm:import externid="loginmethod" from="parameters">loginpage</mm:import><%
/**
 * settings.jsp
 *
 * @since    MMBase-1.6
 * @version  $Id: settings.jsp,v 1.3 2006-02-02 12:18:33 pierre Exp $
 * @author   Finalist
 * @author   Kars Veling
 * @author   Pierre van Rooden
 * @author   Michiel Meeuwissen
 * @author   Vincent van der Locht
 */
%><mm:content type="text/html" expires="0"><%
%><mm:cloud method="$loginmethod"  loginpage="login.jsp" jspvar="cloud" sessionname="$loginsessionname"
><mm:log jspvar="log"><%
Controller controller = new Controller(pageContext, cloud);
String forwardUri = controller.processRequest();
%><jsp:forward page="<%=forwardUri%>"/></mm:log></mm:cloud></mm:content>
