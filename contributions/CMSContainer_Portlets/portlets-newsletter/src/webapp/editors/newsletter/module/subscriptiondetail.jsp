<%@include file="globals.jsp" %>

<b><fmt:message key="subscriptiondetail.title" /></b>

<jsp:useBean id="subscriptionDetailBean" scope="request" class="com.finalist.newsletter.module.bean.SubscriptionDetailBean" />

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