<%@ include file="settings.jsp"
%><%@ page language="java" import="com.jspsmart.upload.*"
%><jsp:useBean id="mySmartUpload" scope="page" class="com.jspsmart.upload.SmartUpload"
/><%@ page import="java.io.ByteArrayOutputStream"
%><%@ page import="org.mmbase.applications.editwizard.*" %>
<html>
<body bgcolor="white">
<h1>Upload</h1>
<hr />
<%

	// find editwizard in session
	String wizardinstance = request.getParameter("wizard");
	String wizardname = wizardinstance;
	Wizard wiz = null;
	
	if (wizardinstance!=null && !wizardinstance.equals("")) {
		// ok.
		int pos = wizardinstance.indexOf("|");
		if (pos>-1) wizardname = wizardinstance.substring(0, pos);
		wiz = (Wizard)session.getValue("Wizard_"+wizardinstance);
	}
	
	if (wiz==null) {
		%>
			Correct Wizardinstance not found in current session. Make sure you wizard param is set correctly. Maybe the server was restarted?
			
		<%
		return;
	}

	int maxsize = settings_maxupload;
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
			File f = flist.getFile(i);
			String fieldname = f.getFieldName();
			if (!f.isMissing()) {
				// is uploaded!

				ByteArrayOutputStream bos = new ByteArrayOutputStream(f.getSize());
				for (int p=0; p<f.getSize(); p++) {
					bos.write(f.getBinaryData(p));
				}

				byte[] buf = bos.toByteArray();
				wiz.setUpload(fieldname, buf, f.getFileName(), f.getFilePathName());
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
</body>
</html>
