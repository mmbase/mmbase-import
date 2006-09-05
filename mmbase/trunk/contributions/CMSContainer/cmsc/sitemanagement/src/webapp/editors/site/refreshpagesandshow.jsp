<%@page language="java" contentType="text/html;charset=utf-8" import="com.finalist.tree.*" %>
<%@include file="globals.jsp"  %>
<%@page import="com.finalist.cmsc.navigation.*" %>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
	<mm:import externid="showpage" jspvar="newpage" />
	<mm:node referid="showpage" jspvar="node">
		<mm:import id="pagepath">../../<%= NavigationUtil.getPathToRootString(node, !ServerUtil.useServerName()) %></mm:import>
	</mm:node>
</mm:cloud>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="pagerefresh.title" /></title>
		<link href="../css/main.css" type="text/css" rel="stylesheet" />
		<script type="text/javascript" src="../utils/window.js"></script>
		<script type="text/javascript">
			function refreshPages() {
				refreshFrame('pages');
				if (window.opener) {
					window.close();
				}
				document.location.href = '<mm:write referid="pagepath"/>';
			}
		</script>
	</head>
	<body onload="refreshPages()"></body>
</html:html>