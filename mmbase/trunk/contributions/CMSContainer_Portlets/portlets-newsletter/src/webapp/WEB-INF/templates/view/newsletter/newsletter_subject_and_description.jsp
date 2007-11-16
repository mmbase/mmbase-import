<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<mm:cloud>
	<mm:node number="$[page]" notfound="skip">
	<h3><mm:field name="subject" write="true" /></h3>
	<p><mm:field name="description" write="true" /></p>
	</mm:node>
</mm:cloud>
