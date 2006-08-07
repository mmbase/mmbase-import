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
<link href="../css/main.css" type="text/css" rel="stylesheet" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload images</title>
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
</head>
<body>
<mm:cloud jspvar="cloud" >
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
                      <td><input type="file" name="zipfile"></input></td>
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
    if ("post".equalsIgnoreCase(request.getMethod())) {
        NodeManager manager = cloud.getNodeManager("images");
        int count = BulkUploadUtil.uploadAndStore(manager, request);
%>
        <table border="0">
            <tr>
                <td>
                    <% if (count == 0) { %>
                        <fmt:message key="images.upload.error"/>
                    <% } else { %>
                        <fmt:message key="images.upload.result">
                            <fmt:param value="<%= count %>"/>
                        </fmt:message>
                    <% } %>
                </td>
            </tr>
        </table>
      </div>
<%
    }
%>
</div>
</mm:cloud>
</body>
</html>