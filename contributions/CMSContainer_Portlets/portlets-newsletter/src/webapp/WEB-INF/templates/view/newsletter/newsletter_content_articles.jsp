<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<cmsc:portlet-preferences />

<mm:import externid="themes" />

<mm:cloud>
			<table width="90%" border="0" cellspacing="0" cellpadding="0">

		<c:forEach var="themenumber" items="${themes}">
			Thema: ${themenumber}
			<c:forEach var="articlenumber" items="${themenumber}">
				Article: ${articlenumber}
			</c:forEach>
		</c:forEach>
			</table>
</mm:cloud>

