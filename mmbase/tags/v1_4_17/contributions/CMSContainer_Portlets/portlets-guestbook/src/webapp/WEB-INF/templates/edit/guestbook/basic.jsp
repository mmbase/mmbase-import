<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<mm:cloud>
    <mm:import externid="elementId" required="true" from="request" />

    <form name="<portlet:namespace />form" method="post"
        action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>"><mm:node
        number="${elementId}" notfound="skip">
        <h1><mm:field name="title" /></h1>
        <mm:field name="body">
            <mm:isnotempty>
                <p class="body"><mm:write /></p>
            </mm:isnotempty>
        </mm:field>

        <input type="text" name="content_${elementId}_title" value="<mm:field name="title" />" />
        <br />
        <input type="text" name="content_${elementId}_body" value="<mm:field name="body" />" />
        <br />

        <br />
        <c:set var="elementsPerPage">
            <mm:field name="pagesize">
                <mm:isnotempty>
                    <mm:write />
                </mm:isnotempty>
            </mm:field>
        </c:set>
        <c:if test="${elementsPerPage == -1}">
            <c:set var="elementsPerPage" value="50" />
        </c:if>
        <mm:relatednodescontainer type="guestmessage" role="posrel">
            <c:set var="totalElements">
                <mm:size id="totalitems" />
            </c:set>

            <cmsc:pager maxPageItems="${elementsPerPage}" items="${totalElements}" index="center"
                maxIndexPages="${10}" export="offset,currentPage=pageNumber">
                <table>
                    <mm:relatednodes offset="${offset}" max="${offset + elementsPerPage}">
                        <mm:field name="number" id="messageNumber" write="false" />

                        <cmsc:actionURL var="actionUrl">
                            <cmsc:param name="action" value="delete" />
                            <cmsc:param name="deleteNumber" value="${messageNumber}" />
                        </cmsc:actionURL>

                        <tr>
                            <td colspan="2"><mm:field name="title" /></td>
                        </tr>
                        <tr>
                            <td><fmt:message key="view.title" />:</td>
                            <td><input type="text" name="content_${messageNumber}_title" value="<mm:field name="title" />" /></td>
                        </tr>
                        <tr>
                            <td><fmt:message key="view.name" />:</td>
                            <td><input type="text" name="content_${messageNumber}_name" value="<mm:field name="name" />" /></td>
                        </tr>
                        <tr>
                            <td><fmt:message key="view.body" />:</td>
                            <td><textarea name="content_${messageNumber}_body"><mm:field name="body" /></textarea></td>
                        </tr>
                        <tr>
                            <td colspan="2"><a href="${actionUrl}"><fmt:message key="edit.delete" /></a></td>
                        </tr>
                        <tr>
                            <td colspan="2">&nbsp;</td>
                        </tr>
                    </mm:relatednodes>
                </table>
                <%@include file="/WEB-INF/templates/pagerindex.jsp"%>
            </cmsc:pager>
        </mm:relatednodescontainer>
    </mm:node> <input type="submit" name="save" value="<fmt:message key="edit.submit" />:" /><br />

    </form>
</mm:cloud>
