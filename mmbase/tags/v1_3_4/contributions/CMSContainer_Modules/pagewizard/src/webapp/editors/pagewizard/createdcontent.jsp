<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="pagewizard.title">
	<c:if test="${param.action != 'cancel'}">
		<mm:cloud>
			<mm:node number="${param.ewnodelastedited}">
				<mm:field name="title" jspvar="title" write="false"/>
			</mm:node>
		</mm:cloud>
	</c:if>
	<script>
		function loaded() {
			<c:if test="${param.action != 'cancel'}">
				window.opener.selectElement('${param.ewnodelastedited}', '${title}');
			</c:if>
			window.close();
		}
	</script>
</cmscedit:head>
<body onload="loaded()">

</body>
</html:html>
</mm:content>