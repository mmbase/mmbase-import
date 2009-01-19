<%@ include file="settings.jsp" %><%
    /**
     * upload.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: upload.jsp,v 1.1 2006-03-05 21:46:43 henk Exp $
     * @author   Kars Veling
     * @author   Pierre van Rooden
     * @author   Michiel Meeuwissen
     */

String did = request.getParameter("did");
if (did==null) {
    out.write("No valid parameters for the upload routines. Make sure to supply did field.");
    return;
}
%>

<html>
<head>
<title>Uploaden van bestanden</title>
<link rel="stylesheet" type="text/css" href="/editors/css/tree.css">
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
<%
    String wizard="";
    Object con=ewconfig.subObjects.peek();
    if (con instanceof Config.SubConfig) {
        wizard=((Config.SubConfig)con).wizard;
    }
%>
<div id="busy" style="visibility:hidden;position:absolute;width:100%;text-alignment:center;padding:3px;">
   <% // hh: translated hardcoded english prompts %>
   Een moment geduld alstublieft.<br /><br />U kunt het uploaden afbreken door <a href="#" onclick="closeIt(); return false;">hier</a> te klikken.</a>
</div>
<div id="form" style="padding:3px;">
    <form action="<mm:url page="processuploads.jsp" />?did=<%=did%>&proceed=true&popupid=<%=popupId%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizard%>&maxsize=<%=ewconfig.maxupload%>" enctype="multipart/form-data" method="POST">
        Gebruik de "Browse" knop om de file die u wilt uploaden te selecteren.<br/><br/>
        <input type="file" name="<%=did%>" onchange="upload();"></input><br /><br/>
        Als na het selecteren van de file het uploaden niet vanzelf begint kunt u op de "upload" knop klikken om
        het uploaden handmatig te starten.<br/><br/>
        <input type="button" onclick="upload();" value="upload"></input><br />
    </form>
</div>

</body>
</html>
