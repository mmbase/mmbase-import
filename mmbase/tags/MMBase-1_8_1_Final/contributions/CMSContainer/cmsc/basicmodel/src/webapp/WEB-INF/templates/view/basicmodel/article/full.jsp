<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<c:if test="${viewtype eq 'list'}">
	<portlet:renderURL var="renderUrl" />
	<a href="${renderUrl}"><fmt:message key="view.back" /></a>
</c:if>

<mm:cloud>
	<mm:import externid="elementId" required="true" />
	<mm:node number="${elementId}" notfound="skip">

		<mm:field name="title"><mm:isnotempty><h1><mm:write /></h1></mm:isnotempty></mm:field>
		<mm:field name="subtitle"><mm:isnotempty><h2><mm:write /></h2></mm:isnotempty></mm:field>
		<mm:field name="intro"><mm:isnotempty><p class="intro"><mm:write /></p></mm:isnotempty></mm:field>

		<cmsc-bm:linkedimages position="top-left" style="float: left;" />
		<cmsc-bm:linkedimages position="top-right" style="float: right;" />
		<mm:field name="body" />
		<cmsc-bm:linkedimages position="bottom-left" style="float: left;" />
		<cmsc-bm:linkedimages position="bottom-right" style="float: right;" />

		<mm:relatednodes type="attachments">
			<mm:first><p><b>Bijlagen</b></p><ul></mm:first>
			<li><a href="<mm:attachment/>" title="<mm:field name='description'/>" target="_blank"><mm:field name="title" /></a></li>
			<mm:last></ul></mm:last>
		</mm:relatednodes>
		<mm:relatednodes type="urls">
			<mm:first><p><b>Links</b></p><ul></mm:first>
			<mm:field name="url" id="url" write="false" />
			<li><a href="<mm:url referid='url'/>" title="<mm:field name='name'/>" target="_blank"><mm:field name="name" /></a></li>
			<mm:last></ul></mm:last>
		</mm:relatednodes>
	</mm:node>
</mm:cloud>