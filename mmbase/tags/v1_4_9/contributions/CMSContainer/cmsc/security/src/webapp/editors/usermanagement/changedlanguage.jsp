<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@include file="globals.jsp"  %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
<cmscedit:head title="changelanguage.title">
	<script>
		function messageAndRefresh() {
			top.location.href = "../index.jsp";
		}
	</script>
</cmscedit:head>
<body onload="messageAndRefresh()">
</body>
</html:html>
</mm:content>