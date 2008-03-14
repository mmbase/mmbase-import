<form method="POST" name="<portlet:namespace />form_subscribe" action="<cmsc:renderURL><cmsc:param name="action" value="subscribe"/></cmsc:renderURL>" target="_self">
<input type="hidden" name="template" value="newsletter/subscription/subscribe.jsp">

<div class="heading">
	<h3><fmt:message key="subscription.introduction.title" /></h3>
</div>
<div class="content">
	<p><fmt:message key="subscription.introduction.info" />
</div>
<br>
<a href="javascript:document.forms['<portlet:namespace />form_subscribe'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" alt=""/> <fmt:message key="subscription.introduction.buttontext" /></a>

</form>	
