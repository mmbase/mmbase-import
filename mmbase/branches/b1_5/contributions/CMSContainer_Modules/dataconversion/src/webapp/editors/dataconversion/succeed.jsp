<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.util.http.BulkUploadUtil,com.finalist.cmsc.upload.service.OODocUploadUtil"%>


<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="odtupload.title">
<script language="javascript">
 	var XMLHttpReq;
 	function createXMLHttpRequest() {
	
		if(window.XMLHttpRequest) { 
			XMLHttpReq = new XMLHttpRequest();
		}
		else if (window.ActiveXObject) { 
			try {
				XMLHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				try {
					XMLHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
				} catch (e) {}
			}
		}
	}
	//发送请求
	function sendRequest() {
		createXMLHttpRequest();
        var url = "/mylist/WorkScheduleServlet";
		XMLHttpReq.open("GET", url, true);
		XMLHttpReq.onreadystatechange = processResponse;
		XMLHttpReq.send(null); 
	}
	// 响应
    function processResponse() {

    	if (XMLHttpReq.readyState == 4) { 
        	if (XMLHttpReq.status == 200) { 
				DisplayHot();
				setTimeout("sendRequest()", 1000*60);
            } else { 
              //  window.alert("request error!");
            }
        }
    }
    function DisplayHot() {
	
	    var count = XMLHttpReq.responseXML.getElementsByTagName("count")[0].firstChild.nodeValue;
	    var current = XMLHttpReq.responseXML.getElementsByTagName("current")[0].firstChild.nodeValue;
		var id;
		var title;
		if(XMLHttpReq.responseXML.getElementsByTagName("id")[0].firstChild != null)
	     id = XMLHttpReq.responseXML.getElementsByTagName("id")[0].firstChild.nodeValue;
		else
			id = "";
		if(XMLHttpReq.responseXML.getElementsByTagName("title")[0].firstChild != null)
	     title = XMLHttpReq.responseXML.getElementsByTagName("title")[0].firstChild.nodeValue;
		else
			title = "";

	
		document.getElementById("form1:title").innerHTML = title;	
		document.getElementById("form1:count").innerHTML = count;
		document.getElementById("form1:iid").value = id;
		document.getElementById("form1:current").value = current;
		document.getElementById("form1:size").value = count;
		if(count <= 0)
		{
			closeWin();
			document.getElementById("form1:iknow").style.visibility = 'hidden';
			document.getElementById("form1:next").style.visibility = 'hidden';
		}
		else	if(count == 1)
		{
			document.getElementById("form1:iknow").style.visibility = 'visible';
			document.getElementById("form1:next").style.visibility = 'hidden';
		}
		else
		{
			document.getElementById("form1:iknow").style.visibility = 'visible';
			document.getElementById("form1:next").style.visibility = 'visible';
		}
	}

</script>
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
                  <a href="#"><fmt:message key="odtupload.title" /></a>
               </div>
            </div>
         </div>
      </div>
      
      <div class="editor" style="height:500px">
      <div class="body">
          <form action="../upload/OdtUpload.do" enctype="multipart/form-data" method="post"-->
          	    		
          		<input type="hidden" name="channel" value="${parent}"/>
          	
                <table border="0">
                   <tr>
                      <td><fmt:message key="odtupload.explanation" /></td>
                   </tr>
                   <tr>

                      <td><input type="file" name="odtfile"/>  
					</td>

                   </tr>
                   <tr>
                      <td><input type="button" name="uploadButton" onclick="upload();" 
                      			value="<fmt:message key="attachments.upload.submit" />"/></td>
                   </tr>
                </table>
         </form>
      </div>
      <div class="ruler_green"><div><fmt:message key="odtupload.attachmentlist" /></div></div>
      <div class="body">
	    <c:if test="${odtDoc == false}">
		<span >The file must be OpenOffice odt type!</span>
					</c:if>
        <div id="busy">
            <fmt:message key="uploading.message.wait"/><br />
        </div>
<c:if test="${not empty binaries}">

<table width="100%" border="0">

<tr><td></td><td ><fmt:message key="odtupload.file.title" /></td></tr>
<tbody class="hover">
<c:forEach var="entity" items="${binaries}" varStatus="status">
<c:choose><c:when test="${status.count % 2 != 0}">
<tr class="swap"></c:when>
<c:otherwise><tr></c:otherwise>
</c:choose>
<td ><a href="DeleteOdt.do?parent=${parent}&name=${entity.title}"><img src="../gfx/icons/delete.png" width="16" height="16" title="delete" alt="delete"/></a></td><td><c:out value="${entity.title}"/></td>

</tr>
</c:otherwise>
</c:choose>

</c:forEach>
</tbody>
</table>

<form action="../upload/OdtStore.do">

<p><input type="submit" value="<fmt:message key="odtupload.file.save" />"/>
	<input type="hidden" name="parent" value="${parent}"/>

</form>

</c:if>
</div>
</div>

</mm:cloud>


</body>
</html:html>
</mm:content>