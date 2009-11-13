<%@ include file="settings.jsp" %>
<mm:content type="text/html" expires="0" language="en">
<%
    /**
     * upload.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id$
     * @author   Kars Veling
     * @author   Pierre van Rooden
     * @author   Michiel Meeuwissen
     */

String did = request.getParameter("did");
if (did==null) {
    out.write("No valid parameters for the upload routines. Make sure to supply did field.");
    return;
}

String maxsize = request.getParameter("maxsize");
if (maxsize == null || maxsize.length() == 0) {
	maxsize = String.valueOf(ewconfig.maxupload);
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
    }

    function closeIt() {
        window.close();
    }
</script>
<body>
<%
    String wizard="";
    Object con=ewconfig.subObjects.peek();
    if (con instanceof Config.SubConfig) {
        wizard=((Config.SubConfig)con).wizard;
    }
%>
<div id="form">
    <form action="<mm:url page="processuploads.jsp" />?did=<%=did%>&proceed=true&popupid=<%=popupId%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizard%>&maxsize=<%=maxsize%>" enctype="multipart/form-data" method="POST" >
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