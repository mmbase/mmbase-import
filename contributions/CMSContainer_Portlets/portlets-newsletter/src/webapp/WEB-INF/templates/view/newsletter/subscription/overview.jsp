<mm:cloud>

<cmsc:portlet-preferences />

<form method="POST" name="<portlet:namespace />form_change" action="<cmsc:renderURL><cmsc:param name="action" value="change"/></cmsc:renderURL>" target="_self">
<input type="hidden" name="template" value="newsletter/subscription/change.jsp">

<div class="heading">
	<h3><fmt:message key="subscription.overview.title" /></h3>
</div>
<div class="content">
	<p><fmt:message key="subscription.subscribe.info" />
</div>
<br>
<a href="javascript:document.forms['<portlet:namespace />form_change'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" alt=""/> <fmt:message key="subscription.overview.buttontext" /></a>

</form>	
</mm:cloud>