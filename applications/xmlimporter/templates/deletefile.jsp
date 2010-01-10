<%@page language="java" contentType="text/html"
 import="java.io.*, java.util.*,org.mmbase.module.sessionInfo,
org.mmbase.util.scanpage, org.mmbase.applications.xmlimporter.Consultant,
 org.mmbase.util.logging.Logger, org.mmbase.util.logging.Logging" %>

<%!  Logger log = Logging.getLoggerInstance(
     "org.mmbase.applications.xmlimporter.jsp.deletefile.jsp"); %>

<html>
  <head>
    <title>XML Import</title>
    <link rel="stylesheet" href="css/mmbase.css" type="text/css">
  </head>
<body  class="basic">

<H1>XML Importer Delete From Server</H1>
<HR>
<%  try {
     String importDir = System.getProperty("mmbase.config") + "/import/";
     String deleteFile = importDir + request.getParameter("deleteFile");
     if (request.getParameter("deleteFile") == null ||
         request.getParameter("deleteFile").equals("")) {
         throw new FileNotFoundException();
     }
     java.io.File delFile = new File(deleteFile);
     delFile.delete();
     String s1 = "FILE DELETED: " + deleteFile;
     log.info(s1);
     out.println(s1);
    } catch (FileNotFoundException e) {
        String s1 = "FILE NOT DELETED: FILE NOT FOUND";
        log.error(s1);
        out.println(s1);
    } catch (Exception e) {
        String s1 = "FILE NOT DELETED: UNKNOWN ERROR";
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