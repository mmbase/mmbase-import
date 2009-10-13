<%@page language="java" contentType="text/html"
 import="org.mmbase.applications.xmlimporter.Consultant, java.lang.Thread,
 org.mmbase.util.logging.Logger, org.mmbase.util.logging.Logging" %>

<jsp:useBean id="consultant" scope="session"
 class="org.mmbase.applications.xmlimporter.Consultant" />

<%!  Logger log = Logging.getLoggerInstance(
     "org.mmbase.applications.xmlimporter.jsp.continueimport.jsp"); %>

<% try {
      int nr = Integer.parseInt(request.getParameter("candidateNr"));
      consultant.setChoice(nr); %>
      <jsp:forward page="./runningimport.jsp"/>
<% } catch (Exception e) {
      log.error("UNKNOWN ERROR");
      log.error("message: " + e.toString());
   }%>