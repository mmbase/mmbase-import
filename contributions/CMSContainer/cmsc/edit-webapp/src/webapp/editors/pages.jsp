<%@ page import="org.apache.commons.lang.RandomStringUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>

<!--todo : get default page size from system properties.-->

<fmt:bundle basename="cmsc">
    <fmt:message key="pages.message" var="error"/>
    <fmt:message key="pages.go" var="go"/>
    <fmt:message key="searchpages.showresults" var="searchresult">
        <fmt:param>${offset * resultsPerPage +1}</fmt:param>
        <fmt:param>${(listSize > (offset+1) * resultsPerPage)?((offset+1) * resultsPerPage):listSize }</fmt:param>
        <fmt:param>${listSize}</fmt:param>
    </fmt:message>
</fmt:bundle>


<script language="JavaScript">

    function gotopage(targetfield) {

        var inputValue = document.getElementById(targetfield).value;
        var re = new RegExp("^[1-9][0-9]*$");

        if (re.test(inputValue) && inputValue <= Math.ceil(${maxPage})) {
            var url = "?status=&offset=" + (inputValue - 1) + "&orderby="+"${extraparams}";
            window.location.href = url;
        } else {
            alert("${error}");
        }
    }

    function enterto(event, targetfield) {
        if (event.keyCode == 13) {
            gotopage(targetfield)
        }
    }
</script>

<c:set var="baselink">
    ?status=${status}&orderby=${orderby}${extraparams}
</c:set>

<c:if test="${empty offset}"><c:set var="offset" value="0"/></c:if>

<c:set var="maxPage" value="${ cmsc:ceil(listSize/resultsPerPage)-1}"/>

<table border="0" width="100%">
    <tr>
        <td style="width:50%;">
            ${searchresult}
        </td>
        <td style="text-align:right;width:50%;">
            <fmt:message key="searchpages.page"/>
            <c:choose>
                <c:when test="${maxPage>=0&&maxPage<13}">
                    <c:forEach var="count" begin="0" end="${maxPage}">
                        <a href="${baselink}&offset=${count}" class="page_list_nav${offset==count}">
                                ${count+1}
                        </a>
                        <c:if test="count ne maxPage"> | </c:if>
                    </c:forEach>
                </c:when>

                <c:when test="${maxPage>=13}">
                    <c:set var="length_of_prefix" value="${offset <6 ? offset+2 : 2}"/>
                    <c:forEach var="count" begin="0" end="${length_of_prefix}">
                        <a href="${baselink}&offset=${count}" class="page_list_nav${offset==count}">
                                ${count+1}
                        </a>
                        |
                    </c:forEach>
                    ...

                    <c:if test="${5<offset&&offset <maxPage-5}">
                        <c:forEach var="count" begin="${offset-2}" end="${offset+2}">
                            <a href="${baselink}&offset=${count}" class="page_list_nav${offset==count}">
                                    ${count+1}
                            </a>
                            |
                        </c:forEach>
                        ...
                    </c:if>

                    <c:set var="length_of_postfix" value="${offset>(maxPage-6) ? offset-2 : (maxPage-2)}"/>
                    <c:forEach var="count" begin="${length_of_postfix}" end="${maxPage}">
                        <a href="${baselink}&offset=${count}" class="page_list_nav${offset==count}">
                                ${count+1}
                        </a>
                        |
                    </c:forEach>
                </c:when>

                <c:otherwise>
                    The page you requested does not exist.  ${maxPage}
                </c:otherwise>

            </c:choose>

            <c:set var="targetfield">
                <%=RandomStringUtils.randomAlphabetic(5)%>
            </c:set>


            <input type="text" name="targetpage" id="${targetfield}" size="4"
                   onKeyPress="enterto(event,'${targetfield}')"/>

            <input type="button" id="goto" value="${go}"
                   onclick="gotopage('${targetfield}')"/>
        </td>
    </tr>

</table>
