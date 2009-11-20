<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>

<%@ attribute name="text"  %>
<%@ attribute name="relationrole"  description="can be picked up from request scope, defaults to 'related'" %>
<%@ attribute name="searchdir"  description="can be picked up from request scope, defaults to 'destination'" %>
<%@ attribute name="parentnodenr"  description="if you want to link the new node to some 'parent' node."  %>
<%@ attribute name="wizardfile" required="true"  %>

<util:getParams var="extraparams" exclude="nodenr"/>

<c:if test="${empty relationrole}" >
    <c:choose>
        <c:when test="${not empty requestScope.relationrole}"><c:set var="relationrole" value="${requestScope.relationrole}" /></c:when>
        <c:otherwise><c:set var="relationrole" value="related"/> </c:otherwise>
    </c:choose>
</c:if>

<c:if test="${empty searchdir}" >
    <c:choose>
        <c:when test="${not empty requestScope.searchdir}"><c:set var="searchdir" value="${requestScope.searchdir}" /></c:when>
        <c:otherwise><c:set var="searchdir" value="destination"/> </c:otherwise>
    </c:choose>
</c:if>




<div class="add">
    <c:url value="${wizardfile}.jsp" var="link">
        <c:if test="${not empty relationrole}">
            <c:param name="relationrole" value="${relationrole}" />
            <c:if test="${not empty searchdir}"><c:param name="searchdir" value="${searchdir}" /></c:if>
            <c:if test="${not empty searchdir}"><c:param name="parentnodenr" value="${parentnodenr}" /></c:if>
        </c:if>
    </c:url>
    <a href="${link}&${extraparams}" class="addButton">
    <c:choose>
        <c:when test="${not empty text}"> ${text} </c:when>
        <c:otherwise>
        nieuw(e) ${wizardfile}</c:otherwise>
    </c:choose>
     <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/new.png" class="icon" border="0" /></a>
</div>