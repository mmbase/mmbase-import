<%@include file="globals.jsp" %>

<cmscedit:head title="reactions.title">
	<fmt:message key="subscriptiondetail.title" />
</cmscedit:head>

<br><br>

<jsp:useBean id="subscriptionDetailBean" scope="request" class="com.finalist.newsletter.module.bean.SubscriptionDetailBean" />
<c:set var="userName" value="${subscriptionDetailBean.userName}" />

<table width="50%">
	<tr>
		<th align="left"><fmt:message key="subscriptiondetail.username" /></tk>
		<td><jsp:getProperty name="subscriptionDetailBean" property="userName" /></td>
	</tr>
	<tr>
		<th align="left"><fmt:message key="subscriptiondetail.emailaddress" /></tk>
		<td><jsp:getProperty name="subscriptionDetailBean" property="emailAddress" /></td>
	</tr>
	<tr>
		<th align="left"><fmt:message key="subscriptiondetail.status" /></tk>
		<td><jsp:getProperty name="subscriptionDetailBean" property="status" /></td>
	</tr>
	<tr>
		<th align="left"><fmt:message key="subscriptiondetail.mimetype" /></tk>
		<td><jsp:getProperty name="subscriptionDetailBean" property="mimeType" /></td>
	</tr>
</table>

<br /><a href="SubscriptionAction.do?action=update&username=${userName}"><fmt:message key="subscriptiondetail.link.update" /></a>
<br /><a href="SubscriptionAction.do?action=pause&username=${userName}"><fmt:message key="subscriptiondetail.link.pause" /></a>
<br /><a href="SubscriptionAction.do?action=resume&username=${userName}"><fmt:message key="subscriptiondetail.link.resume" /></a>
<br /><a href="SubscriptionAction.do?action=unsubscribe&username=${userName}"><fmt:message key="subscriptiondetail.link.unsubscribe" /></a>
<br /><a href="SubscriptionAction.do?action=terminate&username=${userName}"><fmt:message key="subscriptiondetail.link.terminate" /></a>