<%@page language="java" contentType="text/html;charset=utf-8"%>

<%@include file="globals.jsp"%>
<%@page import="com.finalist.cmsc.repository.RepositoryUtil" %>
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
   <script type="text/javascript">
      function resizeTreeDiv() {
         var treeElement = document.getElementById('tree');
         if (treeElement) {
            var x = treeElement.offsetHeight;
            var leftElement = document.getElementById('left');
            x = leftElement.offsetHeight - x;
            var y = getFrameHeight(document.window);
            treeElement.style.height = y - x + 'px';
         }
      }
      
      function clearDefaultSearchText(defaultText) {
      	var searchField = document.forms["searchForm"]["title"];
      	if(searchField.value == defaultText) {
	      	searchField.value = "";
      	}
      }
      
      window.onresize = resizeTreeDiv;
      
      function loadFunction() {
		resizeTreeDiv();
		alphaImages();
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
	<body style="overflow: auto" onload="loadFunction();">
   <mm:cloud jspvar="cloud" loginpage="../login.jsp">
		<mm:import externid="channel" from="request" />

	<div id="left">
		<div class="side_block">
			<!-- bovenste balkje -->
			<div class="header">
				<div class="title"><fmt:message key="selector.search.header" /></div>
				<div class="header_end"></div>
			</div>
			<div class="search_form">
	      		<form action="SearchAction.do" name="searchForm" method="post" target="content">
				<input type="text" name="title" value="<fmt:message key="selector.search.term" />" onfocus="clearDefaultSearchText('<fmt:message key="selector.search.term" />');"/>
				</form>
			</div>
			<div class="search_form_options">
				<a href="javascript:document.forms['searchForm'].submit()" class="button"><fmt:message key="selector.search.search" /></a>
			</div>
				
			<ul class="shortcuts">
				<mm:hasrank minvalue="administrator">
				<li class="trashbin">
					<a href="<mm:url page="../recyclebin/index.jsp"/>" target="content">
						<fmt:message key="selector.recyclebin" />
					</a>
					<mm:import id="trashchannel" jspvar="trashchannel"><%= RepositoryUtil.ALIAS_TRASH %></mm:import>
					<mm:node number="$trashchannel">
						(<mm:countrelations type="contentelement" searchdir="destination" role="contentrel"/>)
					</mm:node>
				</li>
				</mm:hasrank>
        			<li class="images"><a href="<mm:url page="../resources/ImageInitAction.do"/>" target="content"><fmt:message key="selector.images" /></a></li>
				<li class="attachements"><a href="<mm:url page="../resources/AttachmentInitAction.do"/>" target="content"><fmt:message key="selector.attachments" /></a></li>
				<li class="urls"><a href="<mm:url page="../resources/UrlInitAction.do"/>" target="content"><fmt:message key="selector.urls" /></a></li>
			</ul>
			<!-- einde block -->
			<div class="side_block_end"></div>
		</div>
		
		<div class="side_block_gray">
			<!-- bovenste balkje -->
			<div class="header">
				<div class="title"><fmt:message key="selector.title" /></div>
				<div class="header_end"></div>
			</div>
			<div class="search_form">
				<mm:node referid="channel" jspvar="channel">
					<mm:field name="path" id="channelPath" write="false" />
					<html:form action="/editors/repository/QuickSearchAction" target="bottompane" styleId="addressBarForm">
							<html:text property="path" value="${channelPath}" styleId="addressbar"/>
					</html:form>
				</mm:node>
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
			<br />
			<script type="text/javascript">
				ajaxTreeLoader.initTree('', 'tree');
			</script>
			<div id="tree" style="float: left;width: 310px; height: 100px; overflow:auto"><fmt:message key="selector.loading" /></div>

			<html:form action="/editors/repository/PasteAction">
				<html:hidden property="action" />
				<html:hidden property="sourcePasteChannel" />
				<html:hidden property="destPasteChannel" />
			</html:form>
			<!-- einde block -->
			<div class="side_block_end"></div>
		</div>
	</div>
	</mm:cloud>
	</body>
</html:html>
</mm:content>
