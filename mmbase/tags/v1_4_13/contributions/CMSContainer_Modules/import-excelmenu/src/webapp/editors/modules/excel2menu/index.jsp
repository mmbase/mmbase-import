<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@ page import="com.finalist.cmsc.excel2menu.MenuImportRequest"%>
<%@ page import="org.mmbase.bridge.*"%>
<html>
<head>
  <title>Excel2menu</title>
  <script type="text/javascript">
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
<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<div id="form">
 <form action="?" enctype="multipart/form-data" method="post" >
<table>
  <tr><td class="prompt">Properties</td>
  <td><textarea name="properties" value="meny" rows="20" cols="40"></textarea></td></tr>
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
		MenuImportRequest importer = new MenuImportRequest();
		importer.process(cloud, request);
%>
Created
<% } %>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>