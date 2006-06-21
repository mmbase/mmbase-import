<%@page language="java" contentType="text/html;charset=utf-8"%>
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
		ajaxTreeConfig.url = 'SelectorChannel.do';
		ajaxTreeConfig.addressbarId = 'addressbar';
	</script>
	<script type="text/javascript">
		function selectItem(channel, path) {
			opener.selectChannel(channel, path);
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
	<mm:import externid="channel" from="request" />
	<mm:node referid="channel">
		<mm:field name="path" id="channelPath" write="false" />

		<form action="SelectorChannel.do">
			<input type="text" name="path" value="${channelPath}" id="addressbar" class="width80" />
			<input type="submit" class="button" value=" <fmt:message key="selector.search" /> " />
		</html>
		<div id="addressbar_choices" class="addressbar"></div>
		<script type="text/javascript">
			new AddressBar("addressbar", 
				"addressbar_choices", 
				ajaxTreeConfig.url + "?action=autocomplete",
				{paramName: "path" });
		</script>
	</mm:node>

	<script type="text/javascript">
		ajaxTreeLoader.initTree('', 'tree');
	</script>
	<div style="float: left" id="tree">Repository loading</div>

	</body>
</html:html>
</mm:content>