<%@include file="/taglibs.jsp" %>
<%@page import="java.io.*"%>
<%@page import="org.apache.commons.fileupload.*"%>
<%@page import="nl.leocms.util.tools.Excel2Menu"%>
<html>
<head>
   <title>Excel2Menu</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <script language="javascript">
   function upload() {
     var f=document.forms[0];
     f.submit();
     setTimeout('sayWait();',0);

   }

   function sayWait() {
     document.getElementById("form").style.visibility="hidden";
     document.getElementById("busy").style.visibility="visible";
   }
   </script>
   <style type="text/css">
   input { width: 300px; }
   </style>
</head>
<body>
   <h2>Excel2Menu</h2>
   <mm:cloud method="http" rank="administrator" jspvar="cloud">
   <mm:log jspvar="log">
   <div id="form">
    <form action="?" enctype="multipart/form-data" method="POST" >
      <table class="formcontent" >
        <tr>
         <td class="fieldname">MaxLevel</td>
         <td><input type="text" name="maxLevel" value="5" /></td>
        </tr>
        <tr>
         <td class="fieldname">SitePath</td>
         <td><input type="text" name="sitePath" value="natmm" /></td>
        </tr>
        <tr>
         <td class="fieldname">Excelfile</td>
         <td><input type="file" name="excelfile"/></td>
        </tr>
        <tr>
         <td colspan="2"><input type="button" onclick="upload();" value="upload" style="width:100px;text-align:center;"/></td>
        </tr>
      </table>
      <br />
     <input type="hidden" name="action" value="upload"/>
    </form>
   </div>
   <div id="busy" style="visibility:hidden;position:absolute;width:100%;text-alignment:center;">
       Uploading... Please wait.<br />
   </div>
   <%
   String sMaxLevel = null;
   String sSiteTitle = null;
   String sSitePath = null;
   InputStream inputStream = null;
   
   if(FileUpload.isMultipartContent(request)) {
     
     ApplicationHelper ap = new ApplicationHelper(cloud);
     
     DiskFileUpload upload = new DiskFileUpload();
     upload.setSizeMax(250*1024*1024);
     upload.setSizeThreshold(4096);
     upload.setRepositoryPath(ap.getTempDir());
     List items = upload.parseRequest(request);
     Iterator it = items.iterator();
     while(it.hasNext()) {
        FileItem item = (FileItem) it.next();
        String fieldName = item.getFieldName();
   
        if (fieldName.equals("maxLevel")) {
           sMaxLevel = item.getString();
        }
        if (fieldName.equals("sitePath")) {
           sSitePath = item.getString();
        }
        if(fieldName.equals("excelfile")){
           inputStream = item.getInputStream();
        }
     }
   
     Excel2Menu exel2Menu = new Excel2Menu(cloud, sMaxLevel, sSitePath);
     exel2Menu.convert(inputStream);
   
     %>Created<%
   }
   %>
   </mm:log>
   </mm:cloud>
</body>
</html>
