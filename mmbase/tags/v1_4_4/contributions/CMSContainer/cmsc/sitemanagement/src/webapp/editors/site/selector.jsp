<%@page language="java" contentType="text/html;charset=utf-8"
%><%@page import="com.finalist.cmsc.navigation.*"
%><%@include file="globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="selector.title" ajax="true">
	<script type="text/javascript" src="ccp.js"></script>
	<script type="text/javascript" src="../utils/cookies.js"></script>

	<link href="../utils/ajaxtree/ajaxtree.css" type="text/css" rel="stylesheet" />
	<link href="../utils/ajaxtree/addressbar.css" type="text/css" rel="stylesheet" />

	<script type="text/javascript" src="../utils/ajaxtree/ajaxtree.js"></script>
	<script type="text/javascript" src="../utils/ajaxtree/addressbar.js"></script>

	<script type="text/javascript">
		ajaxTreeConfig.resources = '../utils/ajaxtree/images/';
		ajaxTreeConfig.url = '<mm:url page="Navigator.do" />';
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
   <script type="text/javascript">
      function resizeTreeDiv() {
         var treeElement = document.getElementById('tree');
         if (treeElement) {
            var offsetHeight = treeElement.offsetHeight;
            var leftElement = document.getElementById('left');
            offsetHeight = leftElement.offsetHeight - offsetHeight;
            var frameHeight = getFrameHeight(document.window);
            treeElement.style.height = (frameHeight - offsetHeight) + 'px';
         }
      }

      window.onresize = resizeTreeDiv;
		
		var treeNumbers = new Array();
		var treeDivs = new Array();
		var treeSize = 0;
        function loadFunction() {
			resizeTreeDiv();
	    	alphaImages();
	    	for(count = 0; count < treeSize; count++) {
			    ajaxTreeLoader.initTree(treeNumbers[count], treeDivs[count]);
			}
        }
   </script>
</cmscedit:head>
<body style="overflow: auto" onload="loadFunction();">
	<mm:cloud jspvar="cloud" loginpage="../login.jsp">
		<mm:import externid="channel" from="request" />
		<mm:compare referid="channel" value="" inverse="true">
			<mm:node number="${channel}" jspvar="pageNode" notfound="skip">
				<mm:import id="pagepath"><mm:field name="path" /></mm:import>
			</mm:node>
		</mm:compare>
		<mm:compare referid="channel" value="">
			<mm:import id="pagepath"></mm:import>
		</mm:compare>
<div id="left">

<% NodeList sites = SiteUtil.getSites(cloud);
   request.setAttribute("siteNodes", sites); %>

<cmscedit:sideblock title="selector.title" titleClass="side_block_gray"
		titleStyle="width: 241px;" bodyClass="body_table">
	<div class="search_form">
		<html:form action="/editors/site/QuickSearchAction" target="bottompane" styleId="addressBarForm">
		      <html:text property="path" value="${pagepath}" styleId="addressbar"/>
		</html:form>
	</div>
	<div class="search_form_options">
		<a href="#" class="button" onclick="getElementById('addressBarForm').submit()"> <fmt:message key="selector.search" /> </a>
	</div>

	<div id="addressbar_choices" class="addressbar"></div>
	<script type="text/javascript">
		new AddressBar("addressbar", 
			"addressbar_choices", 
			ajaxTreeConfig.url + "?action=autocomplete",
			{paramName: "path" });
	</script>
	<div style="clear:both"></div>

	<div id="tree" style="float: left;width: 239px; height: 100px; overflow:auto;">
		<mm:listnodes referid="siteNodes">
			<script type="text/javascript">
				treeNumbers[treeSize] = '${_node.number}';
				treeDivs[treeSize] = 'tree${_node.number}';
				treeSize++;
			
//				ajaxTreeLoader.initTree('${_node.number}', 'tree${_node.number}');
			</script>
			<div style="float: left" id="tree${_node.number}">Site loading</div>
			<div style="height: 15px; clear: both"></div>
		</mm:listnodes>
		<mm:hasrank minvalue="administrator">
			<ul class="shortcuts">
               <li class="sitenew">
					<a href="SiteCreate.do" target="content"><fmt:message key="selector.newsite" /></a>
				</li>
			</ul>
		</mm:hasrank>
		<jsp:include page="../usermanagement/role_legend.jsp"/>
	</div>

	<html:form action="/editors/site/PasteAction">
		<html:hidden property="action" />
		<html:hidden property="sourcePasteChannel" />
		<html:hidden property="destPasteChannel" />
	</html:form>
</cmscedit:sideblock>
</div>
</mm:cloud>
</body>
</html:html>
</mm:content>