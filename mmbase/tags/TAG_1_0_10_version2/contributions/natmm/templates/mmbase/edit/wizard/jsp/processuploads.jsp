<%@ include file="settings.jsp"
%><%@ page import="org.apache.commons.fileupload.*"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%@ page import="org.mmbase.applications.editwizard.Config" %>
<html>
<head>
<title>Uploaden van bestanden</title>
<link rel="stylesheet" type="text/css" href="/editors/css/tree.css">
</head>
<body style="padding:3px;">
<h3>Upload</h3>
<hr />
<mm:log jspvar="log">
<%
    /**
     * processuploads.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: processuploads.jsp,v 1.5 2008-02-11 15:40:32 nklasens Exp $
     * @author   Kars Veling
     * @author   Pierre van Rooden
     * @author   Michiel Meeuwissen
     */

Config.WizardConfig wizardConfig = null;

if (! ewconfig.subObjects.empty()) {
    Config.SubConfig top  = (Config.SubConfig) ewconfig.subObjects.peek();
    if (! popup) {
        if (top instanceof Config.WizardConfig) {
            log.debug("no popup");
            wizardConfig = (Config.WizardConfig) top;
        }
    } else {
        Stack stack = (Stack) top.popups.get(popupId);
        if (stack != null) {
           log.debug("popup");
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
        ArrayList alAllowedTypes = new ArrayList();
        alAllowedTypes.add("image/gif");
        alAllowedTypes.add("image/pjpeg");
        alAllowedTypes.add("image/jpeg");
        alAllowedTypes.add("image/x-png");
        alAllowedTypes.add("image/tiff");
        for (Iterator i = fileItems.iterator(); i.hasNext(); ) {
            FileItem fi = (FileItem)i.next();
            if (!fi.isFormField()) {
                String fullFileName = fi.getName();
                String fileName = fullFileName;
                // the path passed is in the cleint system's format,
                // so test all known path separator chars ('/', '\' and "::" )
                // and pick the one which would create the smallest filename
                // Using Math is rather ugly but at least it is shorter and performs better
                // than Stringtokenizer, regexp, or sorting collections
                int last = Math.max(Math.max(
                    fullFileName.lastIndexOf(':'), // old mac path (::)
                    fullFileName.lastIndexOf('/')),  // unix path
                    fullFileName.lastIndexOf('\\')); // windows path
                if (last > -1) {
                    fileName = fullFileName.substring(last+1);
                }
                if (fi.get().length > 0) { // no need uploading nothing
                  if (log.isDebugEnabled()) {
                     log.debug("Setting binary " + fi.get() + " " + fi.get().length + " " +  fileName + " " + fullFileName);
                  }
                  String wizard = request.getParameter("wizard");
                  if(wizard!=null && wizard.indexOf("images") > -1) {
                     if (!alAllowedTypes.contains(fi.getContentType()))
                     {
                        throw new Exception("Unsupported type");
                     }
                  }
                  wizardConfig.wiz.setBinary(fi.getFieldName(), fi.get(), fileName, fullFileName, fi.getContentType());
						fileCount++;
                } 
            }
        }
        out.println("Uploaded files:" + fileCount);
        %>
            <script language="javascript">
                try { // Mac IE doesn't always support window.opener.
                    window.opener.doRefresh();
                    window.close();
                } catch (e) {}
            </script>
        <%
    } catch (FileUploadBase.SizeLimitExceededException e) {
      // hh: translated hardcoded english prompts
      long lMaxSizeInMB = maxsize / (1024*1024);    
  %>    
      De file die u wilt uploaden is groter dan <%= lMaxSizeInMB %> MB.<br />
      <a href="<mm:url page="upload.jsp" />?proceed=true&did=<%=did%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizardConfig.wizard%>&maxsize=<%=ewconfig.maxupload%>">Probeer het nogmaals</a> of
      <a href="javascript:window.close();">stop met uploaden</a>.
      
<%  } catch (FileUploadException e) {
      // hh: translated hardcoded english prompts
  %>
      Er is een fout opgetreden bij het uploaden van (<%=e.toString()%>).<br />
      <a href="<mm:url page="upload.jsp" />?proceed=true&did=<%=did%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizardConfig.wizard%>&maxsize=<%=ewconfig.maxupload%>">Progbeer het nogmaals</a> of
      <a href="javascript:window.close();">stop met uploaden</a>.

<%  } catch (Exception e) {

%>
      Afbeeldingen kunnen alleen van het formaat gif, jpeg, png of tiff zijn. De file die u wilt uploaden is niet van dit formaat.<br />
      <a href="<mm:url page="upload.jsp" />?proceed=true&did=<%=did%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizardConfig.wizard%>&maxsize=<%=ewconfig.maxupload%>">Probeer het nogmaals</a> of
      <a href="javascript:window.close();">stop met uploaden</a>.

<%  }

%>
</mm:log>
</body>
</html>
