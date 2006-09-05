<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@include file="../../globals.jsp" %>
<fmt:setBundle basename="cmsc-repository" scope="request" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.NodeManager"%>
<%@page import="org.mmbase.bridge.Node"%>
<%@page import="com.finalist.util.http.BulkUploadUtil"%>
<%@page import="org.mmbase.bridge.Cloud"%>
<html>
<head>
<link href="../css/main.css" type="text/css" rel="stylesheet"/>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload attachments</title>
  <script language="javascript" type="text/javascript">
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
</head>
<body>
<mm:cloud jspvar="cloud" >
      <div class="tabs">
         <div class="tab">
            <div class="body">
               <div>
                  <a href="attachmentsearch.jsp"><fmt:message key="attachments.title" /></a>
               </div>
            </div>
         </div>
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
          <form action="" enctype="multipart/form-data" method="POST">
                <table border="0">
                   <tr>
                      <td><fmt:message key="attachments.upload.explanation" /></td>
                   </tr>
                   <tr>
                      <td><input type="file" name="zipfile"/></td>
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
        <div id="busy" style="visibility:hidden;position:absolute;width:100%;text-alignment:center;">
            uploading... Please wait.<br />
        </div>
<%
    // retrieve list op node id's from either the recent upload
    // or from the request url to enable a return url
    // TODO move this to a struts action there are some issue with HttpUpload
    // in combination with struts which have to be investigated first
    String uploadedNodes = "";
    int numberOfUploadedNodes = -1;
    if ("post".equalsIgnoreCase(request.getMethod())) {
        NodeManager manager = cloud.getNodeManager("attachments");
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

<% if (numberOfUploadedNodes == 0) { %>
    <p><fmt:message key="attachments.upload.error"/></p>
<% } else if (numberOfUploadedNodes > 0) { %>
    <p><fmt:message key="attachments.upload.result">
           <fmt:param value="<%= numberOfUploadedNodes %>"/>
       </fmt:message>
    </p>
         <table>
            <tr class="listheader">
               <th></th>
               <th nowrap="true"><fmt:message key="attachmentsearch.titlecolumn" /></th>
               <th><fmt:message key="attachmentsearch.filenamecolumn" /></th>
            </tr>
            <tbody class="hover">
                <c:set var="useSwapStyle">true</c:set>

                <mm:listnodescontainer path="attachments" nodes="<%= uploadedNodes %>">
                    <mm:listnodes>

                    <mm:import id="url">javascript:selectElement("<mm:field name="number"/>", "<mm:field name="title"/>");</mm:import>
                    <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
                       <td>
                        <%-- use uploadedNodes and numberOfUploadedNodes in return url --%>
                        <a href="<mm:url page="../WizardInitAction.do">
                                     <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                     <mm:param name="returnurl" value="<%="../editors/resources/attachmentupload.jsp?uploadedNodes=" + uploadedNodes + "&numberOfUploadedNodes=" + numberOfUploadedNodes%>" />
                                 </mm:url>">
                              <img src="../gfx/icons/page_edit.png" /></a>
                       </td>
                       <td><mm:field name="title"/></td>
                       <td><mm:field name="filename"/></td>
                    </tr>
                    <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
                    </mm:listnodes>
                </mm:listnodescontainer>

            </tbody>
         </table>
<% } %>
</div>
</div>
</mm:cloud>
</body>
</html>