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
		<h2 id="content_${elementId}_subtitle"><mm:field name="subtitle" /></h2>
		<p class="intro" id="content_${elementId}_intro"><mm:field name="intro" escape="none" /></p>

		<cmsc-bm:linkedimages position="top-left" style="float: left;" />
		<cmsc-bm:linkedimages position="top-right" style="float: right;" />
		<div id="content_${elementId}_body"><mm:field name="body" escape="none" /></div>
		<cmsc-bm:linkedimages position="bottom-left" style="float: left;" />
		<cmsc-bm:linkedimages position="bottom-right" style="float: right;" />

		<script type="text/javascript">
			new InPlaceEditor.Local('content_${elementId}_title');
			new InPlaceEditor.Local('content_${elementId}_subtitle');
			new InPlaceEditor.Local('content_${elementId}_intro', {minHeight:100, htmlarea:true, formId:'contentportlet'});
			new InPlaceEditor.Local('content_${elementId}_body', {minHeight:300, htmlarea:true, formId:'contentportlet'});
		</script>
	</mm:node>
<p>
<input type="submit" value="<fmt:message key="edit.save" />" />
</p>
</form>
</mm:cloud>