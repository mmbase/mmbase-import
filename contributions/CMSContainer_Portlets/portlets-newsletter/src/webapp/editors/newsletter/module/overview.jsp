<%@include file="globals.jsp" %>

<div>
	<h3><fmt:message key="index.title" /></h3>
	<%@include file="globalstatistics.jsp" %>
</div>
<br />
<div>
	<p><a href="NewsletterAction.do?action=overview"><fmt:message key="index.link.newsletteroverview" /></a></p>
	<p><a href="SubscriptionAction.do?action=overview"><fmt:message key="index.link.subscriptionoverview" /></a></p>
</div>
