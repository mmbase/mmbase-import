<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="bannerglobals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:import externid="rows" jspvar="nodeList" vartype="List" />
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   	<cmscedit:head title="customer.title">
	    <script src="../repository/search.js" type="text/javascript"></script>
	    <script src="../repository/content.js" type="text/javascript"></script>
   	</cmscedit:head>
<body>
    <mm:import externid="action">search</mm:import>
    <%-- either: search of select --%>

    <%@include file="includes/tabs_by_position.jsp"%>
    <div class="editor" style="height:500px"><%@include file="includes/search_by_position.jsp"%>
    <div class="ruler_green">
    <div><fmt:message key="banner.search.results.positions" /></div>
    </div>

    <div class="body"><c:if test="${not empty rows}">
        <%@include file="includes/searchpages.jsp"%>
        <table>
            <tr class="listheader">
                <th><fmt:message key="banner.search.column.page" /></th>
                <th><fmt:message key="banner.search.column.position" /></th>
                <th><fmt:message key="banner.search.column.name" /></th>
                <th><fmt:message key="banner.search.column.title" /></th>
                <th><fmt:message key="banner.search.column.expiredate" /></th>
                <th><fmt:message key="banner.search.column.type" /></th>
                <th><fmt:message key="banner.search.column.from" /></th>
                <th><fmt:message key="banner.search.column.until" /></th>
                <th><fmt:message key="banner.search.column.maxclicks" /></th>
                <th><fmt:message key="banner.search.column.clicks" /></th>
            </tr>
            <tbody class="hover">
                <c:set var="useSwapStyle">true</c:set>
                <c:forEach items="${rows}" var="row" varStatus="status">
                    <tr <c:if test="${useSwapStyle}">class="swap"</c:if>>
                        <td>${row.page}</td>
                        <td>${row.position}</td>
                        <td>${row.name}</td>
                        <td>${row.title}</td>
                        <td><fmt:formatDate value="${row.expireDate}" dateStyle="SHORT" /></td>
                        <td>${row.type}</td>
                        <td><fmt:formatDate value="${row.startDate}" dateStyle="SHORT" /></td>
                        <td><fmt:formatDate value="${row.endDate}" dateStyle="SHORT" /></td>
                        <td>
                        <c:if test="${row.useMaxClicks == 'false'}">-</c:if>
                        <c:if test="${row.useMaxClicks != 'false'}">${row.maxClicks}</c:if>
                        </td>
                        <td>${row.clicks}</td>
                    </tr>
                    <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
                </c:forEach>
            </tbody>
        </table>
    </c:if> <c:if test="${empty rows}">
        <fmt:message key="banner.search.noresult.banners" />
    </c:if> <c:if test="${not empty rows}">
        <%@include file="includes/searchpages.jsp"%>
    </c:if></div>
</body>
</html:html>
</mm:content>
