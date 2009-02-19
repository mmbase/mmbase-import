<%@page language="java" contentType="text/html"
 import="org.mmbase.applications.xmlimporter.Consultant, java.lang.Thread,
 org.mmbase.util.logging.Logger, org.mmbase.util.logging.Logging" %>

<jsp:useBean id="consultant" scope="session"
 class="org.mmbase.applications.xmlimporter.Consultant" />

<%!  Logger log = Logging.getLoggerInstance(
     "org.mmbase.applications.xmlimporter.jsp.runningimport.jsp"); %>

<html>

<head>
<%
   if ( consultant.getImportStatus() == Consultant.IMPORT_STARTING
   || consultant.getImportStatus() == Consultant.IMPORT_RUNNING ) { %>
    <META HTTP-EQUIV=Refresh CONTENT="1; URL=runningimport.jsp">
<% } %>
    <title>XML Import Running</title>
    <link rel="stylesheet" href="../css/mmbase.css" type="text/css">
</head>

<body  class="basic">

<%
   if (consultant.getImportStatus() == Consultant.IMPORT_STARTING
       || consultant.getImportStatus() == Consultant.IMPORT_RUNNING) {
      %> <H1>XML Importer Running</H1> <%
   } else {
      %> <H1>XML Importer Finished</H1> <%
   }
%>

<HR>

<% try {
      if (consultant.getImportStatus() == Consultant.IMPORT_TIMED_OUT) {
         out.println("A TRANSACTION HAS TIMED OUT.<BR><BR>Import aborted.");
      } else if ( consultant.interactive() && consultant.duplicatesFound() ) {%>
         <jsp:forward page="./choosemergeresult.jsp"/>
<%    } else if (!consultant.interactive()
                 && consultant.getImportStatus() == Consultant.IMPORT_FINISHED) {
         String s1 = "NO DUPLICATES FOUND";
         if ( consultant.duplicatesFound() ) {
            s1 = "DUPLICATES FOUND\n\n" +
                 "Consult tcpDuplicates.txt in the mmbase/config/import/report directory for more information.";
         }
         out.println(s1);
      }
   } catch (Exception e) {
        String s1 = "UNKNOWN ERROR";
        String s2 = e.toString();
        log.error(s1);
        out.println(s1);
        log.error(s2);
        out.println(s2);
    }
%>

<FORM METHOD="POST" ACTION="./importhome.jsp" ENCTYPE="multipart/form-data">
   <INPUT TYPE="SUBMIT" VALUE="Back to import page">
</FORM>

</body>
</html>