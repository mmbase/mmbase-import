<%@ page import="com.finalist.util.http.BulkUploadUtil"%>
<script type="text/javascript">
       function upload() {
           if(document.getElementById("atype").options[document.getElementById("atype").options.selectedIndex].value=="urls"){
              document.getElementByName("uploadButton").disable=true;
              alert("Only attachments and images can be uploaded!");
               }
           setTimeout('sayWait();',0);
       }
   
       function sayWait() {
           document.getElementById("busy").style.visibility="visible";
           //document.getElementById("notbusy").style.visibility="hidden";
       }
</script>

<html:form action="/editors/repository/AssetUploadAction.do" enctype="multipart/form-data" method="post">
<input type="hidden" id="assetType" name="assetType" value="attachments"/>
<input type="hidden" id="parentchannel" name="parentchannel" value="${parentchannel}"/>
<table border="0">
   <tr>
      <td><fmt:message key="asset.upload.explanation" /></td>
   </tr>
   <tr>
      <td><html:file property="file" /></td>
   </tr>
   <tr>
      <td><html:submit property="uploadButton" onclick="upload();">
         <fmt:message key='assets.upload.submit' /></html:submit></td>
   </tr>
</table>
</html:form>
<div id="busy"><fmt:message key="uploading.message.wait" /><br />
</div>
<%
   // retrieve list op node id's from either the recent upload
   // or from the request url to enable a return url
   // in combination with struts which have to be investigated first
   String uploadedNodes = "";
   int numberOfUploadedNodes = -1;
   if ("post".equalsIgnoreCase(request.getMethod())) {
      List<Integer> nodes = (ArrayList<Integer>)(request.getAttribute("uploadedAssets"));
      uploadedNodes = BulkUploadUtil.convertToCommaSeparated(nodes);
      numberOfUploadedNodes = nodes.size();
   } else {
      if (request.getParameter("uploadedNodes") != null) {
         uploadedNodes = request.getParameter("uploadedNodes");
      }
      if (request.getParameter("numberOfUploadedNodes") != null) {
         numberOfUploadedNodes = Integer.parseInt(request.getParameter("numberOfUploadedNodes"));
      }
   }
%>
<%
   if (numberOfUploadedNodes == 0) {
%>
<p><fmt:message key="assets.upload.error" /></p>
<%
   }
%>