<%@ include file="settings.jsp"
%><%@ page import="org.apache.commons.fileupload.*"
%><%@ page import="org.mmbase.applications.editwizard.*" %>
%><%@ page import="org.mmbase.applications.editwizard.Config" %>
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
     * @version  $Id: processuploads.jsp,v 1.13 2004-01-26 10:08:10 pierre Exp $
     * @author   Kars Veling
     * @author   Pierre van Rooden
     * @author   Michiel Meeuwissen
     */

Config.WizardConfig wizardConfig = null;

if (! ewconfig.subObjects.empty()) {
    Config.SubConfig top  = (Config.SubConfig) ewconfig.subObjects.peek();
    if (! popup) {
        if (top instanceof Config.WizardConfig) {
            log.info("no popup");
            wizardConfig = (Config.WizardConfig) top;
        }
    } else {
        Stack stack = (Stack) top.popups.get(popupId);
        if (stack != null) {
            log.info("popup");
           wizardConfig = (Config.WizardConfig) stack.peek();
        }
    }
    if (wizardConfig!=null) {
        Config.WizardConfig checkConfig = new Config.WizardConfig();
        log.trace("checkConfig" + configurator);
        configurator.config(checkConfig);
        if (checkConfig.objectNumber != null && (!checkConfig.objectNumber.equals(wizardConfig.objectNumber))) {
            log.info("found wizard is for other other object (" + checkConfig.objectNumber + "!= " + wizardConfig.objectNumber + ")");
            wizardConfig = null;
        } else {
            log.info("processing request");
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
    DiskFileUpload fu = new DiskFileUpload();
    // maximum size before a FileUploadException will be thrown
    fu.setSizeMax(maxsize);

    // maximum size that will be stored in memory --- what shoudl this be?
    // fu.setSizeThreshold(maxsize);

    // the location for saving data that is larger than getSizeThreshold()
    // where to store?
    // fu.setRepositoryPath("/tmp");

    // Upload
    try {
        List fileItems = fu.parseRequest(request);
        int fileCount = 0;
        for (Iterator i = fileItems.iterator(); i.hasNext(); ) {
            FileItem fi = (FileItem)i.next();
            if (!fi.isFormField()) {
                String fullFileName = fi.getName();
                String fileName = fullFileName;
                // the path passed is in the cleint system's format,
                // so test both path separator chars ('/' and '\')
                int last = fullFileName.lastIndexOf("/");
                if (last==-1) last = fullFileName.lastIndexOf("\\");
                if (last>0) {
                    fileName = fullFileName.substring(last+1);
                }
                wizardConfig.wiz.setBinary(fi.getFieldName(), fi.get(), fileName, fullFileName);
                fileCount++;
            }
        }
        out.println("Uploaded files:"+fileCount);
        %>
            <script language="javascript">
                try { // Mac IE doesn't always support window.opener.
                    window.opener.doRefresh();
                    window.close();
                } catch (e) {}
            </script>
        <%
    } catch (FileUploadBase.SizeLimitExceededException e) {
  %>
      Uploaded file exceeds maximum file size of <%=maxsize%> bytes.<br />
      <a href="<mm:url page="upload.jsp" />?proceed=true&did=<%=did%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizardConfig.wizard%>&maxsize=<%=ewconfig.maxupload%>">Try again</a> or
      <a href="javascript:window.close();">abandon upload</a>.
  <%
    } catch (FileUploadException e) {
  %>
      An error ocurred while uploading this file (<%=e.toString()%>).<br />
      <a href="<mm:url page="upload.jsp" />?proceed=true&did=<%=did%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizardConfig.wizard%>&maxsize=<%=ewconfig.maxupload%>">Try again</a> or
      <a href="javascript:window.close();">abandon upload</a>.
  <%
    }




%>
</mm:log>
</body>
</html>
