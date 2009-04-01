<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<tr>
	<td colspan="3">
		<h4><fmt:message key="edit_defaults.paging" /></h4>
	</td>
</tr>
<tr>
	<td colspan="2"><fmt:message key="edit_defaults.usepaging" />:</td>
	<td>
		<cmsc:select var="usePaging" default="false">
			<cmsc:option value="true" message="edit_defaults.yes" />
			<cmsc:option value="false" message="edit_defaults.no" />
		</cmsc:select>
	</td>
</tr>
<tr>
	<td colspan="2"><fmt:message key="edit_defaults.position" />:</td>
	<td>
		<cmsc:select var="position">
			<cmsc:option value="both" message="edit_defaults.position.both" />
			<cmsc:option value="top" message="edit_defaults.position.top" />
			<cmsc:option value="bottom" message="edit_defaults.position.bottom" />
		</cmsc:select>
	</td>
</tr>
<tr>
	<td colspan="2"><fmt:message key="edit_defaults.elementsperpage" />:</td>
	<td>
		<cmsc:select var="elementsPerPage">
			<cmsc:option value="" message="edit_defaults.unlimited" />
			<cmsc:option value="5" />
			<cmsc:option value="10" />
			<cmsc:option value="15" />
			<cmsc:option value="20" />
			<cmsc:option value="25" />
			<cmsc:option value="50" />
		</cmsc:select>
	</td>
</tr>
<tr>
	<td colspan="2"><fmt:message key="edit_defaults.numberofpages" />:</td>
	<td>
		<cmsc:select var="showPages">
			<cmsc:option value="" message="edit_defaults.unlimited" />
			<cmsc:option value="5" />
			<cmsc:option value="10" />
			<cmsc:option value="15" />
			<cmsc:option value="20" />
		</cmsc:select>
	</td>
</tr>
<tr>
	<td colspan="2"><fmt:message key="edit_defaults.pagesindex" />:</td>
	<td>
		<cmsc:select var="pagesIndex">
			<cmsc:option value="center" message="edit_defaults.pagesindex.center" />
			<cmsc:option value="forward" message="edit_defaults.pagesindex.forward" />
			<cmsc:option value="half-full" message="edit_defaults.pagesindex.half-full" />
		</cmsc:select>
	</td>
</tr>