<jsp:useBean id="newsletterBean" class="com.finalist.newsletter.module.NewsletterBean"scope="session" />
<jsp:useBean id="subscriptionBean" class="com.finalist.newsletter.module.SubscriptionBean"scope="session" />

<fmt:setBundle basename="newsletter-module" scope="request" />

<b><fmt:message key="module.subscription.title" /></b>

<mm:cloud>

<table width="50%">
	<tr>
		<th align="left"><fmt:message key="module.subscription.newslettertitle" /></th>
		<th align="left"><fmt:message key="module.subscription.themes" /></th>
		<th align="left"><fmt:message key="module.subscription.publications" /></th>
		<th align="left"><fmt:message key="module.subscription.subscriptions" /></th>
	</tr>
	<mm:listnodes type="newsletter">
	<mm:field name="number" id="newsletter" write="false" />
	<tr>
		<jsp:setProperty name="newsletterBean" property="newsletter" value="${newsletter}" />
		<td><a href="newsletterdetails.jsp?number=${number}"><mm:field name="title" write="true" /></a></td>
		<td><jsp:getProperty name="newsletterBean" property="themes" /></td>
		<td><jsp:getProperty name="newsletterBean" property="publications" /></td>
		<td><jsp:getProperty name="newsletterBean" property="subscriptions" /></td>
	</tr>
	</mm:listnodes>
</table>

<br><br>

<table width="50%">
	<tr>
		<th align="left"><fmt:message key="module.subscription.username" /></th>
		<th align="left"><fmt:message key="module.subscription.newsletters" /></th>
		<th align="left"><fmt:message key="module.subscription.themes" /></th>
	</tr>
	<c:forEach var="username" items="${subscribers}">
	<tr>
		<jsp:setProperty name="subscriptionBean" property="username" value="${username}" />
		<td><a href="subscriberdetail.jsp?name=${username}">${username}</a></td>
		<td><jsp:getProperty name="subscriptionBean" property="newsletters" /></td>
		<td><jsp:getProperty name="subscriptionBean" property="themes" /></td>
	</tr>
	</c:forEach>
</table>


</mm:cloud>