<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<tr>
	<td colspan="2"><fmt:message key="edit_defaults.define"/>:</td>
	<mm:cloud>
		<mm:node number="${requestScope['com.finalist.cmsc.beans.om.definitionId']}" notfound="skip">
			<td>
				<input type="text" name="portletname" value="<mm:field name='title'/>" disabled="disabled"/>
			</td>
		</mm:node>
	</mm:cloud>
</tr>
