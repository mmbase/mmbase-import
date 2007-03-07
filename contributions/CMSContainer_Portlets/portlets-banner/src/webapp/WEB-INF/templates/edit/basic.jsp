<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<mm:cloud>
    <mm:import externid="elementId" required="true" from="request" />

    <form name="<portlet:namespace />form" method="post"
        action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
        <mm:node
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
    </mm:node> <input type="submit" name="save" value="<fmt:message key="edit.submit" />:" /><br />

    </form>
</mm:cloud>
