<%-- comma separated values of banners, used for export to Excel --%>
<%-- the comments are there to remove whitespace, sorry --%><%--
--%><%@page language="java" contentType="text/plain;charset=utf-8"%><%--
--%><%@include file="bannerglobals.jsp"%><%--
--%><c:set var="SEP" value="," /><%--
--%><fmt:message key="banner.search.column.page" />${SEP}<%--
--%><fmt:message key="banner.search.column.position" />${SEP}<%--
--%><fmt:message key="banner.search.column.name" />${SEP}<%--
--%><fmt:message key="banner.search.column.title" />${SEP}<%--
--%><fmt:message key="banner.search.column.use_maxclicks" />${SEP}<%--
--%><fmt:message key="banner.search.column.maxclicks" />${SEP}<%--
--%><fmt:message key="banner.search.column.expiredate" />${SEP}<%--
--%><fmt:message key="banner.search.column.type" />${SEP}<%--
--%><fmt:message key="banner.search.column.from" />${SEP}<%--
--%><fmt:message key="banner.search.column.until" />${SEP}<%--
--%><fmt:message key="banner.search.column.clicks" /><%--
--%><c:if test="${not empty rows}">
<c:forEach items="${rows}" var="row" varStatus="status"><%--
                        --%>${row.page}${SEP}<%--
                        --%>${row.position}${SEP}<%--
                        --%>${row.name}${SEP}<%--
                        --%>${row.title}${SEP}<%--
                        --%>${row.useMaxClicks}${SEP}<%--
                        --%>${row.maxClicks}${SEP}<%--
                        --%><fmt:formatDate value="${row.expireDate}" dateStyle="SHORT" />${SEP}<%--
                        --%>${row.type}${SEP}<%--
                        --%><fmt:formatDate value="${row.startDate}" dateStyle="SHORT" />${SEP}<%--
                        --%><fmt:formatDate value="${row.endDate}" dateStyle="SHORT" />${SEP}<%--
                        --%>${row.clicks}
</c:forEach><%--
--%></c:if>
