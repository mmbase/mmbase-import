<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="profile.title" />
<body>
<mm:cloud jspvar="cloud" loginpage="../login.jsp">
	<cmscedit:sideblock title="profile.title">
		<ul class="shortcuts">
			<li class="password">
				<a href="changepassword.jsp" target="rightpane"><fmt:message key="changepassword.title" /></a>
			</li>
			<li class="language">
				<a href="changelanguage.jsp" target="rightpane"><fmt:message key="changelanguage.title" /></a>
			</li>
		</ul>
	</cmscedit:sideblock>			
</mm:cloud>
</body>
</html:html>
</mm:content>