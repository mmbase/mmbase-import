<mm:cloud>

<cmsc:portlet-preferences />

<div class="newsletter_subscription">
   <form method="post" name="<portlet:namespace />form_change" action="<cmsc:renderURL><cmsc:param name="action" value="change"/></cmsc:renderURL>" target="_self">
   <fieldset>
      <input type="hidden" name="template" value="newsletter/subscription/change.jsp"/>
   	<h3><fmt:message key="subscription.overview.title" /></h3>
      
   	<p><fmt:message key="subscription.subscribe.info" /></p>

      <input type="submit" value="<fmt:message key="subscription.overview.buttontext" />" />
      
   </fieldset>
   </form>
</div>
	
</mm:cloud>