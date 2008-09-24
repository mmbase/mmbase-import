<%@ page language="java" contentType="text/html;charset=utf-8" session="false"
import="org.apache.commons.fileupload.*,java.util.*,java.io.*" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
  "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html" escaper="none"><mm:cloud>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Send and process mail with attachments</title>
    <style type="text/css" media="screen">
      /* <![CDATA[ */
      body { font-family: "Lucida Grande", Arial, sans-serif; font-size: 0.8em; }
      label { font-weight: bold; }
      .err { color: #D00021; }
      /* ]]> */
    </style>
  </head>
  <body> 
    <%! // Very low-tech e-mail address validation 
    public boolean isValidEmailAddr(String emailAddrString) {
    boolean isValid = false;
    if (emailAddrString != null && 
    emailAddrString.indexOf("@") != -1 &&
    emailAddrString.indexOf(".") != -1) {
    isValid = true;
    }
    return isValid;
    }
    %>
<mm:log jspvar="log">
  <%
// Variables
StringBuffer errorMsg = new StringBuffer();
boolean noError = false;

String subject = "";
String name = "";
String email = "";
String message = "";

// directories should be written like '/tmp' (without a slash at the end)
String webappDir = "/home/whatever/web-app";    // !! change to mirror your webapp!!
String uploadDir = "/uploads";      // directory to upload to, should be in webapp
String fileName = "";
int fileCount = 0;
ArrayList fileNames = new ArrayList();

// Initialization
// Maximum size (bytes) before a FileUploadException will be thrown
int maxsize = 500000;       // 500000 = 1/2 MegaByte?
DiskFileUpload fu = new DiskFileUpload();
fu.setSizeMax(maxsize);
// maximum size that will be stored in memory --- what shoudl this be?
fu.setSizeThreshold(50000); // 50 Kb ?
// the location for saving data that is larger than getSizeThreshold()
// where to store?
fu.setRepositoryPath(webappDir + uploadDir);

boolean isMultipart = FileUpload.isMultipartContent(request);   // check multipart
log.debug("Is it a multipart? " + isMultipart);
if (isMultipart) {
// Upload
try {
    List fileItems = fu.parseRequest(request);
    Iterator iter = fileItems.iterator();
    log.debug("List with files size: " + fileItems.size());
    while (iter.hasNext()) {
        FileItem fi = (FileItem)iter.next();
        if (!fi.isFormField()) {
            String fullFileName = fi.getName();
            fileName = fullFileName;
            // the path passed is in the client system's format,
            // so test all known path separator chars ('/', '\' and "::" )
            // and pick the one which would create the smallest filename
            // Using Math is rather ugly but at least it is shorter and performs better
            // than Stringtokenizer, regexp, or sorting collections
            int last = Math.max(Math.max(
                fullFileName.lastIndexOf(':'), // old mac path (::)
                fullFileName.lastIndexOf('/')),  // unix path
                fullFileName.lastIndexOf('\\')); // windows path
            if (last > -1) {
                fileName = fullFileName.substring(last+1);
            }
            log.debug("We've got a file: " + fullFileName);
            log.debug("Uploading files to: " + webappDir + uploadDir);
            if (fi.get().length > 0) { // no need uploading nothing
                // Store all files
                fileCount++;
                fileNames.add(fileName);
                File uploadedFile = new File(webappDir + uploadDir + "/" + fileName);
                fi.write(uploadedFile);
                log.debug("Uploaded file: " + fileName);
            }
        // fields from e-mail form
        } else {    // fi.isFormField()
            String key = fi.getFieldName();
            String value = fi.getString();
            
            // name, email, subject, message
            if (key.equals("subject")) subject = value;
            if (key.equals("name")) name = value;
            if (key.equals("email")) email = value;
            if (key.equals("message")) message = value;
        }
    }
    log.info("Uploaded '"+ fileCount + "' files to: " + webappDir + uploadDir);
} catch (FileUploadBase.SizeLimitExceededException slee) {
%>
    <p class="error">Your file exceeds the maximum size of <%= maxsize %> bytes:
    (<%= slee.toString() %>).</p>
<%
} catch (FileUploadException fue) {
%>
    <p class="error">There has been an error uploading your file: (<%= fue.toString() %>).</p>
<%
}

// Check: message, e-mail address
if (message.equals("")) {
  errorMsg.append("<br />No message written.");
  noError = false;
}
if (!isValidEmailAddr(email)) {
  errorMsg.append("<br />E-mail address incorrect or missing.");
  noError = false;
}

// Geen errors, ga door
if (errorMsg.length() < 1) noError = true;
if (noError) {
    log.debug("No errors, send message");
%>  
    <mm:cloud logon="admin" pwd="admin2k" method="pagelogon">
    <mm:createnode id="mailtje" type="email">
      <mm:setfield name="from"><%= email %></mm:setfield>
      <mm:setfield name="replyto"><%= email %></mm:setfield>
      <mm:setfield name="to">daniel@mmbase.org</mm:setfield>
      <mm:setfield name="subject"><%= subject %></mm:setfield>
      <mm:setfield name="body">
<% if (fileCount > 0) { %>
<multipart id="plaintext" type="text/plain" encoding="UTF-8"><%= message %></multipart>
<%
    Iterator iterator = fileNames.iterator();
    int fi = 0;
    while (iterator.hasNext()) {
        String attachFile = (String)iterator.next();
%>        
<multipart id="attach<%= fi %>" type="application/octet-stream" file="<%= uploadDir %>/<%= attachFile %>" filename="<%= attachFile %>" attachment="<%= attachFile %>"></multipart>
<%
        fi++;
    }
} else { 
%><%= message %><% } %>
        </mm:setfield>
    </mm:createnode>
    
    <mm:node referid="mailtje">
      <mm:field name="mail(oneshot)" />
    </mm:node>
    </mm:cloud>
    
    <%  log.debug("Message (seems to be) send!"); %>
    <%-- melding mail is verstuurd --%>
      <p>Your message has been send</p>

<% } else { %>
  <p class="err">Error: <%= errorMsg %><br />
  Go <a href="javascript:window.history.go(-1)">back</a>.</p>
<% } 
}
%>
</mm:log>
    
</body>
</html>
</mm:cloud>
</mm:content>
