<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<div class="kolom">
<mm:cloud>
	<mm:node number="${contentchannel}" notfound="skip">
		<h2><mm:field name="name"/></h2>
	</mm:node>
</mm:cloud>