<%@include file="globals.jsp" %>

<cmscedit:head title="reactions.title">
	<fmt:message key="newsletterdetail.title" />: ${newsletterDetailBean.title}
</cmscedit:head>

<br><br>

<table width="50%">
	<tr>
		<th align="left"><fmt:message key="newsletterdetail.username" /></tk>
		<th align="left"><fmt:message key="newsletterdetail.numberofthemes" /></tk>
	</tr>
	<c:forEach var="bean" items="${newsletterDetailBean.subscribers}">
	<c:url var="url" value="SubscriptionAction.do?action=detail&username=${bean.userName}" />
	<tr>
		<td><a href="${url}"><jsp:getProperty name="bean" property="userName" /></a></td>
		<td><jsp:getProperty name="bean" property="numberOfThemes" /></td>
	</tr>
	</c:forEach>
</table>