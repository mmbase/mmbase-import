<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

	<cmsc:renderURL var="renderUrl" />
	<a href="${renderUrl}"><fmt:message key="view.back" /></a>

<mm:cloud>
<form name="contentportlet" method="post"
	action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
	<mm:import externid="elementId" required="true" from="request" />
	<mm:node number="${elementId}" notfound="skip">

		<h1 id="content_${elementId}_title"><mm:field name="title" /></h1>
		<h2 id="content_${elementId}_subtitle"><mm:field name="subtitle" /></h2>
		<p class="intro" id="content_${elementId}_intro"><mm:field name="intro" /></p>

		<cmsc-bm:linkedimages position="top-left" style="float: left;" />
		<cmsc-bm:linkedimages position="top-right" style="float: right;" />
		<div id="content_${elementId}_body"><mm:field name="body" /></div>
		<cmsc-bm:linkedimages position="bottom-left" style="float: left;" />
		<cmsc-bm:linkedimages position="bottom-right" style="float: right;" />

		<script type="text/javascript">
			new Ajax.InPlaceHtmlEditor('content_${elementId}_title', '/demoajaxreturn.html');
			new Ajax.InPlaceHtmlEditor('content_${elementId}_subtitle', '/demoajaxreturn.html');
			new Ajax.InPlaceHtmlEditor('content_${elementId}_intro', '/demoajaxreturn.html', {rows:15,cols:40, htmlarea:true});
			new Ajax.InPlaceHtmlEditor('content_${elementId}_body', '/demoajaxreturn.html', {rows:15,cols:40, htmlarea:true});
		</script>
	</mm:node></form>
</mm:cloud>