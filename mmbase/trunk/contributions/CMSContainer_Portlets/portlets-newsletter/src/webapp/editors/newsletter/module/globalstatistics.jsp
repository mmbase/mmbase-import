<%@include file="globals.jsp" %>

<jsp:useBean id="globalOverviewBean" scope="request" type="com.finalist.newsletter.module.bean.GlobalOverviewBean" />

<b><td><fmt:message key="module.stats.title" /></td></b>
<table width="50%">
	<tr>
		<td><fmt:message key="module.stats.total.newsletters" /></td>
		<td><jsp:getProperty name="globalOverviewBean" property="numberOfNewsletters"  /></td>
	</tr>
	<tr>
		<td><fmt:message key="module.stats.total.themes" /></td>
		<td><jsp:getProperty name="globalOverviewBean" property="numberOfThemes"  /></td>
	</tr>
	<tr>
		<td><fmt:message key="module.stats.total.publications" /></td>
		<td><jsp:getProperty name="globalOverviewBean" property="numberOfPublications"  /></td>
	</tr>
	<tr>
		<td><fmt:message key="module.stats.total.subscriptions" /></td>
		<td><jsp:getProperty name="globalOverviewBean" property="numberOfSubscribtions"  /></td>
	</tr>
</table>