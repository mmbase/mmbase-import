<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="guestbookglobals.jsp"%>
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

    <%@include file="includes/tabs_by_guestmessage.jsp"%>
    <div class="editor" style="height:500px"><%@include file="includes/search_by_guestmessage.jsp"%>
    <div class="ruler_green">
    <div><fmt:message key="guestbook.search.results.guestmessages" /></div>
    </div>

    <div class="body"><c:if test="${not empty rows}">
        <%@include file="includes/searchpages.jsp"%>
        <table>
            <tr class="listheader">
                <th></th>
                <th><fmt:message key="guestbook.search.column.name" /></th>
                <th><fmt:message key="guestbook.search.column.email" /></th>
                <th><fmt:message key="guestbook.search.column.title" /></th>
                <th><fmt:message key="guestbook.search.column.body" /></th>
            </tr>
            <tbody class="hover">
                <c:set var="useSwapStyle">true</c:set>
                <c:forEach items="${rows}" var="row" varStatus="status">
                    <tr <c:if test="${useSwapStyle}">class="swap"</c:if>>
                        <td style="white-space:nowrap;">
                                <a href="<mm:url page="DeleteGuestMessageAction.do" >
                                            <mm:param name="objectnumber">${row.number}</mm:param>
                                            <mm:param name="isRemote">${param.remote eq 'on'}</mm:param>
                                            <mm:param name="returnurl" value='<%="/editors/resources/SearchGuestBookAction.do" + request.getAttribute("geturl")%>' />
                                         </mm:url>">
                                <img src="../gfx/icons/delete.png" title="<fmt:message key="reactionsearch.icon.delete" />"/></a>
                        </td>
                        <td>${row.name}</td>
                        <td>${row.email}</td>
                        <td>${row.title}</td>
                        <td>${row.body}</td>
                    </tr>
                    <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
                </c:forEach>
            </tbody>
        </table>
    </c:if> <c:if test="${empty rows}">
        <fmt:message key="guestbook.search.noresult.guestmessages" />
    </c:if> <c:if test="${not empty rows}">
        <%@include file="includes/searchpages.jsp"%>
    </c:if></div>
</body>
</html:html>
</mm:content>