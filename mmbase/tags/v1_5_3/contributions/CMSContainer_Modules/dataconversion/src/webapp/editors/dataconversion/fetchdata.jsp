<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<c:url var="actionUrl" value="/editors/dataconversion/FetchData.do?id=${uuid}"/>
<cmscedit:head title="dataconversion.title">
<script language="javascript">
 	var XMLHttpReq;
   var flag = false;
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
	//send request 
	function sendRequest() {
		createXMLHttpRequest();
      var url = "${actionUrl}";
		XMLHttpReq.open("GET", url, true);
		XMLHttpReq.onreadystatechange = processResponse;
		XMLHttpReq.send(null); 
	}
	//deal with the response 
    function processResponse() {
    	if (XMLHttpReq.readyState == 4) { 
        	if (XMLHttpReq.status == 200) { 
				DisplayHot();
            if(!flag) {
				   setTimeout("sendRequest()", 1000*60);
            }
         } 
         else { 
                window.alert("request error!");
         }
        }
    }
    function DisplayHot() {	
	    var count = XMLHttpReq.responseXML.getElementsByTagName("signal")[0].firstChild.nodeValue;
       if(count != null && count != " ") {
		    document.getElementById("messgage").innerHTML = "<fmt:message key="dataconversion.success" />";
          flag = true;
       }
	}
</script>
</cmscedit:head>
<body onload="sendRequest()">
<mm:cloud jspvar="cloud" >
      <div class="tabs">
         <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#" ><fmt:message key="dataconversion.title" /></a>
               </div>
            </div>
         </div>
      </div> 
   <div class="editor" style="height:500px">
   <div style="height:100px"></div>
      <div id="messgage" align="center">
           <fmt:message key="dataconversion.importing" />
      </div>
   </div>
</mm:cloud>
</body>
</html:html>
</mm:content>