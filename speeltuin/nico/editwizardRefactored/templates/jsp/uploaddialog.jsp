<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%
String language = (String)request.getAttribute("language");
String did = (String)request.getAttribute("did");
String wizardname = (String)request.getAttribute("wizardname");
String sessionkey = (String)request.getAttribute("sessionkey");
String popupid = (String)request.getAttribute("popupid");
String maxsize = (String)request.getAttribute("maxsize");
%><mm:content type="text/html" expires="0" language="<%=language%>">
<html>
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

  function closeIt() {
    window.close();
  }
</script>
<body>
<div id="form">
    <form action="<mm:url page="processuploads.jsp" />?did=<%=did%>&proceed=true&popupid=<%=popupid%>&sessionkey=<%=sessionkey%>&wizard=<%=wizardname%>&maxsize=<%=maxsize%>" enctype="multipart/form-data" method="POST" >
        <input type="file" name="<%=did%>" onchange="upload();"></input><br />
        <input type="button" onclick="upload();" value="upload"></input><br />
    </form>
</div>
<div id="busy" style="visibility:hidden;position:absolute;width:100%;text-alignment:center;">
    uploading... Please wait.<br /><br />Or click <a href="#" onclick="closeIt(); return false;">here</a> to cancel upload.</a>
</div>
</body>
</html>
</mm:content>