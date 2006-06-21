<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@page import="com.finalist.cmsc.navigation.*"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
	<title><fmt:message key="selector.title" /></title>
	<link href="../../style.css" type="text/css" rel="stylesheet" />
	<link href="../../utils/ajaxtree/addressbar.css" type="text/css" rel="stylesheet" />

	<link href="../../utils/ajaxtree/ajaxtree.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="../../js/prototype.js"></script>
	<script type="text/javascript" src="../../js/scriptaculous/scriptaculous.js"></script>
	<script type="text/javascript" src="../../utils/ajaxtree/ajaxtree.js"></script>
	<script type="text/javascript" src="../../utils/ajaxtree/addressbar.js"></script>

	<script type="text/javascript">
		ajaxTreeConfig.resources = '../../utils/ajaxtree/images/';
		ajaxTreeConfig.url = 'SelectorPage.do';
		ajaxTreeConfig.addressbarId = 'addressbar';
	</script>
	<script type="text/javascript">
		function selectItem(page, path) {
			opener.selectPage(page, path);
			close();
		}
	</script>

	<style type="text/css">
		.tooltip {
			position: absolute;
			display: none;
			z-index: 1000;
			left: 0px;
			top: 0px;
		}
		.width80 {
			width: 80%
		}
	</style>
	</head>
	<body style="overflow: auto">
	<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
		<mm:import externid="channel" from="request" />
		<mm:compare referid="channel" value="" inverse="true">
			<mm:node number="${channel}" jspvar="pageNode" notfound="skip">
				<mm:import id="pagepath">
					<mm:field name="path" />
				</mm:import>
			</mm:node>
		</mm:compare>
		<mm:compare referid="channel" value="">
			<mm:import id="pagepath"></mm:import>
		</mm:compare>

		<form action="SelectorPage.do">
			<input type="text" name="path" value="${pagePath}" id="addressbar" class="width80" />
			<input type="submit" class="button" value=" <fmt:message key="selector.search" /> " />
		</html>
		<div id="addressbar_choices" class="addressbar"></div>
		<script type="text/javascript">
			new AddressBar("addressbar", 
				"addressbar_choices", 
				ajaxTreeConfig.url + "?action=autocomplete",
				{paramName: "path" });
		</script>

		<%for (NodeIterator iter = SiteUtil.getSites(cloud).nodeIterator(); iter.hasNext();) {
            Node site = iter.nextNode();
		%>
		<script type="text/javascript">
			ajaxTreeLoader.initTree('<%= site.getNumber() %>', 'tree<%= site.getNumber() %>');
		</script>
		<div style="float: left" id="tree<%= site.getNumber() %>">Site loading</div>
		<% } %>
	</mm:cloud>
	</body>
</html:html>
</mm:content>