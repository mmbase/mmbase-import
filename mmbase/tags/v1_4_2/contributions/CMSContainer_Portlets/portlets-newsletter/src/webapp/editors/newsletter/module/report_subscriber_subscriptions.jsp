<%@include file="globals.jsp" %>

<cmscedit:head title="reactions.title">
	<fmt:message key="subscriptiondetail.title" />
</cmscedit:head>

<br><br>

<jsp:useBean id="subscriptionDetailBean" scope="request" class="com.finalist.newsletter.module.bean.SubscriptionDetailBean" />
<c:set var="userName" value="${subscriptionDetailBean.userName}" />

<mm:cloud>
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
		<td>
			<cmsc:select var="mimetype">
				<c:forEach var="m" items="${bean.availableMimeTypes}">
					<cmsc:option name="${m}" value="${m}" />
				</c:forEach>
			</cmsc:select>
		</td>
	</tr>
	<tr>
		<th align="left"><fmt:message key="subscriptiondetail.mimetype" /></tk>
		<td>
			<cmsc:select var="status">
				<c:forEach var="s" items="${bean.availableStatusOptions}">
					<cmsc:option name="${s}" value="${s}" />
				</c:forEach>
			</cmsc:select>
		</td>

	</tr>
</table>
<br><br>
<table width="75%">
	<mm:listnodes type="newsletter">
	<mm:field name="number" jspvar="newsletternumber" write="false" />
		<tr>
			<td width="10px"><cmsc:checkbox var="newslettersubscriptions" value="${newsletternumber}" />
			<td colspan="2"><mm:field name="title" write="true" />	</td>
		</tr>		
		<mm:relatednodes type="newslettertheme" role="newslettertheme">
		<mm:field name="number" jspvar="themenumber" write="false" />
		<tr><td>&nbsp;</td>
			<td width="10px"><cmsc:checkbox var="themesubscriptions" value="${themenumber}" />
			<td><mm:field name="title" write="true" />	</td>
		</tr>
		</mm:relatednodes>
	</mm:listnodes>
</table>
</mm:cloud>

<br /><a href="SubscriptionAction.do?action=update&username=${userName}"><fmt:message key="subscriptiondetail.link.update" /></a>
<br /><a href="SubscriptionAction.do?action=pause&username=${userName}"><fmt:message key="subscriptiondetail.link.pause" /></a>
<br /><a href="SubscriptionAction.do?action=resume&username=${userName}"><fmt:message key="subscriptiondetail.link.resume" /></a>
<br /><a href="SubscriptionAction.do?action=unsubscribe&username=${userName}"><fmt:message key="subscriptiondetail.link.unsubscribe" /></a>
<br /><a href="SubscriptionAction.do?action=terminate&username=${userName}"><fmt:message key="subscriptiondetail.link.terminate" /></a>