<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.util.http.BulkUploadUtil,com.finalist.cmsc.upload.service.OODocUploadUtil"%>


<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="attachments.upload.title">
    <script src="../repository/search.js"type="text/javascript" ></script>
  
	<script language="javascript" type="text/javascript">
	    function upload() {
	        var f=document.forms[0];
	        f.submit();
	        setTimeout('sayWait();',0);
	
	    }
	
	    function sayWait() {
	        document.getElementById("busy").style.visibility="visible";
           document.getElementById("notbusy").style.visibility="hidden";
	    }
				
		function showInfo(objectnumber) {
			openPopupWindow('attachmentinfo', '500', '500', 'attachmentinfo.jsp?objectnumber='+objectnumber);
	    }

		 
	</script>


</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" >


      <div class="tabs">
         <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="attachments.upload.title" /></a>
               </div>
            </div>
         </div>
      </div>
      
      <div class="editor" style="height:500px">
      <div class="body">
          <form action="../upload/OdtUpload.do" enctype="multipart/form-data" method="post">
          	         		
          		<input type="hidden" name="channel" value="<%=request.getParameter("parent")%>"/>
          		<input type="hidden" name="channelbak" value="${parent }"/>
                <table border="0">
                   <tr>
                      <td><fmt:message key="attachments.upload.explanation" /></td>
                   </tr>
                   <tr>
                      <td><input type="file" name="odtfile"/></td>
                   </tr>
                   <tr>
                      <td><input type="button" name="uploadButton" onclick="upload();" 
                      			value="<fmt:message key="attachments.upload.submit" />"/></td>
                   </tr>
                </table>
         </form>
      </div>
      <div class="ruler_green"><div><fmt:message key="attachments.upload.results" /></div></div>
      <div class="body">
        <div id="busy">
            <fmt:message key="uploading.message.wait"/><br />
        </div>
<c:if test="${not empty binaries}">
<table width="100%">
<tr><td></td><td>Title</td></tr>
<c:forEach var="entity" items="${binaries}">
<tr>
	<td><a href="DeleteOdt.do?parent=${parent}&name=${entity.title}"><img src="../gfx/icons/delete.png" width="16" height="16" title="delete" alt="delete"/></a></td><td><c:out value="${entity.title}"/></td>
</tr>
</c:forEach>
</table>

<form action="../upload/OdtStore.do">
<p><input type="submit" value="submit"/>
	<input type="hidden" name="parent" value="${parent}"/>
</form>

</c:if>
</div>
</div>

</mm:cloud>
</body>
</html:html>
</mm:content>