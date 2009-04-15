<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<cmsc:portlet-preferences />

<mm:cloud>
	<form method="post" name="<portlet:namespace />form_change" action="<cmsc:actionURL><cmsc:param name="action" value="change"/></cmsc:actionURL>" target="_parent">
	<input type="hidden" name="template" value="newsletter/change/overview.jsp"/>
	<div class="heading">
		<h3><fmt:message key="subscription.change.title" /></h3>
	</div>
	<div class="content">
		<p><fmt:message key="subscription.change.info" /></p><br>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<c:forEach var="newsletternumber" items="${allowednewsletters}">
		<mm:node number="${newsletternumber}" notfound="skip">
			<tr>
				<td colspan="2">
					<b><fmt:message key="newsletter" />: <mm:field jspvar="newslettertitle" name="title" write="true" /></b>
				</td>					
			</tr>
			<tr>
				<td colspan="2"><mm:field name="intro" write="true" />	</td>
			</tr>
			<tr>
				<td colspan="2"><cmsc:checkbox var="newslettersubscriptions" value="${newsletternumber}" /><fmt:message key="subscription.subscribe.tothisnewsletter" /></td></tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2"><b><fmt:message key="additionalthemes" />: ${newslettertitle}</b></td>
			</tr>
			<mm:relatednodes type="newslettertheme" role="newslettertheme">
				<mm:field name="number" write="false" jspvar="theme"/>
				<mm:field name="title" write="false" jspvar="themeTitle"/>
				<mm:field name="shortdescription" write="false" jspvar="themeShortDescription"/>
				<tr>				
					<td width="25%"><cmsc:checkbox var="newslettertheme" value="${theme}">${themeTitle}</cmsc:checkbox></td> 
					<td>${themeShortDescription}</td>
				</tr>				
			</mm:relatednodes>
			<tr><td>&nbsp;</td></tr>
			</mm:node>
			</c:forEach>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr><td colspan="2"><b><fmt:message key="subscription.mimetype.title" /></b></td></tr>
			<tr><td colspan="2"><fmt:message key="subscription.mimetype.info" /></td></tr>
			<tr>
				<td><fmt:message key="subscription.mimetype.select" /></td>
				<td>
				<cmsc:select var="preferredmimetype">
					<c:forEach var="m" items="${mimetypeoptions}">
						<cmsc:option name="${m}" value="${m}" />
					</c:forEach>
				</cmsc:select>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr><td colspan="2"><b><fmt:message key="subscription.status.title" /></b></td></tr>
			<tr><td colspan="2"><fmt:message key="subscription.status.info" /></td></tr>
			<tr>
				<td><fmt:message key="subscription.status.select" /></td>
				<td>
				<cmsc:select var="subscriptionstatus">
					<c:forEach var="s" items="${statusoptions}">
						<cmsc:option name="${s}" value="${s}" />
					</c:forEach>
				</cmsc:select>
				</td>
			</tr>

		</table>
		<br>
		<a href="javascript:document.forms['<portlet:namespace />form_change'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/><fmt:message key="subscription.change.buttontext" /></a>
		<br>
	</div>
	</form>
	<br>
	<form method="post" name="<portlet:namespace />form_terminate" action="<cmsc:actionURL><cmsc:param name="action" value="terminate"/></cmsc:actionURL>" target="_parent">
	<input type="hidden" name="template" value="newsletter/change/overview.jsp"/>
		<div>
			<b><fmt:message key="subscription.terminate.title" /></b>
		</div>
		<div class="content">	
			<p><fmt:message key="subscription.terminate.info" /></p>		
			<p><cmsc:checkbox var="confirm_unsubscribe" value="1"><fmt:message key="subscription.terminate.confirm"  /></cmsc:checkbox></p>
		</div>
		<p><a href="javascript:document.forms['<portlet:namespace />form_terminate'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/><fmt:message key="subscription.terminate.buttontext" /></a></p>
	</form>
</mm:cloud>