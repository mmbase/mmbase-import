<%@ include file="settings.jsp"
%><%@ page language="java" import="com.jspsmart.upload.*"
%><jsp:useBean id="mySmartUpload" scope="page" class="com.jspsmart.upload.SmartUpload"
/><%@ page import="java.io.ByteArrayOutputStream"
%><%@ page import="org.mmbase.applications.editwizard.*" %>
<html>
<body bgcolor="white">
<h1>Upload</h1>
<hr />
<mm:log jspvar="log">
<%

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

	int maxsize = ewconfig.maxupload;
	try {
		maxsize = Integer.parseInt(request.getParameter("maxsize"));
	} catch (Exception e) {}

	// Initialization
	mySmartUpload.initialize(pageContext);

	mySmartUpload.setTotalMaxFileSize(maxsize);

	// Upload	
	mySmartUpload.upload();

	try {
	
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
				wizardConfig.wiz.setUpload(fieldname, buf, f.getFileName(), f.getFilePathName());
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
	} catch (Exception e) { 
		out.println(e.toString());
	}
	
	
	
%>
</mm:log>
</body>
</html>
