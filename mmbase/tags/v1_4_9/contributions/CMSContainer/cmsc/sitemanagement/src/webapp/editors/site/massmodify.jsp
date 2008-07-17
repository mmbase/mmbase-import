<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="massmodify.title"/>
<body>
<cmscedit:contentblock title="massmodify.title" titleClass="content_block_pink">
	<html:form action="/editors/site/MassModify">
		<html:hidden property="number"/>
		<p>
			<html:select property="requiredLayout">
				<html:option value="-1"> - </html:option>
				<html:optionsCollection name="layoutList" value="value" label="label"/>
			</html:select>
			<html:select property="newLayout">
				<html:option value="-1"> - </html:option>
				<html:optionsCollection name="layoutList" value="value" label="label"/>
			</html:select>
	`		<html:checkbox property="linkPortlets" />
		</p>
		<p>
		   	<html:cancel><fmt:message key="massmodify.cancel"/></html:cancel>
		   	<html:submit property="modify"><fmt:message key="massmodify.submit"/></html:submit>&nbsp;
	   	</p>
	</html:form>
</cmscedit:contentblock>
</body>
</html:html>
</mm:content>