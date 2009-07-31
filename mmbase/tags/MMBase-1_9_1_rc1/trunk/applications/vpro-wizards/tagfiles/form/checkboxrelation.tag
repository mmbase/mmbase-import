<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
    a checkbox relation tag creates a specific relation when the checkbox is checked
    and removes the relation when the checkbox is unchecked.

    This tag can be used both while creating a node and editing a node. If there
    is no node yet, the relation is created to the new node, so there must be a new node!

    a limitation for now is that it can only work from the node that the form is editing, so
    you can not insert your own base node number yet. I don't know if that is too restrictive..

    It can contian any field, but you will probably want to use the hiddenfield
    for this one
--%>

<%@ attribute name="name" description="the name that is displayed" %>
<%@ attribute name="destinationnode" description="the number of a node that already exists" %>
<%@ attribute name="role" %>

<%--store values temporarily--%>
    <c:set var="_action" value="${action}"/>
    <c:set var="_modifier" value="${modifier}"/>
    <c:set var="_nodetype" value="${nodetype}"/>

    <%--set values for createrelation context--%>
    <c:set var="action" value="CheckboxRelation" scope="request"/>
    <c:set var="nodetype" value="${role}" scope="request"/>


    <%--figure out an id--%>
    <c:if test="${empty relationid}"><c:set var="relationid" scope="request" value="2000"/></c:if>
    <c:set var="relationid" scope="request" value="${relationid+1}"/>

    <c:set var="action" value="CheckboxRelation" scope="request"/>

    <c:choose>
        <%--when there is no root node, action is create anyway--%>
        <c:when test="${empty nodenr}"><c:set var="modifier" value="create"/></c:when>

        <%-- if there is a nodenr, check if we find the relation--%>
        <c:otherwise>
            <%--now check if the relation is there alreadyy--%>
            <mm:cloud method="asis">
                <c:set var="destnodetype"><mm:node number="${destinationnode}"><mm:nodeinfo type="type"/></mm:node></c:set>
                <mm:node number="${nodenr}">
                    <mm:relatednodes type="${destinationtype}" role="${role}" constraints="number=${destinationnode}">
                        <c:set var="checked">checked="checked"</c:set>
                        <%--
                            when the relation exists the relationid becomes the relation node number
                            and the modifier becomes 'update'
                        --%>
                        <c:set var="relationid" scope="request"><mm:field name="number" /></c:set>
                        <c:set var="modifier" value="update" scope="request"/>
                    </mm:relatednodes>
                </mm:node>
            </mm:cloud>
        </c:otherwise>
    </c:choose>
    <c:set var="_action" value="actions[${modifier}${action}][${relationid}]"/>

<%--do the form--%>
<div class="inputBlock">
    <div class="fieldName">&nbsp;</div>
    <div class="fieldValue">

        <%--the relation source is different for create and update--%>
        <c:choose>
            <c:when test="${action == 'create'}">
                <input type="hidden" name="${_action}.referSource" value="new" />
            </c:when>
            <c:otherwise>
                <input type="hidden" name="${_action}.source" value="${nodenr}" />
            </c:otherwise>
        </c:choose>

        <input class="checkbox" type="checkbox" onclick="disableRelated();" name="${_action}.relate" ${checked} />${name}<br>
        <input type="hidden" name="${_action}.destination" value="${destinationnode}" />
        <input type="hidden" name="${_action}.role" value="${relationrole}" />

        <%-- now process the body--%>
        <jsp:doBody/>

        <%--put values back--%>
        <c:set var="action" value="${_action}" scope="request"/>
        <c:set var="modifier" value="${_modifier}" scope="request"/>
        <c:set var="nodetype" value="${_nodetype}" scope="request"/>
    </div>
</div>