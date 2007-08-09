<%@page import="nl.leocms.editwizard.*"
%><%@ include file="settings.jsp"
%><mm:locale language="<%=ewconfig.language%>"
><mm:cloud method="http" rank="basic user" jspvar="cloud"
><%@ page errorPage="exception.jsp"
%><mm:log jspvar="log">
<%
   WizardController c = new WizardController();
   c.perform(response, request, ewconfig, configurator, popup, popupId, closedObject, cloud);

/*
String editModeReq = request.getParameter("editmode");
if (editModeReq != null) request.getSession().setAttribute("editmode", editModeReq);   

Object editModeSes = request.getSession().getAttribute("editmode");
System.out.println("Mode is: " + editModeSes);

if (editModeSes == null) {
   WizardController c = new WizardController();
   c.perform(response, request, ewconfig, configurator, popup, log, popupId, closedObject, cloud);
}
else {
   String editMode = (String) editModeSes;
   if (editMode.equals("oneclickedit")) {
      OneClickEditWizardController c = new OneClickEditWizardController();
      c.perform(response, request, ewconfig, configurator, popup, log, popupId, closedObject, cloud);
   }
}
*/
%></mm:log></mm:cloud></mm:locale>

