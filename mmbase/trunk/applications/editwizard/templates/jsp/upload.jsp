<%@ include file="settings.jsp" %><%

String did = request.getParameter("did");
if (did==null) {
    out.write("No valid parameters for the upload routines. Make sure to supply did field.");
    return;
}
%>

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

//		document.body.innerHTML='uploading... Please wait.<br /><br />Or click <a href="#" onclick="closeIt(); return false;">here</a> to cancel upload.</a>';
	}
	
	function closeIt() {
		window.close();
	}
</script>
<body>
<div id="form">
	<form action="processuploads.jsp?popup=true&wizard=<%=ewconfig.wizard%>&maxsize=<%=ewconfig.maxupload%>" enctype="multipart/form-data" method="POST" >
		<input type="file" name="<%=did%>" onchange="upload();"></input><br />
		<input type="button" onclick="upload();" value="upload"></input><br />
	</form>
</div>
<div id="busy" style="visibility:hidden;position:absolute;width:100%;text-alignment:center;">
	uploading... Please wait.<br /><br />Or click <a href="#" onclick="closeIt(); return false;">here</a> to cancel upload.</a>
</div>
</body>
</html>
