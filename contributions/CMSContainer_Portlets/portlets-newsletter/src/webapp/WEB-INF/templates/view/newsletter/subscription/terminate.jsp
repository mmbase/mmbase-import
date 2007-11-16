<form method="POST" name="<portlet:namespace />form_terminate" action="<cmsc:actionURL><cmsc:param name="action" value="terminate"/></cmsc:actionURL>" target="_parent">
	<div>
		<b><fmt:message key="subscription.terminate.title" /></b>
	</div>
	<div class="content">	
		<p><fmt:message key="subscription.terminate.info" /></p>		
		<p><cmsc:checkbox var="confirm_unsubscribe" value="1"><fmt:message key="subscription.terminate.confirm"  /></cmsc:checkbox></p>
	</div>
	<p><a href="javascript:document.forms['<portlet:namespace />form_terminate'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/><fmt:message key="subscription.terminate.buttontext" /></a></p>
</form>
