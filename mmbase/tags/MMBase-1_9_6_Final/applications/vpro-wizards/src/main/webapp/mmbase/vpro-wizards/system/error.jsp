<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>VPRO-Wizards error</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/mmbase/vpro-wizards/stylesheets/edit.css"/>
</head>
<body class="error">
<h4>FOUT</h4>
    <p>Er is iets mis gegaan bij het verwerken van een van de opdrachten:</p>
    <c:forEach var="error" items="${globalErrors}">
        <div class="message error"> ${error.message}</div>
        <c:forEach var="pr" items="${error.properties}" >
        </c:forEach>
    </c:forEach>
    <a href="javascript:location=document.referrer;">terug</a>
</body>
</html>