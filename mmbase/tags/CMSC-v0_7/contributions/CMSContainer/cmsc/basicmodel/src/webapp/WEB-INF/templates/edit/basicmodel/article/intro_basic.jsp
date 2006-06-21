<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<mm:cloud>
<form name="contentportlet" method="post"
	action="<portlet:actionURL><portlet:param name="action" value="edit"/></portlet:actionURL>">
	<mm:import externid="elementId" required="true" from="request" />
	<mm:node number="${elementId}" notfound="skip">
		<table>
			<tr>
				<td><fmt:message key="edit.element.title" />:</td>
				<td><mm:field name="title">
					<input type="text" name="content_${elementId}_title" value="<mm:write />" />
				</mm:field></td>
			</tr>
			<tr>
				<td><fmt:message key="edit.element.subtitle" />:</td>
				<td><mm:field name="subtitle">
					<input type="text" name="content_${elementId}_subtitle" value="<mm:write />" />
				</mm:field></td>
			</tr>
			<tr>
				<td><fmt:message key="edit.element.intro" />:</td>
				<td><mm:field name="intro">
					<textarea cols="30" rows="5" name="content_${elementId}_intro" id="content_${elementId}_intro"><mm:write /></textarea>
					<script type="text/javascript">
						createHTMLArea('content_${elementId}_intro');
					</script>
				</mm:field></td>
			</tr>
			<tr>
				<td align="left"><input type="submit" value="<fmt:message key="edit.save" />" /></td>
				<td></td>
			</tr>
		</table>
	</mm:node>
</form>
</mm:cloud>
