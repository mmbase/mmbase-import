<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>
<%--
    this tag sets a cookie with the url of the current page.
--%>
<c:set var="qs">${pageContext.request.queryString}</c:set>
<c:set var="referer" value="${pageContext.request.requestURI}"/>
<mm:import id="url" >${referer}<c:if test="${not empty qs}">?${qs}</c:if></mm:import>
<c:set var="url_escaped"><mm:write referid="url" escape="url"/></c:set>
<util:setcookie name="referer" value="${url_escaped}" path="${pageContext.request.contextPath}/" />
