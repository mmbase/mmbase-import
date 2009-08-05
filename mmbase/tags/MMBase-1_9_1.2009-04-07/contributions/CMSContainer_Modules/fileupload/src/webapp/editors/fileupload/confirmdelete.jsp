<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	<html:html xhtml="true">
		<cmscedit:head title="Delete">
			<style type="text/css">
				input { width: 100px; }
				h1 { font-size: 13px; }
				h2 { font-size: 12px; }
				div.side_block_green div.body { display: block; }
			</style>
		</cmscedit:head>
		
		<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
			<body>
				<cmscedit:sideblock title="fileupload.confirm.title" titleClass="side_block_green">
					<mm:node number="${fileNode.number}" notfound="skip">
						<h1><mm:field name="title" /></h1>	
						
						<p><fmt:message key="fileupload.confirm.intro" /></p>
						
						<mm:countrelations type="contentelement" role="posrel" searchdir="source" jspvar="ceCount" write="false" />
						
						<c:if test="${ ceCount gt 0 }">
							<p>
								<fmt:message key="fileupload.confirm.related" />
							</p>
						
							<mm:relatednodes type="contentelement" role="posrel" searchdir="source">
								<mm:first>
									<h2><fmt:message key="fileupload.confirm.contentelements" /></h2>
									<ul>
								</mm:first>
								<li><mm:field name="title" /></li>
								<mm:last></ul></mm:last>	
							</mm:relatednodes>	
						</c:if>
					</mm:node>
					
					<form action="?">
						<html:hidden property="id" value="${fileNode.number}" />
						<html:submit property="remove"><fmt:message key="fileupload.confirm.button.remove" /></html:submit>
						&nbsp;
						<html:submit property="cancel"><fmt:message key="fileupload.confirm.button.cancel" /></html:submit>
					</form>
				</cmscedit:sideblock>
			</body>
		</mm:cloud>
	</html:html>
</mm:content>