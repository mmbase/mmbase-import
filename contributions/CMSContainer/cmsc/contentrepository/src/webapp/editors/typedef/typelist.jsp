<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<html>
	<head>
		<META http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Search Results</title>
		<link href="../../mmbase/edit/wizard/style/layout/searchlist.css" type="text/css" rel="stylesheet">
		<link href="../../editors/editwizards_new/style/color/searchlist.css" type="text/css" rel="stylesheet">
		<link href="../../editors/editwizards_new/style/extra/searchlist.css" type="text/css" rel="stylesheet">
		<style xml:space="preserve" type="text/css">
		  body { behavior: url(../../editors/css/hover.htc);}
		</style>
		<script src="../../mmbase/edit/wizard/javascript/tools.js" type="text/javascript"><!--help IE--></script>
		<script src="../../mmbase/edit/wizard/javascript/searchlist.js" type="text/javascript"><!--help IE--></script>
		<script type="text/javascript">
		  window.status = "";
		  var listpage = "list.jsp?proceed=true&sessionkey=editwizard&loginmethod=&language=en&country=&debug=false&popupid=search&loginmethod=";
		  var searchtype = getParameter_general("type", "objects");
		  var searchterm = getParameter_general("searchterm", "nothing");
		  var cmd = getParameter_general("cmd", "${cmd}");
		  var selected = getParameter_general("selected", "");
		  var relationRole = getParameter_general("relationRole", "allowrel");
		  var relationOriginNode = "${relationOriginNode}";
		  var relationCreateDir = getParameter_general("relationCreateDir", "");
		</script>
		<script src="../../mmbase/edit/wizard/javascript/searchwindow.js" type="text/javascript"><!--help IE--></script>
	</head>
	<body onload="window.focus(); preselect(selected); doOnloadSearch(); resizeSelectTable();">
		<form>
		<div class="searchresult" id="searchresult" style="height: 340px;">
			<!-- IE is too stupid to understand div.searchresult table -->
			<table class="searchresult" cellspacing="0">	
			  <c:forEach var="contentType" items="${contentTypes}" varStatus="status">
				<tr id="item_${contentType.id}" number="${contentType.id}" onClick="doclick_search(this);" class="<c:if test='${(status.index mod 2) == 0}'>odd</c:if><c:if test='${(status.index mod 2) != 0}'>even</c:if>">
					<td style="display: none;">
						<input id="cb_${contentType.id}" did="${contentType.id}" name="${contentType.id}" style="visibility: hidden;" type="checkbox">
					</td>
					<td>${contentType.name}</td>
				</tr>
			  </c:forEach>
			</table>
		</div>
		<div class="searchnavigation" id="searchnavigation">
			<div>
			<span class="pagenav-current">1</span>&nbsp;
			<span class="pagenav">&nbsp;(items 1-${counttypes}/${counttypes}, pages 1/1)</span>
			</div>
			<div class="page_buttons_seperator">
				<div></div>
			</div>
			<div class="page_buttons">
				<div class="button">
					<div class="body_last">
						<a onclick="dosubmit();" value="OK" name="ok">OK</a>
					</div>
				</div>
				<div class="button">
					<div class="button_body">
						<a onclick="closeSearch();" value="Cancel" name="cancel">Cancel</a>
					</div>
				</div>
				<div class="begin">	</div>
			</div>
		</div>
		</form>
	</body>
</html>

