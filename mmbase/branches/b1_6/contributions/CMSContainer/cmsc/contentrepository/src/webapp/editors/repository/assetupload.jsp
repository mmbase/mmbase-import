<%@ page import="com.finalist.util.http.BulkUploadUtil"%>
<script type="text/javascript">
       function upload() {
           setTimeout('sayWait();',0);
       }
   
       function sayWait() {
           document.getElementById("busy").style.visibility="visible";
       }
</script>

<html:form action="/editors/repository/AssetUploadAction.do" enctype="multipart/form-data" method="post">
<input type="hidden" id="assetType" name="assetType" value="attachments"/>
<input type="hidden" id="insertAsset" name="insertAsset" value="${param.insertAsset}"/>
<input type="hidden" id="parentchannel" name="parentchannel" value="${parentchannel}"/>
<table border="0">
   <tr>
      <td><fmt:message key="asset.upload.explanation" /></td>
   </tr>
         <c:if test="${param.exist=='1'}">
            <tr>
               <td style="color:red;"><fmt:message key="asset.upload.existed" /></td>
            </tr>
         </c:if>
         <c:if test="${param.emptyFile=='yes'}">
            <tr>
               <td style="color:red;"><fmt:message key="asset.upload.emptyfile" /></td>
            </tr>
         </c:if>
         <c:if test="${param.exceed == 'yes'}">
            <tr>
               <td style="color:red;"><fmt:message key="asset.upload.size.exceed"/></td>
            </tr>
         </c:if>
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