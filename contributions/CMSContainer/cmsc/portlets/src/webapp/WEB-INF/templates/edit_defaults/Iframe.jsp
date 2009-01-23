<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<h3><fmt:message key="edit_defaults.title" /></h3>	
	<form name="<portlet:namespace />form" method="post" action="<cmsc:actionURL />" target="_parent">	
		<table class="editcontent">
			<tr>
				<td><fmt:message key="edit_defaults.define"/></td>
				<mm:cloud>
					<mm:node number="${requestScope['com.finalist.cmsc.beans.om.definitionId']}" notfound="skip">
						<td>
							<input type="text" name="portletname" value="<mm:field name='title'/>" disabled="disabled"/>
						</td>
					</mm:node>
				</mm:cloud>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.source" />:</td>
				<td><cmsc:text var="source" /></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.height" />:</td>
				<td><cmsc:text var="height" /></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.width" />:</td>
				<td><cmsc:text var="width" /></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.scrolling" />:</td>
				<td><cmsc:select var="scrolling">
						<cmsc:option value="no" message="edit_defaults.scrolling.no" />
						<cmsc:option value="yes" message="edit_defaults.scrolling.yes"/>
						<cmsc:option value="auto" message="edit_defaults.scrolling.auto" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.frameBorder" />:</td>
				<td><cmsc:select var="frameBorder">
						<cmsc:option value="0" message="edit_defaults.frameBorder.no" />
						<cmsc:option value="1" message="edit_defaults.frameBorder.yes" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.marginHeight" />:</td>
				<td><cmsc:text var="marginHeight" /></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.marginWidth" />:</td>
				<td><cmsc:text var="marginWidth" /></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.align" />:</td>
				<td><cmsc:select var="align">
						<cmsc:option value="top" message="edit_defaults.align.top" />
						<cmsc:option value="middle" message="edit_defaults.align.middle" />
						<cmsc:option value="bottom" message="edit_defaults.align.bottom" />
						<cmsc:option value="left" message="edit_defaults.align.left" />
						<cmsc:option value="right" message="edit_defaults.align.right" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.style" />:</td>
				<td><cmsc:text var="style" /></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.class" />:</td>
				<td><cmsc:text var="class" /></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.id" />:</td>
				<td><cmsc:text var="id" /></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.name" />:</td>
				<td><cmsc:text var="name" /></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.usetable" />:</td>
				<td><cmsc:select var="useTable">
						<cmsc:option value="true" message="edit_defaults.usetable.yes" />
						<cmsc:option value="false" message="edit_defaults.usetable.no" />
					</cmsc:select>
				</td>
			</tr>
		
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>