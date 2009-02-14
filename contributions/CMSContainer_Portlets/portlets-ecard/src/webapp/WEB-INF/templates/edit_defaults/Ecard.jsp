<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />

			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />

			<%-- Select content channel selector --%>
			<c:import url="sections/selectchannel.jsp" />

			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.senderEmail" />:</td>
				<td>
					<input type="text" name="senderEmail" value="${senderEmail}" />
				</td>
			</tr>	
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.senderName" />:</td>
				<td>
					<input type="text" name="senderName" value="${senderName}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.emailSubject" />:</td>
				<td>
					<input type="text" name="emailSubject" value="${emailSubject}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.emailBodyBefore" />:</td>
				<td>
					<textarea name="emailBodyBefore" rows="6"><c:out value="${emailBodyBefore}"/></textarea>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.emailBodyAfter" />:</td>
				<td>
					<textarea name="emailBodyAfter" rows="6"><c:out value="${emailBodyAfter}"/></textarea>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.confirmation" />:</td>
				<td>
					<textarea name="confirmation" rows="6"><c:out value="${confirmation}"/></textarea>
				</td>
			</tr>	

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />

		</table>
	</form>
</div>