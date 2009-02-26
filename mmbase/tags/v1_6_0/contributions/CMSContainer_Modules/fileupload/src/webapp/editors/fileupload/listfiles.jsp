<%@ page contentType="text/html; charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ include file="globals.jsp" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="fileupload.list.title">
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
							<a href="#"><fmt:message key="fileupload.list.title" /></a>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="editor">
			<div class="ruler_green"><div><fmt:message key="fileupload.list.upload"/></div></div>
			
			<div class="cmsc-form">
				<logic:messagesPresent property="org.apache.struts.action.GLOBAL_MESSAGE">
					<ul class="global-errors">
						<html:messages id="errors" bundle="MODULES-FILEUPLOAD" property="org.apache.struts.action.GLOBAL_MESSAGE">
							<li>${errors}</li>
						</html:messages>
					</ul>
				</logic:messagesPresent>
				
				<logic:messagesPresent message="true" property="org.apache.struts.action.GLOBAL_MESSAGE">
					<ul class="global-messages">
						<html:messages id="message" message="true" bundle="MODULES-FILEUPLOAD" property="org.apache.struts.action.GLOBAL_MESSAGE">
							<li>${message}</li>
						</html:messages>
					</ul>
				</logic:messagesPresent>
				
				<html:form action="/editors/fileupload/UploadFile.do" method="post" enctype="multipart/form-data">
					<label><fmt:message key="fileupload.upload.label.title" /></label>
					<html:text property="title" />
					<span class="error"><html:errors property="title" bundle="MODULES-FILEUPLOAD" /></span>
					<br style="clear: both;" />
					
					<label><fmt:message key="fileupload.upload.label.description" /></label>
					<html:textarea property="description" rows="5" cols="40" />
					<span class="error"><html:errors property="description" bundle="MODULES-FILEUPLOAD" /></span>
					<br style="clear: both;" />
					
					<label><fmt:message key="fileupload.upload.label.source" /></label>
					<html:file property="file" />
					<span class="error"><html:errors property="file" bundle="MODULES-FILEUPLOAD" /></span>
					<br style="clear: both;" />
					
					<button class="button" type="submit"><fmt:message key="fileupload.upload.submit" /></button><br />
				</html:form>
			</div>
		
			<div class="ruler_green"><div><fmt:message key="fileupload.list.filter"/></div></div>
			
			<div class="cmsc-form">

				<html:form action="/editors/fileupload/ListFiles.do" method="post">
					<label><fmt:message key="fileupload.filter.label.title" /></label>
					<html:text property="searchTitle" />
					<span class="error"><html:errors property="searchTitle" bundle="MODULES-FILEUPLOAD" /></span>
					<br style="clear: both;" />
					<label><fmt:message key="fileupload.filter.label.filename" /></label>
					<html:text property="searchFilename" />
					<span class="error"><html:errors property="searchFilename" bundle="MODULES-FILEUPLOAD" /></span>
					<br style="clear: both;" />

					<button class="button" type="submit"><fmt:message key="fileupload.filter.submit" /></button><br />
				</html:form>
				
			</div>

			<div class="ruler_green"><div><fmt:message key="fileupload.list.title"/></div></div>
			<div class="body">
				
				<c:choose>
					<c:when test="${fn:length(results) gt 0}">
					<c:set var="listSize" value="${listFilesForm.resultCount}"/>
					<c:set var="resultsPerPage" value="${listFilesForm.resultsPerPage}" />
					<c:set var="extraparams" value="&searchTitle=${listFilesForm.searchTitle}&searchFilename=${listFilesForm.searchFilename}"/>
					<%@ include file="../pages.jsp" %>
					
						<table>
							<thead>
								 <tr>
									  <th></th>
									  <th><fmt:message key="fileupload.list.header.title" /></th>
									  <th><fmt:message key="fileupload.list.header.creationdate" /></th>
									  <th><fmt:message key="fileupload.list.header.filename" /></th>
								 </tr>
							</thead>
							<tbody class="hover">
								<mm:listnodes referid="results">
									<tr <mm:odd>class="swap"</mm:odd>>
										<td style="white-space: nowrap;">
											<%-- edit --%>
											<fmt:message var="editText" key="fileupload.list.action.edit" />
											<c:url value="/editors/fileupload/EditFile.do" var="editUrl">
												<c:param name="id" value="${_node.number}" />
											</c:url>
											<a href="${editUrl}" title="${fn:escapeXml(editText)}">
												<img 
													src="../gfx/icons/edit.png" 
													width="16" 
													height="16" 
													title="${fn:escapeXml(editText)}"
													alt="${fn:escapeXml(editText)}"
												/>
											</a>
											<%-- delete --%>
											<fmt:message var="deleteText" key="fileupload.list.action.delete" />
											<c:url value="/editors/fileupload/DeleteFile.do" var="deleteUrl">
												<c:param name="id" value="${_node.number}" />
											</c:url>
											<a href="${deleteUrl}" title="${fn:escapeXml(deleteText)}">
												<img 
													src="../gfx/icons/delete.png" 
													width="16" 
													height="16" 
													title="${fn:escapeXml(deleteText)}"
													alt="${fn:escapeXml(deleteText)}"
												/>
											</a>
										</td>
										
										<td>${_node.title}</td>
										<td><fmt:formatDate value="${_node.creationdate}" pattern="d MMM yyyy HH:mm" /></td>
										<td><c:out value="${_node.filename}" /></td>
									</tr>
								</mm:listnodes>
							</tbody>
						</table>
					</c:when>
					<c:otherwise>
						<fmt:message key="fileupload.list.noresults" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
	</mm:cloud>
</body>
</html:html>
</mm:content>
