<%@ include file="settings.jsp"
%><%@ page import="com.jspsmart.upload.*"
%><jsp:useBean id="mySmartUpload" scope="page" class="com.jspsmart.upload.SmartUpload"
/><%@ page import="java.io.ByteArrayOutputStream"
%><%@ page import="org.mmbase.applications.editwizard.*" %>
<html>
<body bgcolor="white">
<h1>Upload</h1>
<hr />
<mm:log jspvar="log">
<%
    /**
     * processuploads.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: processuploads.jsp,v 1.8 2002-10-31 09:07:50 pierre Exp $
     * @author   Kars Veling
     * @author   Pierre van Rooden
     * @author   Michiel Meeuwissen
     */

Config.WizardConfig wizardConfig = null;
if (ewconfig.subObjects.size() > 0) {
    if (ewconfig.subObjects.peek() instanceof Config.WizardConfig) {
        log.debug("checking configuration");
        wizardConfig = (Config.WizardConfig) ewconfig.subObjects.peek();
        Config.WizardConfig checkConfig = new Config.WizardConfig();
        log.trace("checkConfig" + configurator);
        configurator.config(checkConfig);
        if (checkConfig.objectNumber != null && (!checkConfig.objectNumber.equals(wizardConfig.objectNumber))) {
            log.debug("found wizard is for other other object (" + checkConfig.objectNumber + "!= " + wizardConfig.objectNumber + ")");
            wizardConfig = null;
        } else {
            log.debug("processing request");
            wizardConfig.wiz.processRequest(request);
        }
    }
}

    String did = request.getParameter("did");
    int maxsize = ewconfig.maxupload;
    try {
        maxsize = Integer.parseInt(request.getParameter("maxsize"));
    } catch (Exception e) {}

    // Initialization
    mySmartUpload.initialize(pageContext);

    mySmartUpload.setTotalMaxFileSize(maxsize);

    // Upload
    try {
        mySmartUpload.upload();

        Files flist = mySmartUpload.getFiles();

        for (int i=0; i<flist.getCount(); i++) {
            com.jspsmart.upload.File f = flist.getFile(i);
            String fieldname = f.getFieldName();
            if (!f.isMissing()) {
                // is uploaded!

                ByteArrayOutputStream bos = new ByteArrayOutputStream(f.getSize());
                for (int p=0; p<f.getSize(); p++) {
                    bos.write(f.getBinaryData(p));
                }

                byte[] buf = bos.toByteArray();
                wizardConfig.wiz.setBinary(fieldname, buf, f.getFileName(), f.getFilePathName());
            }

        }
        out.println("Uploaded files:"+flist.getCount());

        %>
            <script language="javascript">
                try { // Mac IE doesn't always support window.opener.
                    window.opener.doRefresh();
                    window.close();
                } catch (e) {}
            </script>
        <%
    } catch (java.lang.SecurityException e) {
  %>
      Uploaded file exceeds maximum file size of <%=maxsize%> bytes.<br />
      <a href="upload.jsp?proceed=true&did=<%=did%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizardConfig.wizard%>&maxsize=<%=ewconfig.maxupload%>">Try again</a> or
      <a href="javascript:window.close();">abandon upload</a>.
  <%
    } catch (Exception e) {
  %>
      An error ocurred while uploading this file (<%=e.toString()%>).<br />
      <a href="upload.jsp?proceed=true&did=<%=did%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizardConfig.wizard%>&maxsize=<%=ewconfig.maxupload%>">Try again</a> or
      <a href="javascript:window.close();">abandon upload</a>.
  <%
    }




%>
</mm:log>
</body>
</html>
