<%@ page import="com.finalist.util.http.BulkUploadUtil"%>
<script src="../repository/search.js" type="text/javascript"></script>
<script type="text/javascript">
       function upload() {
           var f=document.forms[0];
           f.submit();
           setTimeout('sayWait();',0);
   
       }
   
       function sayWait() {
           document.getElementById("busy").style.visibility="visible";
           document.getElementById("notbusy").style.visibility="hidden";
       }
   </script>

<form action="" enctype="multipart/form-data" method="post"><input
	type="hidden" name="uploadAction" value="${param.uploadAction}" />
<table border="0">
	<tr>
		<td><fmt:message key="asset.upload.explanation" /></td>
	</tr>
	<tr>
		<td><input type="file" name="zipfile" /></td>
	</tr>
	<tr>
		<td><input type="button" name="uploadButton" onclick="upload();"
			value="<fmt:message key="assets.upload.submit" />" /></td>
	</tr>
</table>
</form>
<div id="busy"><fmt:message key="uploading.message.wait" /><br />
</div>
<%
   // retrieve list op node id's from either the recent upload
   // or from the request url to enable a return url
   // TODO move this to a struts action there are some issue with HttpUpload
   // in combination with struts which have to be investigated first

   String assetType = "attachments";
   //assetType = get value from the submitted form as the asset type
   //if(assetType == null){
      //assetType = "attachments";
   //}
   String uploadedNodes = "";
   int numberOfUploadedNodes = -1;
   if ("post".equalsIgnoreCase(request.getMethod())) {
      NodeManager manager = cloud.getNodeManager(assetType);
      List<Integer> nodes = BulkUploadUtil.uploadAndStore(manager, request);
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