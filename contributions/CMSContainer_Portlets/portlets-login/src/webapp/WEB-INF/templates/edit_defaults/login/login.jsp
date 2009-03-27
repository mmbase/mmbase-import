<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>
<fmt:setBundle basename="com.finalist.cmsc.portlets.LoginPortlet" scope="request"/>
<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="../sections/definitiondisplay.jsp" />
		
			<tr>
				<td colspan="3"><h4><fmt:message key="view.account.sendpassword" /></h4></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.register.emailfromname" />:</td>
				<td>
					<input type="text" name="emailFromName" value="${fn:escapeXml(emailFromName)}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.register.emailfromemail" />:</td>
				<td>
					<input type="text" name="emailFromEmail" value="${fn:escapeXml(emailFromEmail)}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.register.emailsubject" />:</td>
				<td>
					<input type="text" name="emailSubject" value="${fn:escapeXml(emailSubject)}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.register.emailtext" />:</td>
				<td>
					<textarea name="emailText" rows="8" cols="25"><c:out value="${emailText}" /></textarea>
				</td>
			</tr>

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>