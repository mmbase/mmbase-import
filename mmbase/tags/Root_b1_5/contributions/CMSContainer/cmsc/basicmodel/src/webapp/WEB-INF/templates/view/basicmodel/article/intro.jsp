<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<mm:cloud>
	<mm:import externid="elementId" required="true" from="request" />
	<mm:node number="${elementId}" notfound="skip">
		<h1><mm:field name="title" /></h1>
		<mm:field name="subtitle"><mm:isnotempty><h2><mm:write /></h2></mm:isnotempty></mm:field>
		<mm:field name="intro"><mm:isnotempty><p class="intro"><mm:write escape="none" /></p></mm:isnotempty></mm:field>
		<br />
		<cmsc:renderURL page="${page}" window="${window}" var="renderUrl"><cmsc:param name="elementId" value="${elementId}" /></cmsc:renderURL>
		<a href="${renderUrl}"><fmt:message key="view.readmore" /></a>
	</mm:node>
</mm:cloud>