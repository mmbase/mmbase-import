<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
	<title><fmt:message key="selector.title" /></title>
	<link href="../style.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="ccp.js"></script>
	<script type="text/javascript" src="../utils/cookies.js"></script>

	<link href="../utils/ajaxtree/ajaxtree.css" type="text/css" rel="stylesheet" />
	<link href="../utils/ajaxtree/addressbar.css" type="text/css" rel="stylesheet" />

	<script type="text/javascript" src="../js/prototype.js"></script>
	<script type="text/javascript" src="../js/scriptaculous/scriptaculous.js"></script>
	<script type="text/javascript" src="../utils/ajaxtree/ajaxtree.js"></script>
	<script type="text/javascript" src="../utils/ajaxtree/addressbar.js"></script>

	<script type="text/javascript">
		ajaxTreeConfig.resources = '../utils/ajaxtree/images/';
		ajaxTreeConfig.url = 'Navigator.do';
		ajaxTreeConfig.addressbarId = 'addressbar';
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
	<mm:cloud jspvar="cloud" loginpage="../login.jsp">
		<mm:import externid="channel" from="request" />
		<mm:node referid="channel" jspvar="channel">
			<div id="search"><mm:field name="path" id="channelPath" write="false" />
			<html:form action="/editors/repository/QuickSearchAction" target="bottompane">
				<html:text property="path" value="${channelPath}" styleId="addressbar" styleClass="width80" />
				<html:submit styleClass="button"> <fmt:message key="selector.search" /> </html:submit>
			</html:form>
			<div id="addressbar_choices" class="addressbar"></div>
			<script type="text/javascript">
				new AddressBar("addressbar", 
					"addressbar_choices", 
					ajaxTreeConfig.url + "?action=autocomplete",
					{paramName: "path" });
			</script>
			</div>
			<a href="LocateInitAction.do" target="content">
				<fmt:message key="selector.searchcontent" />
			</a><br />
			<mm:hasrank minvalue="administrator">
				<a href="../recyclebin/index.jsp" target="content">
					<fmt:message key="selector.recyclebin" />
				</a><br />
			</mm:hasrank>
			<br />
		</mm:node>


		<script type="text/javascript">
			ajaxTreeLoader.initTree('', 'tree');
		</script>
		<div style="float: left" id="tree">Repository loading</div>

		<html:form action="/editors/repository/PasteAction">
			<html:hidden property="action" />
			<html:hidden property="sourcePasteChannel" />
			<html:hidden property="destPasteChannel" />
		</html:form>

	</mm:cloud>
	</body>
</html:html>
</mm:content>
