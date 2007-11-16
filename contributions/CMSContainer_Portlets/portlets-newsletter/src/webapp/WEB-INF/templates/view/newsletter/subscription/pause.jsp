<form method="POST" name="<portlet:namespace />form_pause" action="<cmsc:actionURL><cmsc:param name="action" value="pause"/></cmsc:actionURL>" target="_parent">
	<div>
		<b><fmt:message key="subscription.pause.title" /></b>
	</div>
	<div class="content">	
		<p><fmt:message key="subscription.pause.info" /></p>		
		<p><cmsc:checkbox var="confirm_pause" value="1"><fmt:message key="subscription.pause.confirm"  /></cmsc:checkbox></p>
	</div>
	<p><a href="javascript:document.forms['<portlet:namespace />form_pause'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/><fmt:message key="subscription.pause.buttontext" /></a></p>
</form>
