<%@include file="globals.jsp" %>

<cmscedit:head title="reactions.title">
	<fmt:message key="newsletterdetail.title" />: ${newsletterDetailBean.title}
</cmscedit:head>

<br><br>

<table width="50%">
	<tr>
		<th></th>
		<th align="left"><fmt:message key="newsletterdetail.username" /></tk>
		<th align="left"><fmt:message key="newsletterdetail.numberofthemes" /></tk>
	</tr>
	<c:forEach var="subscriber" items="${subscriberOverviewBeans}">
	<c:url var="url" value="ReportSubscriberSubscriptions.do?username=${subscriber.userName}" />
	<tr>
		<td><cmsc:checkbox var="checked" value="${subscriber.userName}" /></td>
		<td><a href="${url}"><jsp:getProperty name="subscriber" property="userName" /></a></td>
		<td><jsp:getProperty name="subscriber" property="numberOfThemes" /></td>
	</tr>
	</c:forEach>
</table>