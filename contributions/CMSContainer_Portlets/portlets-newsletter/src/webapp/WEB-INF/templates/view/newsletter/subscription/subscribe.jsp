<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<cmsc:portlet-preferences />

<mm:cloud>
	<form method="POST" name="<portlet:namespace />form_subscribe" action="<cmsc:actionURL><cmsc:param name="action" value="subscribe"/></cmsc:actionURL>" target="_parent">
	<input type="hidden" name="template" value="newsletter/subscription/overview.jsp">
	<div class="heading">
		<h3><fmt:message key="subscription.subscribe.title" /></h3>
	</div>
	<div class="content">
		<p><fmt:message key="subscription.subscribe.info" /></p><br>
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
				<td colspan="2"><cmsc:checkbox var="newslettersubscriptions" value="${newsletternumber}" checked="true" /><fmt:message key="subscription.subscribe.tothisnewsletter" /></td></tr>
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
					<td width="25%"><cmsc:checkbox var="newslettertheme" value="${theme}" checked="true">${themeTitle}</cmsc:checkbox></td> 
					<td>${themeShortDescription}</td>
				</tr>				
			</mm:relatednodes>
			<tr><td>&nbsp;</td></tr>
			</mm:node>
			</c:forEach>
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
		</table>
		<br>
		<a href="javascript:document.forms['<portlet:namespace />form_subscribe'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/><fmt:message key="subscription.subscribe.buttontext" /></a>
		<br>
	</div>
	</form>
</mm:cloud>



