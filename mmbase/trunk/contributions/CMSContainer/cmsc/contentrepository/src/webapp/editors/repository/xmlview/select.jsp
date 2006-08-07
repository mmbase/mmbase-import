<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.Iterator"%>
<%@include file="../globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html xhtml="true">

<mm:import externid="number" />
<mm:notpresent referid="number">
	<body>
		Channel parameter missing.
	</body>
</mm:notpresent>

<mm:present referid="number">
<mm:cloud>
	<mm:node referid="number">
		<mm:field name="path" id="path" write="false"/>
	</mm:node>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="../../css/main.css" type="text/css" rel="stylesheet" />
		<script type="text/javascript">
			function changeSelect() {
				var newHref = "";
				var contentType = document.forms['selectForm'].elements['contentType'].value;
				if (contentType != '') {
					newHref = newHref +  'contentType=' + contentType + '&'
				}
				var numbersOnly = document.forms['selectForm'].elements['numbersOnly'].value;
				if (numbersOnly != '') {
					newHref = newHref + "numbersOnly=" + numbersOnly + '&'
				}
				if(contentType.indexOf('&lt;') == -1) {
					parent.xml.location.href = '../xml?'+ newHref + 'channel=<mm:write escape="url" referid="path"/>';
				}
			}
		</script>
	</head>
	<body>
		<form name="selectForm">
			<select name="contentType" onchange="changeSelect()">
				<option value="">&lt;Kies een content type&gt;</option>

				<mm:listnodes type="editwizards" orderby="nodepath">
					<mm:field name="nodepath" jspvar="nodepath" vartype="String">
						<% if (com.finalist.cmsc.repository.ContentElementUtil.isContentType(nodepath)) { %>
							<option value="<mm:write />"><mm:field name="name"/></option>
						<% } %>
					</mm:field>
				</mm:listnodes>
			</select>
			
			<select name="numbersOnly" onchange="changeSelect()">
				<option value="false">show all information</option>
				<option value="true">show numbers only</option>
			</select>
		</form>
	</body>
</mm:cloud>
</mm:present>
</html:html>
</mm:content>
