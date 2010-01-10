<%@ page language="java" contentType="text/html" import="java.io.*,
 org.mmbase.util.logging.Logger, org.mmbase.util.logging.Logging" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%!  Logger log = Logging.getLoggerInstance(
     "org.mmbase.applications.xmlimporter.jsp.importhome.jsp"); %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="administrator">
<mm:import externid="mmbase_xmlimportdir" jspvar="importDir" from="session" />
<html>
  <head>
    <title>XML Import</title>
    <link rel="stylesheet" href="css/mmbase.css" type="text/css" />
  </head>
<body>

<h1>XML Importer Home</h1>
<mm:import externid="importdir" jspvar="importdir" />
<mm:present referid="importdir">
	<% session.setAttribute("mmbase_xmlimportdir",importdir); %>
</mm:present>

<form method="post" action="<mm:url />">
Directory to upload in<br />
<input type="text" name="importdir" value="<mm:write referid="mmbase_xmlimportdir" />" size="60" /> <input type="submit" name="setdir" value="OK" />
</form>


<hr /><p>Upload a file to the server</p>

<form method="post" action="./uploadfile.jsp" enctype="multipart/form-data">
   <input type="file" name="file1" size="60" /><br />
   <input type="file" name="file2" size="60" /><br />
   <input type="file" name="file3" size="60" /><br />
   <input type="file" name="file4" size="60" /><br />
   <input type="submit" value="start upload" />
</form>

<hr /><p>Start Import</p>

<form method="post" action="./startimport.jsp">
   <select name="choosenFile">
<% try {
//      String importDir = System.getProperty("mmbase.config") + "/import/";
      java.io.File dirName = new java.io.File(importDir);
      dirName.mkdirs(); // Creates directory if not present yet.
      java.io.File[] fileList = dirName.listFiles();
      %><option><%= "" %></option> <% 
      for (int i=0; i < fileList.length; i++) { 
         if ( fileList[i].isFile()) {
             %> <option><%= fileList[i].getName() %></option> <%
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
   </select>
      <input type="checkbox" name="interactive" value="true" /> process duplicates interactive
      <input type="submit" value="Start Import" />
</form>

<hr /><p>Delete a file from the server</p>

<form method="post" action="./deletefile.jsp">
<select name="deleteFile">
<% try {
      // String importDir = System.getProperty("mmbase.config") + "/import/";
      log.info("import directory: " + importDir);
      java.io.File dirName = new java.io.File(importDir);
      java.io.File[] fileList = dirName.listFiles();
      %><option><%= "" %></option><% 
      for (int i=0; i < fileList.length; i++) { 
         if ( fileList[i].isFile()) {
             %> <option><%= fileList[i].getName() %></option> <%
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
</select>
<input type="submit" value="Delete From Server" />
</form>

<p></p><hr />
<p>logged on as:  <%= wolk.getUser().getIdentifier() %> (rank: <%= wolk.getUser().getRank() %>)</p>
</body>
</html>
</mm:cloud>