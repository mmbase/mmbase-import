<%@ page language="java" import="com.jspsmart.upload.*,
 org.mmbase.util.logging.Logger, org.mmbase.util.logging.Logging"%>

<jsp:useBean id="smartUpload" scope="page"
 class="com.jspsmart.upload.SmartUpload" />

<%!  Logger log = Logging.getLoggerInstance(
     "org.mmbase.applications.xmlimporter.jsp.uploadfile.jsp"); %>

<HTML>
  <head>
    <title>XML Import</title>
    <link rel="stylesheet" href="css/mmbase.css" type="text/css">
  </head>
<body>

<H1>XML Importer Upload To Server</H1>
<HR>
<% int count=0;        
   // Initialization
   smartUpload.initialize(pageContext);
   // Upload	
   smartUpload.upload();
   // Select each file
   for (int i=0;i<smartUpload.getFiles().getCount();i++){
      // Retreive the current file
      com.jspsmart.upload.File selectedFile = smartUpload.getFiles().getFile(i);
      // Save it only if this file exists
      if (!selectedFile.isMissing()) {
         // Save the files with its original names in a virtual path of the web server       
         String importDir = System.getProperty("mmbase.config") + "/import/";
	 selectedFile.saveAs(importDir + selectedFile.getFileName());
	 // selectedFile.saveAs("/upload/" + selectedFile.getFileName(), smartUpload.SAVE_VIRTUAL);
	 // sample with a physical path
	 // selectedFile.saveAs("c:\\temp\\" + selectedFile.getFileName(), smartUpload.SAVE_PHYSICAL);
	 //  Display the properties of the current file
	 out.println("<p>");
         String s1 = "FilePathName = " + selectedFile.getFilePathName();
	 out.println(s1 + "<BR>");
	 out.println("Size = " + selectedFile.getSize() + "<BR>");
         log.info("uploaded: " + s1);
	 //out.println("FieldName = " + selectedFile.getFieldName() + "<BR>");
	 //out.println("FileName = " + selectedFile.getFileName() + "<BR>");
	 //out.println("FileExt = " + selectedFile.getFileExt() + "<BR>");
	 //out.println("ContentType = " + selectedFile.getContentType() + "<BR>");
	 //out.println("ContentDisp = " + selectedFile.getContentDisp() + "<BR>");
	 //out.println("TypeMIME = " + selectedFile.getTypeMIME() + "<BR>");
	 //out.println("SubTypeMIME = " + selectedFile.getSubTypeMIME() + "<BR>");
	 //out.println("</p>");
	 count ++;
      }
   }

   // Display the number of files which could be uploaded 
   //out.println("<BR>" + smartUpload.getFiles().getCount() + " files could be uploaded.<BR>");

   // Display the number of files uploaded 
   out.println(count + " file(s) uploaded.");
%>

<FORM METHOD="POST" ACTION="./importhome.jsp" ENCTYPE="multipart/form-data">
   <INPUT TYPE="SUBMIT" VALUE="Back to import page">
</FORM>

</BODY>
</HTML>