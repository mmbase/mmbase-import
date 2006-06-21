<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div>
<h3><fmt:message key="edit_defaults.title" /></h3>

<form name="<portlet:namespace />form" method="post" action="<portlet:actionURL />">

<table class="editcontent">
	<tr>
		<td><fmt:message key="edit_defaults.source" />:</td>
		<td><cmsc:text var="source" /></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="<fmt:message key="edit_defaults.save" />" class="button" />
		</td>
	</tr>
</table>
</form>
</div>