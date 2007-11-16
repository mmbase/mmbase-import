<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<mm:cloud>
	<form method="POST" name="<portlet:namespace />form_change" action="<cmsc:actionURL><cmsc:param name="action" value="change"/></cmsc:actionURL>" target="_parent">
	<input type="hidden" name="template" value="newsletter/subscription/options.jsp">
	<div class="heading">
		<h3><fmt:message key="subscription.change.title" /></h3>
	</div>
	<div class="content">
		<p><fmt:message key="subscription.change.info" /></p>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<mm:listnodes type="newsletter">
			<tr>
				<td colspan="2">
					<b><mm:field name="title" write="true" /></b>
				</td>					
			</tr>
			<tr>
			<td colspan="2"><mm:field name="description" write="true" />
			</td>
			</tr>
			<mm:relatednodes type="newslettertheme" role="related">
				<mm:field name="number" write="false" jspvar="theme"/>
				<mm:field name="title" write="false" jspvar="themeTitle"/>
				<mm:field name="shortdescription" write="false" jspvar="themeShortDescription"/>
				<tr>				
					<td width="25%"><cmsc:checkbox var="newslettertheme" value="${theme}">${themeTitle}</cmsc:checkbox></td> 
					<td>${themeShortDescription}</td>
				</tr>				
			</mm:relatednodes>
			<tr><td>&nbsp;</td></tr>
			</mm:listnodes>
			<tr><td colspan="2"><fmt:message key="subscription.mimetype.info" /></td></tr>
			<tr>
				<td><fmt:message key="subscription.mimetype.select" /></td>
				<td>
				<cmsc:select var="mimetype">
				
				</cmsc:select>
				</td>
			</tr>
		</table>
		<br>
		<a href="javascript:document.forms['<portlet:namespace />form_change'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/><fmt:message key="subscription.change.buttontext" /></a>
		<br>
	</div>
	</form>
</mm:cloud>



