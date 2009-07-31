<%@ page language="java" import="org.apache.commons.fileupload.*,java.util.*,java.io.File,
 org.mmbase.util.logging.Logger, org.mmbase.util.logging.Logging"%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="mmbase_xmlimportdir" jspvar="importDir" from="session" />
<%!  Logger log = Logging.getLoggerInstance(
     "org.mmbase.applications.xmlimporter.jsp.uploadfile.jsp"); %>
<html>
  <head>
    <title>XML Import</title>
    <link rel="stylesheet" href="css/mmbase.css" type="text/css">
  </head>
  <body>
    <h1>XML Importer Upload To Server</h1>
    <hr />
<%  int count=0;
    // Initialization
    DiskFileUpload fu = new DiskFileUpload();
    try {
        List fileItems = fu.parseRequest(request);
        int fileCount = 0;
        // Save the files with its original names in a virtual path of the web server
        // String importDir = System.getProperty("mmbase.config") + "/import/";
        for (Iterator i = fileItems.iterator(); i.hasNext(); ) {
            FileItem fi = (FileItem)i.next();
//            if (!fi.isFormField()) {
            if (!fi.getName().equals("")) {
              fi.write(new File(importDir + fi.getName()));
              %>
                <p>FilePathName = <%=fi.getName()%><br />
                   Size = <%=fi.getSize()%><br /></p>
              <%
              log.info("Uploaded: "+fi.getName());
              count ++;
            }
        }
    } catch (FileUploadBase.SizeLimitExceededException e) {
  %>
    <p>Uploaded file exceeds maximum file size of <%=fu.getSizeMax()%> bytes.<br />
  <%
    } catch (FileUploadException e) {
  %>
    <p>An error ocurred while uploading this file (<%=e.toString()%>).<br />
  <%
    }
  %>
    <p><%=count%> file(s) uploaded</p>
    <form method="POST" action="./importhome.jsp" enctype="multipart/form-data">
       <input type="submit" value="Back to import page" />
    </form>

  </body>
</html>
