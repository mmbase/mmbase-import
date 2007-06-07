<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="cmsc-knownvisitor-ntlm" scope="request" />

<html>
	<head>
		<title><fmt:message key="knownvisitor-ntlm.alternative_login.title" /></title>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/ntlm/css/alternativate_login.css">
<!-- 		<link rel="stylesheet" href="<%=request.getContextPath()%>/ntlm/css/alternative_login.css">-->
	</head>
<body>

<div class="login">
	<div class="title"><fmt:message key="knownvisitor-ntlm.alternative_login.title"/></div>
	<div class="intro"><fmt:message key="knownvisitor-ntlm.alternative_login.intro"/></div>
	
	<c:if test="${!empty failed}">
	<!-- failed:${failed} -->
	<div class="failed"><fmt:message key="knownvisitor-ntlm.alternative_login.failed"/></div>
	</c:if>
	
	<div class="form">
		<form action="${old_uri}" method="post">
		<input type="hidden" name="ntlm_alternative_login" value="true"/>
		<label for="username"><fmt:message key="knownvisitor-ntlm.alternative_login.username"/>:</label> <input type="text" name="username"/>
		<br/>
		<label for="password"><fmt:message key="knownvisitor-ntlm.alternative_login.password"/>:</label> <input type="password" name="password"/>
		<br/>
		<input type="submit" value="<fmt:message key="knownvisitor-ntlm.alternative_login.login"/>"/>
		</form>
	</div>
</div>
</body>
</html>