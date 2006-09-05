<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@page import="com.finalist.cmsc.navigation.*"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
	<head>
	<title><fmt:message key="selector.title" /></title>

	<link href="../css/main.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="ccp.js"></script>
	<script type="text/javascript" src="../utils/cookies.js"></script>

	<link href="../utils/ajaxtree/ajaxtree.css" type="text/css" rel="stylesheet" />
	<link href="../utils/ajaxtree/addressbar.css" type="text/css" rel="stylesheet" />

	<script type="text/javascript" src="../js/prototype.js"></script>
	<script type="text/javascript" src="../js/scriptaculous/scriptaculous.js"></script>
	<script type="text/javascript" src="../utils/ajaxtree/ajaxtree.js"></script>
	<script type="text/javascript" src="../utils/ajaxtree/addressbar.js"></script>
	<script type="text/javascript" src="../utils/window.js"></script>
	<script type="text/javascript" src="../utils/transparent_png.js" ></script>
		
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
      

      function loadFunction() {
		resizeTreeDiv();
		alphaImages();
      }
   </script>
</head>
<body style="overflow: auto" onload="loadFunction()">
	<mm:cloud jspvar="cloud" loginpage="../login.jsp">
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

<div id="left">
		<div class="side_block_gray">
			<!-- bovenste balkje -->
			<div class="header">
				<div class="title"><fmt:message key="selector.title" /></div>
				<div class="header_end"></div>
			</div>
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
		
			<div id="tree" style="float: left;width: 310px; height: 100px; overflow:auto;">
				<%for (NodeIterator iter = SiteUtil.getSites(cloud).nodeIterator(); iter.hasNext();) {
	            	Node site = iter.nextNode();
				%>
				<script type="text/javascript">
					ajaxTreeLoader.initTree('<%= site.getNumber() %>', 'tree<%= site.getNumber() %>');
				</script>
				<div style="float: left" id="tree<%= site.getNumber() %>">Site loading</div>
				<div style="height: 15px; clear: both"></div>
				<% } %>

				<mm:hasrank minvalue="administrator">
					<ul class="shortcuts" style="width: 200px">
		               <li class="sitenew">
							<a href="SiteCreate.do" target="content"><fmt:message key="selector.newsite" /></a>
						</li>
					</ul>
				</mm:hasrank>
			</div>

			<html:form action="/editors/site/PasteAction">
				<html:hidden property="action" />
				<html:hidden property="sourcePasteChannel" />
				<html:hidden property="destPasteChannel" />
			</html:form>
			<div class="side_block_end"></div>
		</div>
	</div>		

	</mm:cloud>
</body>
</html:html>
</mm:content>
