<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="../sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="../sections/viewselector.jsp" />

			<cmsc:location var="cur" sitevar="site" />
			<c:set var="page" value="${cur.id}" />
			<input type="hidden" name="page" value="${page}" />

			<tr>
				<td colspan="2">
					<fmt:message key="edit_defaults.available_newsletters" />
				</td>
				<td class="#">
				                    <mm:cloud>
						<mm:listnodes type="newsletter">
							<mm:field name="number" id="number" write="false" />
							<mm:field name="title"  id="title" write="false" />
							<cmsc:checkbox var="allowednewsletters" value="${number}" />${title}</td></tr><tr><td>&nbsp;</td><td>
						</mm:listnodes>
					</mm:cloud>

				</td>
			</tr>

			<%-- Save button --%>
			<c:import url="../sections/savebutton.jsp" />
			
		</table>
	</form>
</div>