<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%--

LOGIN RESULTS HERE

--%>	
<body onload="window.history.forward(1);">				
<c:choose>
	<c:when test="${sessionScope.logout == 'false'}">
		<form name="<portlet:namespace />form" method="post"
			action="<cmsc:actionURL><cmsc:param name="action" value="logout"/></cmsc:actionURL>">
			<table>
				<tr>
					<td>
						<fmt:message key="view.welcome" />
					</td>
				</tr>
				<tr>
					<td>
						<c:set var="firstName" scope="request" value="${sessionScope.firstName}"/>
						<c:out value="${firstName}"/>
						<c:set var="lastName" scope="request" value="${sessionScope.lastName}"/>
						<c:out value="${lastName}"/>
         	 		</td>
      			</tr>
      			<tr>
					<td colspan="2">
						<input type="submit" name="logout" value="<fmt:message key="view.logout" />"/>
					</td>
				</tr>
			</table>
		</form>
	</c:when>
<%--

LOGIN HERE

--%>
	<%--<c:when test="${sessionScope.logout == 'true'}">--%>
	<c:otherwise>
		<form name="<portlet:namespace />form" method="post"
			action="<cmsc:actionURL><cmsc:param name="action" value="login"/></cmsc:actionURL>">
			<table>
				<tr>
					<td>
						<fmt:message key="view.user" />
					</td>
					<td>
						<input type="text" id="j_username" name="userText" class="userText" value="${userText}" />
					</td>
				</tr>
				<tr>
					<td>
                    	<fmt:message key="view.pass" />
                	</td>
                	<td>
                    	<input type="password" id="j_password" name="passText" class="passText" value="${passText}" />
                	</td>
            	</tr>
				<tr>
					<td colspan="2">
						<input type="submit" name="login" value="<fmt:message key="view.login" />"/>
					</td>
				</tr>
			</table>
		</form>
	</c:otherwise>
	<%--</c:when>--%>
</c:choose>
</body>