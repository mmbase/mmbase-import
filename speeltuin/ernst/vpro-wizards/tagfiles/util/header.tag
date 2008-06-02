<%@ tag body-content="empty"  %>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<mm:cloud method="asis" jspvar="cloud">
    <div id="header">
        <c:set var="username" scope="request"><%= "" + cloud.getUser().getIdentifier() %></c:set>
        <div id="path"></div>
        <h6>
            ${username}
            <i onclick="alert('Veel plezier met deze redactieomgeving!\nRob Vermeulen & Jerry Den Ambtman\nEn koop een spaarlamp!');">|</i>
            <a href="${pageContext.request.contextPath}/edit/system/logout.jsp">uitloggen</a>
        </h6>
    </div>
</mm:cloud>
