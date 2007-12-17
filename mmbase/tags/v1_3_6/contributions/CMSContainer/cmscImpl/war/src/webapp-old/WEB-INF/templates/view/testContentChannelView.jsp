<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<mm:cloud>
	<mm:node number="${contentchannel}" jspvar="m" notfound="skip">
		<h2><mm:field name="name" /></h2>
	</mm:node>
</mm:cloud>
