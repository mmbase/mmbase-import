<form method="POST" name="<portlet:namespace />form_overview" action="<cmsc:renderURL><cmsc:param name="action" value="edit"/></cmsc:renderURL>" target="_parent">
	<input type="hidden" name="template" value="newsletter/subscription/change.jsp">
	<div>
		<b><fmt:message key="subscription.overview.title" /></b>
	</div>
	<div class="content">	
		<p><fmt:message key="subscription.overview.info" /></p><br>
	</div>
	<a href="javascript:document.forms['<portlet:namespace />form_overview'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" alt=""/> <fmt:message key="subscription.overview.buttontext" /></a>
</form>	
