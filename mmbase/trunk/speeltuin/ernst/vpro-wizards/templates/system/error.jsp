<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%--
<%@ taglib uri="/WEB-INF/taglibs-mailer.tld" prefix="mt" %>
--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>3V12 Redactie Error</title>
    <link rel="stylesheet" type="text/css" href="/edit/stylesheets/edit.css"/>
</head>
<body class="error">
<div class="message">
    <h4>FOUT</h4>
    <mm:import id="text">
        <c:if test="${param.error == 1}">
            Je hebt geen rechten om dingen uit te lichten.<br/>
            Neem contact op met Ron van der Sterren (12560).
        </c:if>
        <c:if test="${param.error == 2}">
            Je hebt geen rechten om voor categorien en / of bookmarkfolders.<br/>
            Neem contact op met Ron van der Sterren (12560).
        </c:if>
        <c:if test="${param.error == 3}">
            Je heb geen rechten om een relatie aan te maken tussen ${param.source} en ${param.destination}
            Neem contact op met Ron van der Sterren (12560).
        </c:if>

        <c:if test="${not empty errors}">
            <c:forEach var="error" items="${errors}">
                ${error.message}<br/>
            </c:forEach>
        </c:if>
    </mm:import>
    <mm:write referid="text"/>
    <a href="javascript:location=document.referrer;">terug</a>
</div>
<mm:cloud jspvar="cloud" method="loginpage" loginpage="/edit/login.jsp">
    <mm:import id="user"><%= "" + cloud.getUser().getIdentifier() %></mm:import>
</mm:cloud>

<%--
<mt:mail>
    <mt:server>localhost</mt:server>
    <mt:setrecipient type="to">r.vermeulen@vpro.nl,R.vander.Sterren@vpro.nl</mt:setrecipient>
    <mt:from>3V12Redactiesysteem@vpro.nl</mt:from>
    <mt:subject>Error 3V12 Redactiesysteem</mt:subject>
    <mt:message>
<mm:write referid="text"/>
User: <mm:write referid="user"/>
Referer: <%=request.getHeader("referer")%>
URI: <%="http://"+request.getServerName()+":"+request.getServerPort()+request.getRequestURI()+"?"+request.getQueryString()%>
    </mt:message>
<mt:send/>
</mt:mail>
--%>

</body>
</html>