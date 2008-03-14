<%@include file="globals.jsp" %>

<cmscedit:head title="reactions.title">
	<fmt:message key="subscriptionoverview.title" />
</cmscedit:head>

<br><br>

<table width="50%">
	<tr>
		<th align="left"><fmt:message key="subscriptionoverview.username" /></tk>
		<th align="left"><fmt:message key="subscriptionoverview.status" /></tk>
		<th align="left"><fmt:message key="subscriptionoverview.mimetype" /></tk>
		<th align="left"><fmt:message key="subscriptionoverview.numberofnewsletters" /></tk>
		<th align="left"><fmt:message key="subscriptionoverview.numberofthemes" /></tk>
	</tr>
	<c:forEach var="bean" items="${subscriptionoverviewBeans}">
	<c:url var="url" value="ReportSubscriberSubscriptions.do?name=${bean.userName}" />
	<tr>
		<td><a href="${url}"><jsp:getProperty name="bean" property="userName" /></a></td>
		<td><jsp:getProperty name="bean" property="status" /></td>
		<td><jsp:getProperty name="bean" property="mimeType" /></td>
		<td><jsp:getProperty name="bean" property="numberOfNewsletters" /></td>
		<td><jsp:getProperty name="bean" property="numberOfThemes" /></td>
	</tr>
	</c:forEach>
</table>