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
		ajaxTreeConfig.url = '<mm:url page="SelectorPage.do"/>';
		ajaxTreeConfig.addressbarId = 'addressbar';
		
		treeNumbers = new Array();
		treeDivs = new Array();
		treeSize = 0;
        function loadFunction() {
	    	alphaImages();
	    	for(count = 0; count < treeSize; count++) {
			    ajaxTreeLoader.initTree(treeNumbers[count], treeDivs[count]);
			}
        }
	</script>
	<style type="text/css">
		body {
			behavior: url(../../css/hover.htc);
		}
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
   <div class="side_block">
      <div class="header">
         <div class="title"><fmt:message key="selector.title" /></div>
         <div class="header_end"></div>
      </div>
		<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
			<mm:import externid="channel" from="parameters" />
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
	
			<form action="SelectorPage.do" id="addressBarForm">
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
			<div style="clear:both"></div>
	
			<%for (NodeIterator iter = SiteUtil.getSites(cloud).nodeIterator(); iter.hasNext();) {
	            Node site = iter.nextNode();
			%>
			<script type="text/javascript">
				treeNumbers[treeSize] = '<%= site.getNumber() %>';
				treeDivs[treeSize] = 'tree<%= site.getNumber() %>';
				treeSize++;
			
//				ajaxTreeLoader.initTree('<%= site.getNumber() %>', 'tree<%= site.getNumber() %>');
			</script>
			<div id="tree<%= site.getNumber() %>">Site loading</div>
			<br/>
			<% } %>
		</mm:cloud>
		<jsp:include page="../../usermanagement/role_legend.jsp"/>
      <div class="side_block_end"></div>
   </div>
	</body>
</html:html>
</mm:content>