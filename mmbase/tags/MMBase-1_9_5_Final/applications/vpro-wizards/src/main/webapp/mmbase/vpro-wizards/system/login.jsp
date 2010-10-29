<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
      <title>VPRO Wizards | Inloggen</title>
      <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/mmbase/vpro-wizards/stylesheets/edit.css"/>
      <script type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/javascript.js"></script>
    </head>
<body class="login">
<mm:import externid="referrer"/>
<mm:cloud method="logout" >
<div class="loginFields">
    <h3>VPRO Wizards</h3>
    <c:if test="${not empty param.username}">
        <b>naam, wachtwoord combinatie is verkeerd :-(</b>
    </c:if>
    <form method="post" name="login" action="<mm:write referid="referrer"/>">
        <div class="inputBlock">
                <div class="fieldName">Naam</div>
                <div class="fieldValue"><input type ="text" class="small" size="80" name="username" value="" /></div>
        </div>
        <div class="inputBlock">
                <div class="fieldName">Wachtwoord</div>
                <div class="fieldValue"><input type ="password" class="small" size="80" name="password" value="" /></div>
        </div>
        <div class="formButtons">
            <input class="submit" type="submit" name="command" value="login">
        </div>
    </form>
</div>
<script type="text/javascript">
    document.onLoad=document.login.username.focus();
</script>
</body>
</mm:cloud>
</html>
