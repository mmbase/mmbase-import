<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<cmsc:portlet-preferences />

<mm:cloud>
	<table width="75%" border="0" cellspacing="0" cellpadding="0">

		<c:forEach var="articlenumber" items="${defaultarticles}">
			<mm:node number="${articlenumber}" notfound="skip">
			<cmsc:contenturl number="${articlenumber}" absolute="true" jspvar="url" write="false" />
			<tr><td><a href="${url}"><b><mm:field name="title" write="true" /></b></a></td></tr>				
			<tr><td><mm:field name="intro" write="true" /></td></tr>
			</mm:node>
			<tr><td>&nbsp;</td></tr>
		</c:forEach>

		<c:forEach var="theme" items="${additionalthemes}">
			<mm:node number="${theme}" notfound="skip">
			<tr><td><h3><mm:field name="title" write="true" /></h3></td></tr>
			<mm:import jspvar="articles" vartype="List" reset="true" externid="articles${theme}" />
			<c:forEach var="articlenumber" items="${articles}">
				<mm:node number="${articlenumber}" notfound="skip">
				<cmsc:contenturl number="${articlenumber}" absolute="true" jspvar="url" write="false" />
				<tr><td><a href="${url}"><b><mm:field name="title" write="true" /></b></a></td></tr>				
				<tr><td><mm:field name="intro" write="true" /></td></tr>
				</mm:node>
				<tr><td>&nbsp;</td></tr>
			</c:forEach>
			</mm:node>
			<tr><td>&nbsp;</td></tr>
		</c:forEach>
	</table>
</mm:cloud>
