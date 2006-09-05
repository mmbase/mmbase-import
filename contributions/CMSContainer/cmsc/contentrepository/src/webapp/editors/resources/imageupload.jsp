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
<%@page import="org.mmbase.bridge.NodeList"%>
<%@page import="java.util.List"%>
<html>
<head>
<link href="../css/main.css" type="text/css" rel="stylesheet" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload images</title>
  <script src="../repository/search.js"type="text/javascript" ></script>
  <script src="../utils/rowhover.js" type="text/javascript"></script>
  <script src="../utils/window.js" type="text/javascript"></script>
  <script type="text/javascript" src="../utils/transparent_png.js" ></script>
  <script language="javascript" type="text/javascript">
    function upload() {
        var f=document.forms[0];
        f.submit();
        setTimeout('sayWait();',0);

    }

    function sayWait() {
        document.getElementById("busy").style.visibility="visible";
    }
    
			
	function showInfo(objectnumber) {
		openPopupWindow('imageinfo', '900', '500', 'imageinfo.jsp?objectnumber='+objectnumber);
    }
          
    var blockSelect = false;
  </script>
</head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
      <div class="tabs">
         <div class="tab">
            <div class="body">
               <div>
                  <a href="imagesearch.jsp"><fmt:message key="images.title" /></a>
               </div>
            </div>
         </div>
         <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="images.upload.title" /></a>
               </div>
            </div>
         </div>
      </div>
      
      <div class="editor" style="height:500px">
          <div class="body">
              <form action="" enctype="multipart/form-data" method="POST">
                    <table border="0">
                       <tr>
                          <td><fmt:message key="images.upload.explanation" /></td>
                       </tr>
                       <tr>
                          <td><input type="file" name="zipfile"/></td>
                       </tr>
                       <tr>
                          <td><input type="button" name="uploadButton" onclick="upload();" 
                          			value="<fmt:message key="images.upload.submit" />"/></td>
                       </tr>
                    </table>
             </form>
          </div>
          <div class="ruler_green"><div><fmt:message key="images.upload.results" /></div></div>
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
        NodeManager manager = cloud.getNodeManager("images");
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
    <p><fmt:message key="images.upload.error"/></p>
<% } else if (numberOfUploadedNodes > 0) { %>
    <p><fmt:message key="images.upload.result">
           <fmt:param value="<%= numberOfUploadedNodes %>"/>
       </fmt:message>
    </p>
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

                <mm:listnodescontainer path="images" nodes="<%= uploadedNodes %>">
                    <mm:listnodes>

                    <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title"/>','<mm:image />');</mm:import>
                    <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
                       <td onclick="if(!blockSelect) {objClick(this);} blockSelect=false;">
                        <%-- use uploadedNodes and numberOfUploadedNodes in return url --%>
                        <a href="<mm:url page="../WizardInitAction.do">
                                     <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                     <mm:param name="returnurl" value="<%="../editors/resources/imageupload.jsp?uploadedNodes=" + uploadedNodes + "&numberOfUploadedNodes=" + numberOfUploadedNodes%>" />
                                 </mm:url>" onclick="blockSelect = true">
                              <img src="../gfx/icons/page_edit.png"/></a>
                          <a href="javascript:showInfo(<mm:field name="number" />)">
                              <img src="../gfx/icons/info.png" /></a>
                       </td>
                       <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
                       <td onMouseDown="objClick(this);"><mm:field name="filename"/></td>
                       <td onMouseDown="objClick(this);"><mm:field name="itype"/></td>
                       <td onMouseDown="objClick(this);"><mm:image template="s(100x100)" mode="img" /></td>
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