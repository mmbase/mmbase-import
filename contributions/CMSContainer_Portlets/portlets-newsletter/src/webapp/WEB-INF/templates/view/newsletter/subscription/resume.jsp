<form method="POST" name="<portlet:namespace />form_resume" action="<cmsc:actionURL><cmsc:param name="action" value="resume"/></cmsc:actionURL>" target="_parent">
	<div>
		<b><fmt:message key="subscription.resume.title" /></b>
	</div>
	<div class="content">	
		<p><fmt:message key="subscription.resume.info" /></p>		
	</div>
	<p><a href="javascript:document.forms['<portlet:namespace />form_resume'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/><fmt:message key="subscription.resume.buttontext" /></a></p>
</form>
