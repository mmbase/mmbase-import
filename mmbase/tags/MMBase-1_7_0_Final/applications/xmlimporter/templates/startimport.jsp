<%@page language="java" contentType="text/html"
 import="java.io.*, org.mmbase.applications.xmlimporter.Consultant,
 org.mmbase.applications.xmlimporter.TransactionHandler, org.mmbase.module.Module,
 org.mmbase.util.logging.Logger, org.mmbase.util.logging.Logging" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="mmbase_xmlimportdir" jspvar="importDir" from="session" />

<jsp:useBean id="consultant" scope="session"
 class="org.mmbase.applications.xmlimporter.Consultant" />

<%!  Logger log = Logging.getLoggerInstance(
     "org.mmbase.applications.xmlimporter.jsp.startimport.jsp"); %>

<% 
try {
      consultant.init();
      // String importDir = System.getProperty("mmbase.config") + "/import/";
      String importFile = importDir + request.getParameter("choosenFile");
      boolean interactive = new Boolean(request.getParameter("interactive")
      ).booleanValue();
      consultant.setInteractive(interactive);
      java.io.Reader readr = new InputStreamReader(new FileInputStream(importFile),"ISO-8859-1");
      org.mmbase.applications.xmlimporter.TransactionHandler th =
         (org.mmbase.applications.xmlimporter.TransactionHandler)
         org.mmbase.module.Module.getModule("transactionhandler");
      th.handleTransactionAsynchronously(readr, consultant);
      log.info("import started with file: " + importFile);%>
      <jsp:forward page="./runningimport.jsp"/>
<% } catch (FileNotFoundException e) {
      log.error("IMPORT NOT STARTED: FILE NOT FOUND");
      log.error("message: " + e.toString());
   } catch (Exception e) {
      log.error("IMPORT NOT STARTED: UNKNOWN ERROR");
      e.printStackTrace();
   }
%>