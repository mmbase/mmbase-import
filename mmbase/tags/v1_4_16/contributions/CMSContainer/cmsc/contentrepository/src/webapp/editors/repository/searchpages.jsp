<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!--todo remove this file and use pages.jsp instead.-->

<c:if test="resultCount eq 0">
    <p><fmt:message key="searchpages.nonefound"/></p>
</c:if>

<%
    int  resultsPerPage = Integer.parseInt(com.finalist.cmsc.mmbase.PropertiesUtil.getProperty("repository.search.results.per.page"));
    pageContext.setAttribute("resultsPerPage", resultsPerPage);
%>

<c:set var="listSize" value="${resultCount}"/>
<c:set var="offset" value="${param.offset}"/>

<%@ include file="../searchpage.jsp" %>