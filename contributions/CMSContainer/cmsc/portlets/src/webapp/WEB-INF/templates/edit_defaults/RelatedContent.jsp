<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<h3><fmt:message key="edit_defaults.title" /></h3>	
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">	
		<table class="editcontent">
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />
			
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.window" />:</td>
				<td>
					<cmsc:select var="window">
						<c:forEach var="position" items="${pagepositions}">
							<cmsc:option value="${position}" />
						</c:forEach>
					</cmsc:select>
				</td>
			</tr>
		
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
		
		</table>
	</form>
</div>