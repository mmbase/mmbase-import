<div id="header">
    <c:set var="username" scope="request"><%= "" + cloud.getUser().getIdentifier() %></c:set>
    <div id="path"></div>
    <h6>
        ${username}
        <i onclick="alert('Veel plezier met deze redactieomgeving!\nRob Vermeulen & Jerry Den Ambtman\nEn koop een spaarlamp!');">|</i>
        <a href="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/logout.jsp">uitloggen</a>
    </h6>
</div>