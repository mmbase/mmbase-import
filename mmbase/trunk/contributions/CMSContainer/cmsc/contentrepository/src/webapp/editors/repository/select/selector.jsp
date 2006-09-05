<%@page language="java" contentType="text/html;charset=utf-8"%>

<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
	<title><fmt:message key="selector.title" /></title>
	<link href="../../css/main.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="../ccp.js"></script>
	<script type="text/javascript" src="../../utils/cookies.js"></script>

	<link href="../../utils/ajaxtree/ajaxtree.css" type="text/css" rel="stylesheet" />
	<link href="../../utils/ajaxtree/addressbar.css" type="text/css" rel="stylesheet" />

	<script type="text/javascript" src="../../js/prototype.js"></script>
	<script type="text/javascript" src="../../js/scriptaculous/scriptaculous.js"></script>
	<script type="text/javascript" src="../../utils/ajaxtree/ajaxtree.js"></script>
	<script type="text/javascript" src="../../utils/ajaxtree/addressbar.js"></script>
	<script type="text/javascript" src="../../utils/window.js"></script>
    <script type="text/javascript" src="../../utils/transparent_png.js" ></script>


	<script type="text/javascript">
		ajaxTreeConfig.resources = '../../utils/ajaxtree/images/';
		ajaxTreeConfig.url = '<mm:url page="SelectorContent.do"/>';
		ajaxTreeConfig.addressbarId = 'addressbar';
		
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
      
      function loadFunction() {
		resizeTreeDiv();
		alphaImages();
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
	</head>
	<body style="overflow: auto" onload="loadFunction();">
	
<div id="left">
   <div class="side_block">
      <div class="header">
         <div class="title"><fmt:message key="selector.title" /></div>
         <div class="header_end"></div>
      </div>
		<ul class="shortcuts">
			<li class="search"><a href="../SearchInitAction.do?action=select" target="selectcontent"><fmt:message key="selector.searchcontent" /></a></li>
		</ul>
			
		<mm:import externid="channel" from="request" />
		<mm:node referid="channel">
			<mm:field name="path" id="channelPath" write="false" />
	
			<form action="SelectorContent.do" id="addressBarForm">
				   <div class="search_form">
						<input type="text" name="path" value="${channelPath}" id="addressbar" class="width80" />
					</div>
					<div class="search_form_options">
					   <a href="#" class="button" onclick="getElementById('addressBarForm').submit()"> <fmt:message key="selector.search" /> </a>
					</div>
			</form>
		</mm:node>
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
      <div class="side_block_end"></div>
   </div>
</div>

	</body>
</html:html>
</mm:content>