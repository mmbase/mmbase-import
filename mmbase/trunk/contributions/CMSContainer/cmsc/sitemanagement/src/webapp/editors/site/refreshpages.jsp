<%@page language="java" contentType="text/html;charset=utf-8" import="com.finalist.tree.*" %>
<%@include file="globals.jsp"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<cmscedit:head title="pagerefresh.title">
		<script type="text/javascript">
			function refreshPages() {
				refreshFrame('pages');
				if (window.opener) {
					window.close();
				}
			}
		</script>
	</cmscedit:head>
	<body onload="refreshPages()"></body>
</html:html>