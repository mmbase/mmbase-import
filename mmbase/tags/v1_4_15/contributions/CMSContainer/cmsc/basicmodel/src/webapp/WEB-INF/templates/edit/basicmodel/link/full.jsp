<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<c:if test="${viewtype eq 'list'}">
	<cmsc:renderURL var="renderUrl" />
	<a href="${renderUrl}"><fmt:message key="view.back" /></a>
</c:if>

<mm:cloud>
<form name="contentportlet" id="contentportlet" method="post"
	action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
	<mm:import externid="elementId" required="true" />
	<mm:node number="${elementId}" notfound="skip">

		<h1 id="content_${elementId}_title"><mm:field name="title" /></h1>
		<script type="text/javascript">
			new InPlaceEditor.Local('content_${elementId}_title');
		</script>

		<mm:relatednodes type="attachments" role="posrel">
			<mm:first><ul></mm:first>
			<mm:field name="number" id="attachmentNumber" write="false" />
			<li><span id="content_${attachmentNumber}_title"><mm:field name="title" /></span><br />
				<span id="content_${attachmentNumber}_description"><mm:field name="description" /></span></li>
			<script type="text/javascript">
				new InPlaceEditor.Local('content_${attachmentNumber}_title');
				new InPlaceEditor.Local('content_${attachmentNumber}_description');
			</script>
			<mm:last></ul></mm:last>
		</mm:relatednodes>
		<mm:relatednodes type="urls" role="posrel">
			<mm:first><ul></mm:first>
			<mm:field name="number" id="urlNumber" write="false" />
			<mm:field name="url" id="url" write="false" />
			<li><span id="content_${urlNumber}_name"><mm:field name="name" /></span><br />
				<span id="content_${urlNumber}_url"><mm:field name="url" /></span></li>
			<script type="text/javascript">
				new InPlaceEditor.Local('content_${urlNumber}_name');
				new InPlaceEditor.Local('content_${urlNumber}_url');
			</script>
			<mm:last></ul></mm:last>
		</mm:relatednodes>
	</mm:node>
<p>
<input type="submit" value="<fmt:message key="edit.save" />" />
</p>
</form>
</mm:cloud>