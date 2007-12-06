<jsp:useBean id="statsBean" class="com.finalist.newsletter.module.StatisticsBean"scope="session" />

<fmt:setBundle basename="newsletter-module" scope="request" />

<b><td><fmt:message key="module.stats.total.title " /></td></b>
<table>
	<tr>
		<td><fmt:message key="module.stats.total.newsletters " /></td>
		<td><jsp:getProperty name="statsBean" property="totalNewsletters"  /></td>
	</tr>
	<tr>
		<td><fmt:message key="module.stats.total.themes " /></td>
		<td><jsp:getProperty name="statsBean" property="totalThemes"  /></td>
	</tr>
	<tr>
		<td><fmt:message key="module.stats.total.publications " /></td>
		<td><jsp:getProperty name="statsBean" property="totalPublications"  /></td>
	</tr>
</table>