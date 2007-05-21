<%@page language="java" contentType="text/html;charset=utf-8"
%><%@page import="com.finalist.cmsc.navigation.*"
%><%@include file="../globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="selector.title" ajax="true">
	<link href="../../utils/ajaxtree/addressbar.css" type="text/css" rel="stylesheet" />
	<link href="../../utils/ajaxtree/ajaxtree.css" type="text/css" rel="stylesheet" />

	<script type="text/javascript" src="../../utils/ajaxtree/ajaxtree.js"></script>
	<script type="text/javascript" src="../../utils/ajaxtree/addressbar.js"></script>

	<script type="text/javascript">
		ajaxTreeConfig.resources = '../../utils/ajaxtree/images/';
		ajaxTreeConfig.url = '<mm:url page="${actionname}.do"/>';
		ajaxTreeConfig.addressbarId = 'addressbar';
		ajaxTreeConfig.role = '${param.role}';

		function selectItem(channel, path) {
         opener.selectChannel(channel, path, '${param.fieldName}', '${param.fieldPathName}');
			close();
		}
      function loadFunction() {
		alphaImages();
		ajaxTreeLoader.initTree('', 'tree_div');
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
</cmscedit:head>
<body style="overflow: auto" onload="loadFunction();">
	<cmscedit:sideblock title="selector.title">
        <c:if test="${param.message != null}"><h2>${param.message}</h2></c:if>
		<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
		<mm:import externid="channel" from="parameters" />
		<mm:isnotempty referid="channel">
		<mm:node referid="channel">
			<mm:field name="path" id="channelPath" write="false" />
		</mm:node>
		</mm:isnotempty>
	
			<form action="<mm:url page="${actionname}.do" />" id="addressBarForm">
				   <div class="search_form">
						<input type="text" name="path" value="${channelPath}" id="addressbar" class="width80" />
					</div>
					<div class="search_form_options">
					   <a href="#" class="button" onclick="getElementById('addressBarForm').submit()"> <fmt:message key="selector.search" /> </a>
					</div>
			</form>
			<div id="addressbar_choices" class="addressbar"></div>
			<script type="text/javascript">
				new AddressBar("addressbar", 
					"addressbar_choices", 
					ajaxTreeConfig.url + "?action=autocomplete",
					{paramName: "path" });
			</script>
		</mm:cloud>	
		<div style="clear:both"></div>
		<div style="float: left" id="tree">
			<div style="float: left" id="tree_div"><fmt:message key="selector.loading" /></div>
			<jsp:include page="../../usermanagement/role_legend.jsp"/>
		</div>
	</cmscedit:sideblock>
	</body>
</html:html>
</mm:content>