<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.util.tools.Excel2Menu"%>
<%@page import="net.sf.mmapps.commons.util.UploadUtil" %>
<html>
<head>
   <link href="../style.css" type="text/css" rel="stylesheet"/>
   <title>Excel2menu</title>
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
   .prompt { width: 100px; }
   input { width: 300px; }
   </style>
</head>
    <body>
      <h2>Excel2menu</h2>
      <mm:cloud method="http" rank="administrator" jspvar="cloud">
      <mm:log jspvar="log">
      <div id="form">
       <form action="?" enctype="multipart/form-data" method="post" >
         <table>
           <tr><td class="prompt">MaxLevel</td>
           <td><input type="text" name="maxLevel" value="5" /></td></tr>
           <tr><td class="prompt">SiteTitle</td>
           <td><input type="text" name="siteTitle" value="ExcelImportSite" /></td></tr>
           <tr><td class="prompt">SitePath</td>
           <td><input type="text" name="sitePath" value="natmm" /></td></tr>
           <tr><td class="prompt">Excelfile</td>
           <td><input type="file" name="excelfile"/></input><br />
         </table>
         <br />
        <input type="hidden" name="action" value="upload" /></td></tr>
        <input type="button" onclick="upload();" value="upload" />
       </form>
      </div>
      <div id="busy" style="visibility:hidden;position:absolute;width:100%;text-alignment:center;">
          uploading... Please wait.<br />
      </div>
      <%
      	if ("post".equalsIgnoreCase(request.getMethod())) {
            %>
            <mm:import externid="maxLevel" jspvar="maxLevel">5</mm:import>
            <mm:import externid="siteTitle" jspvar="siteTitle">ExcelImportSite</mm:import>
            <mm:import externid="sitePath" jspvar="sitePath">natmm</mm:import>
            <%

         List binaries = UploadUtil.uploadFiles(request, 4*1000*1000); 
         Excel2Menu t = new Excel2Menu(cloud, maxLevel, siteTitle, sitePath);
      
         for (Iterator iter = binaries.iterator(); iter.hasNext();) {
              UploadUtil.BinaryData binary = (UploadUtil.BinaryData) iter.next();
          	t.convert(binary.getInputStream());
         }
      %>
      Created
      <% } %>
      
      </mm:log>
      </mm:cloud>
      Done!
   </body>
</html>