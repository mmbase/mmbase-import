<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<mm:cloud>
	<mm:import externid="elementId" required="true" from="request" />
	<mm:node number="${elementId}" notfound="skip">
		<h3><mm:field name="title" /></h3>
		<mm:field name="body"><mm:isnotempty><p class="body"><mm:write /></p></mm:isnotempty></mm:field>
	</mm:node>
	<form name="<portlet:namespace />form" method="post"
		action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table>
			<tr>
				<td>
					<fmt:message key="view.name" />:
				</td>
				<td>
					<input type="text" name="name" />
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="view.title" />:
				</td>
				<td>
					<input type="text" name="title" />
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="view.body" />:
				</td>
				<td>
					<textarea name="body"></textarea>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="submit" name="nieuw" value="<fmt:message key="view.submit" />"/>
				</td>
			</tr>
		</table>
	</form>
</mm:cloud>

