<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" %><%@page import="com.finalist.util.http.BulkUploadUtil"
%><%@page import="java.util.List"
%> <mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="images.upload.title">
  <script src="../repository/search.js" type="text/javascript"></script>
  <script type="text/javascript">
    function upload() {
        setTimeout('sayWait();',0);
    }

    function sayWait() {
        document.getElementById("busy").style.visibility="visible";
        document.getElementById("notbusy").style.visibility="hidden";
    }


   function showInfo(objectnumber) {
      openPopupWindow('imageinfo', '900', '500', 'imageinfo.jsp?objectnumber='+objectnumber);
    }

   function unpublish(parentchannel, objectnumber) {
       var url = "../repository/ImageDeleteAction.do";
       url += "?channelnumber=" + parentchannel;
       url += "&objectnumber=" + objectnumber;
       document.location.href = url;
   }

    var blockSelect = false;
  </script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
      <div class="editor" style="height:580px">
          <div class="body">
              <html:form action="/editors/repository/ImageUploadAction.do" enctype="multipart/form-data" method="post">
                  <input type="hidden" id="parentchannel" name="parentchannel" value="${param.channelid}"/>
                  <table border="0">
                  <tr>
                     <td><fmt:message key="images.upload.explanation" /></td>
                  </tr>
                  <c:if test="${param.exist=='1'}">
                     <tr>
                        <td style="color:red;"><fmt:message key="asset.upload.existed" /></td>
                     </tr>
                  </c:if>
                  <c:if test="${param.exceed=='yes'}">
                     <tr>
                        <td style="color:red;"><fmt:message key="asset.upload.size.exceed" /></td>
                     </tr>
                  </c:if>
                  <c:if test="${param.uploadedNodes=='0' && param.exist == '0'&&param.exceed == 'no'}">
                     <tr>
                        <td style="color:red;"><fmt:message key="image.upload.notimage" /></td>
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
          </div>
          <div class="ruler_green"><div><fmt:message key="images.upload.results" /></div></div>

          <div class="body">
            <div id="busy">
                <fmt:message key="uploading.message.wait"/><br />
            </div>
<c:if test="${param.uploadedNodes != 0}">
<table>
   <tr class="listheader">
      <th></th>
      <th nowrap="true"><fmt:message key="imagesearch.titlecolumn" /></th>
      <th><fmt:message key="imagesearch.filenamecolumn" /></th>
      <th><fmt:message key="imagesearch.mimetypecolumn" /></th>
      <th></th>
   </tr>
   <tbody class="hover">
      <c:set var="useSwapStyle">true</c:set>
      <mm:listnodescontainer path="images" nodes="${param.uploadedNodes}">
         <mm:listnodes>

            <mm:field name="description" escape="js-single-quotes" jspvar="description">
               <mm:field name="title" escape="js-single-quotes" jspvar="title">
            <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<%=title%>','<mm:image />','<mm:field name="width"/>','<mm:field name="height"/>', '<%=description%>');</mm:import>
               </mm:field>
            </mm:field>

            <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
               <td >
<%-- use uploadedNodes and numberOfUploadedNodes in return url --%>
                  <c:set var="returnUrl">/editors/resources/imageupload.jsp?uploadedNodes=${param.uploadedNodes}&uploadAction=${param.uploadAction}</c:set>
                  <c:choose>
                     <c:when test="${param.uploadAction == 'select'}">
                        <a href="<mm:url page="SecondaryEditAction.do">
                           <mm:param name="action" value="init"/>
                           <mm:param name="number"><mm:field name="number" /></mm:param>
                           <mm:param name="returnUrl" value="${returnUrl}"/>
                           </mm:url>" onclick="blockSelect = true">
                     </c:when>
                     <c:otherwise>
                        <a href="<mm:url page="../WizardInitAction.do">
                           <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                           <mm:param name="returnurl" value="${returnUrl }" />
                           </mm:url>">
                     </c:otherwise>
                  </c:choose>
                           <img src="../gfx/icons/page_edit.png" title="<fmt:message key="images.upload.edit"/>" alt="<fmt:message key="images.upload.edit"/>"/></a>
                        <a href="javascript:showInfo(<mm:field name="number" />);" onclick="blockSelect = true;">
                           <img src="../gfx/icons/info.png" title="<fmt:message key="images.upload.info"/>" alt="<fmt:message key="images.upload.info"/>"/></a>
                        <a href="javascript:unpublish('${sessionScope.creation}','${param.uploadedNodes}');"
                           title="<fmt:message key="asset.delete" />"><img src="../gfx/icons/delete.png" width="16" height="16"
                           alt="<fmt:message key="asset.delete" />"/></a>
               </td>
               <td onMouseDown="objClick(this);">
                  <c:set var="assettype" ><mm:nodeinfo type="type"/></c:set>
                  <mm:field id="title" write="false" name="title"/>
                  <c:if test="${fn:length(title) > 50}">
                     <c:set var="title">${fn:substring(title,0,49)}...</c:set>
                  </c:if>
                  ${title}
               </td>
               <td onMouseDown="objClick(this);">${title}</td>
               <td onMouseDown="objClick(this);"><mm:field name="itype"/></td>
               <td onMouseDown="objClick(this);"><img src="<mm:image template="s(100x100)"/>" alt="" /></td>
            </tr>
            <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
         </mm:listnodes>
      </mm:listnodescontainer>
   </tbody>
</table>
</c:if>
         </div>
      </div>
</mm:cloud>
</body>
</html:html>
</mm:content>