<%@ page language="java" contentType="text/html" import="java.io.*,
 org.mmbase.util.logging.Logger, org.mmbase.util.logging.Logging" %>

<%!  Logger log = Logging.getLoggerInstance(
     "org.mmbase.applications.xmlimporter.jsp.importhome.jsp"); %>

<HTML>
  <head>
    <title>XML Import</title>
    <link rel="stylesheet" href="css/mmbase.css" type="text/css">
  </head>
<body>

<H1>XML Importer Home</H1>

<HR><P>Upload a file to the server</P>

<FORM METHOD="POST" ACTION="./uploadfile.jsp" ENCTYPE="multipart/form-data">
   <INPUT TYPE="FILE" NAME="FILE1" SIZE="60"><BR>
   <INPUT TYPE="FILE" NAME="FILE2" SIZE="60"><BR>
   <INPUT TYPE="FILE" NAME="FILE3" SIZE="60"><BR>
   <INPUT TYPE="FILE" NAME="FILE4" SIZE="60"><BR>
   <INPUT TYPE="SUBMIT" VALUE="Start Upload">
</FORM>

<HR><P>Start Import</P>

<FORM METHOD="POST" ACTION="./startimport.jsp">
   <SELECT NAME="choosenFile">
<% try {
      String importDir = System.getProperty("mmbase.config") + "/import/";
      java.io.File dirName = new java.io.File(importDir);
      dirName.mkdirs(); // Creates directory if not present yet.
      java.io.File[] fileList = dirName.listFiles();
      %><OPTION><%= "" %></OPTION> <% 
      for (int i=0; i < fileList.length; i++) { 
         if ( fileList[i].isFile()) {
             %> <OPTION><%= fileList[i].getName() %></OPTION> <%
         }
      }
   } catch (Exception e) {
      String s1 = "import.jsp IMPORT NIET GESTART: REDEN ONBEKEND";
      String s2 = e.toString();
      log.error(s1);
      out.println(s1);
      log.error(s2);
      out.println(s2);
}%>
   </SELECT>
      <INPUT type=checkbox name="interactive" value="true">process duplicates interactive
      <INPUT TYPE=submit VALUE="Start Import">
</FORM>

<HR><P>Delete a file from the server</P>

<FORM METHOD="POST" ACTION="./deletefile.jsp">
<SELECT NAME="deleteFile">
<% try {
      String importDir = System.getProperty("mmbase.config") + "/import/";
      log.info("import directory: " + importDir);
      java.io.File dirName = new java.io.File(importDir);
      java.io.File[] fileList = dirName.listFiles();
      %><OPTION><%= "" %></OPTION><% 
      for (int i=0; i < fileList.length; i++) { 
         if ( fileList[i].isFile()) {
             %> <OPTION><%= fileList[i].getName() %></OPTION> <%
         }
      }
   } catch (Exception e) {
      String s1 = "import.jsp FILE DELETE NIET GESTART: REDEN ONBEKEND";
      String s2 = e.toString();
      log.error(s1);
      out.println(s1);
      log.error(s2);
      out.println(s2);
   }%>
</SELECT>
<INPUT TYPE=submit VALUE="Delete From Server">
</FORM>

<P><P><HR>

</body>
</HTML>