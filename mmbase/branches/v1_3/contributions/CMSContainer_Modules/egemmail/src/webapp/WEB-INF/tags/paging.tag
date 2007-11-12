<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@attribute name="offset" required="true" %>
<%@attribute name="resultsPerPage" required="true" %>
<%@attribute name="totalNumberOfResults" required="true" %>

<table border="0" width="100%">
    <tr>
        <td style="width:50%;">
            <fmt:message key="searchpages.showresults">
                <fmt:param>${offset * resultsPerPage + 1}</fmt:param>
                <fmt:param>${(totalNumberOfResults > (offset+1) * resultsPerPage)?((offset+1) * resultsPerPage):totalNumberOfResults}</fmt:param>
                <fmt:param>${totalNumberOfResults}</fmt:param>
            </fmt:message>
        </td>
        <td style="text-align:right;width:50%;">
            <fmt:message key="searchpages.page" />
            <c:set var="maxPage" value="${totalNumberOfResults/resultsPerPage - ((totalNumberOfResults > 0 && totalNumberOfResults mod resultsPerPage == 0)?1:0)}"/>
            <c:forEach var="count" begin="0" end="${maxPage}">
                <c:choose>
                    <c:when test="${(count == 0 && offset == null) || count == offset}">
                        ${count+1}
                    </c:when>
                    <c:otherwise>
                        <a href="#" onclick="doChangePage(${count})">${count+1}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </td>
    </tr>
</table>