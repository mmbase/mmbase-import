<%@ page contentType="text/html; charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ include file="globals.jsp" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="fileupload.error.title">
	<script src="<cmsc:staticurl page='/editors/repository/content.js'/>" type="text/javascript"></script>
	<script src="<cmsc:staticurl page='/editors/repository/search.js'/>" type="text/javascript"></script>
	<link href="<cmsc:staticurl page='/editors/fileupload/fileupload.css' />" type="text/css" rel="stylesheet" />
</cmscedit:head>
<body>
	<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
	
		<div class="content">
			<div class="tabs">
				<div class="tab_active">
					<div class="body">
						<div>
							<a href="#"><fmt:message key="fileupload.error.title" /></a>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="editor">
			<logic:messagesPresent property="org.apache.struts.action.GLOBAL_MESSAGE">
				<ul class="global-errors">
					<html:messages id="errors" bundle="MODULES-FILEUPLOAD" property="org.apache.struts.action.GLOBAL_MESSAGE">
						<li>${errors}</li>
					</html:messages>
				</ul>
			</logic:messagesPresent>
		</div>
		
	</mm:cloud>
</body>
</html:html>
</mm:content>