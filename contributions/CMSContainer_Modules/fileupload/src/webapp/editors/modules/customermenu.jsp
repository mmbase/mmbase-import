<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
	<%-- extra menu for the file upload --%>
	<li class="file-upload">
		<c:url value="/editors/fileupload/ListFiles.do" var="listFilesUrl" />
		<a href="${listFilesUrl}" target="rightpane">
			<%-- TODO: i18n --%>
			File upload
		</a>
	</li>
	<mm:hasrank minvalue="administrator">
		<li class="file-upload-settings">
			<c:url value="/editors/fileupload/settings.jsp" var="settingsUrl" />
			<a href="${settingsUrl}" target="rightpane">
				<%-- TODO: i18n --%>
				File upload settings
			</a>
		</li>
	</mm:hasrank>
</mm:content>
