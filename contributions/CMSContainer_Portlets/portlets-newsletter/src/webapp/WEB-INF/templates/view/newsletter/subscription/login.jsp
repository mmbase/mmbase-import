<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<% session.setAttribute("username", "Jasper Stroomer"); %>

<form method="POST" name="<portlet:namespace />form_new" action="<cmsc:renderURL></cmsc:renderURL>" target="_self">

	<div class="heading">
		<h3><fmt:message key="subscription.login.title" /></h3>
	</div>
	<div class="content">
		<p><fmt:message key="subscription.login.info" /></p>
	</div>
<br>
<a href="javascript:document.forms['<portlet:namespace />form_new'].submit()" class="button"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" alt=""/> <fmt:message key="subscription.login.buttontext" /></a>
				
</form>	
