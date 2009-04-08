<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<tr>
	<td colspan="3"><h4><fmt:message key="edit_defaults.portletset"/></h4></td>
</tr>
			
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
