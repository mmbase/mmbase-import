<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<c:if test="${viewtype eq 'list'}">
	<cmsc:renderURL var="renderUrl" />
	<a href="${renderUrl}"><fmt:message key="view.back" /></a>
</c:if>

<mm:cloud>
	<mm:import externid="elementId" required="true" />
	<mm:node number="${elementId}" notfound="skip">

		<mm:field name="title"><mm:isnotempty><h1><mm:write /></h1></mm:isnotempty></mm:field>
		<mm:field name="subtitle"><mm:isnotempty><h2><mm:write /></h2></mm:isnotempty></mm:field>

		<cmsc-bm:linkedimages position="top-left" style="float: left; padding: 1em 1em;" />
		<cmsc-bm:linkedimages position="top" style="display: block; clear: both; padding: 1em 0em;" />
		<cmsc-bm:linkedimages position="top-right" style="float: right; padding: 1em 1em;" />
		<mm:field name="body" escape="none" />
		<cmsc-bm:linkedimages position="bottom-left" style="float: left; padding: 1em 1em;" />
		<cmsc-bm:linkedimages position="bottom" style="display: block; clear: both; padding: 1em 0em;" />
		<cmsc-bm:linkedimages position="bottom-right" style="float: right; padding: 1em 1em;" />
		
		<mm:countrelations role="posrel" searchdir="destination" id="secondaryContentCount" write="false" />
		<c:if test="${secondaryContentCount gt 0}">
			<p />
			<mm:relatednodes type="attachments" role="posrel" orderby="posrel.pos" searchdir="destination">
				<mm:first><ul></mm:first>
				<li><a href="<mm:attachment/>" title="<mm:field name='description'/>" target="_blank"><mm:field name="title" /></a></li>
				<mm:last></ul></mm:last>
			</mm:relatednodes>
			<mm:relatednodes type="urls" role="posrel" orderby="posrel.pos" searchdir="destination">
				<mm:first><ul></mm:first>
				<mm:field name="url" id="url" write="false" />
				<li><a href="<mm:url referid='url'/>" title="<mm:field name='name'/>" target="_blank"><mm:field name="name" /></a></li>
				<mm:last></ul></mm:last>
			</mm:relatednodes>
			<mm:relatednodes type="contentelement" role="posrel" orderby="posrel.pos" searchdir="destination">
				<mm:first><ul></mm:first>
				<mm:field name="number" id="elementNumber" write="false" />
				<li><a href="<cmsc:contenturl number="${elementNumber}"/>" title="<mm:field name='title'/>"><mm:field name="title" /></a></li>
				<mm:last></ul></mm:last>
			</mm:relatednodes>
		</c:if>
	</mm:node>
</mm:cloud>