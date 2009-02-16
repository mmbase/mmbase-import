<%@include file="globals.jsp" %>
<html>
<cmscedit:head title="Footer" titleMode="plain">
<script type="text/javascript">

function loadXMLDoc(url) {
    req = false;
    // branch for native XMLHttpRequest object
    if(window.XMLHttpRequest && !(window.ActiveXObject)) {
        try {
            req = new XMLHttpRequest();
        } catch(e) {
            req = false;
        }
    // branch for IE/Windows ActiveX version
    } else if(window.ActiveXObject) {
        try {
            req = new ActiveXObject("Msxml2.XMLHTTP");
        } catch(e) {
            try {
                req = new ActiveXObject("Microsoft.XMLHTTP");
            } catch(e) {
                req = false;
            }
        }
    }
    if(req) {
        req.open("GET", url, true);
        req.send("");
    }
}

function heartbeat() {
    loadXMLDoc("../mmbase/edit/wizard/jsp/heartbeat.jsp");
    setTimeout('heartbeat()',60*1000*25);
}

</script>
</cmscedit:head>
<body onLoad="javascript:heartbeat();">
   <div id="footer">
      <fmt:message key="createdby" />
      <div style="float:right">&nbsp;</div>      
      <br/>
      <fmt:message key="version" /> <cmsc:version/>
   </div>
</body>
</html>
