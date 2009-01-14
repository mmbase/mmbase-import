<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
    This tag is used to create a new relation. It only works if there is no node yet. so when
    you are editing an existing node nothing happens. It should be used inside the form:container tag

    you can create a relation between existing nodes or between a new node and an existing node.

    This tag can contian any field tag. The 'action' field is set to 'Relation' and the
    'modifier' tag is set to 'create' and the nodetype field is set to $role.
    this may help fields to resolve these values. The values
    are restricted to the body of the tag

    the id (and actionnr) of the new relation action is put into the request as 'relationid'

    TODO: either referSource or source and referDestination or destination should be given. check on this!

--%>
<%@ attribute name="referSource" description="points to an id of a node in the current request"%>
<%@ attribute name="source" description="a node number or alias"%>
<%@ attribute name="referDestination" description="points to an id of a node in the current request"%>
<%@ attribute name="destination"  description="a node number or alias"%>
<%@ attribute name="role" description="the relation role" required="true" %>



<c:if test="${empty relationid}"><c:set var="relationid" scope="request" value="200"/></c:if>
<c:set var="relationid" scope="request" value="${relationid+1}"/>


<c:if test="${empty nodenr}">

    <%--store values temporarily--%>
    <c:set var="_action" value="${action}"/>
    <c:set var="_modifier" value="${modifier}"/>
    <c:set var="_nodetype" value="${nodetype}"/>

    <%--set values for createrelation context--%>
    <c:set var="action" value="Relation" scope="request"/>
    <c:set var="modifier" value="create" scope="request"/>
    <c:set var="nodetype" value="${role}" scope="request"/>



    <c:if test="${not empty referSource}">
        <input type="hidden" name="actions[createRelation][${relationid}].sourceNodeRef" value="${referSource}" />
    </c:if>
    <c:if test="${not empty source}">
        <input type="hidden" name="actions[createRelation][${relationid}].sourceNodeNumber" value="${source}" />
    </c:if>
    <c:if test="${not empty referDestination}">
        <input type="hidden" name="actions[createRelation][${relationid}].destinationNodeRef" value="${referDestination}" />
    </c:if>
    <c:if test="${not empty destination}">
        <input type="hidden" name="actions[createRelation][${relationid}].destinationNodeNumber" value="${destination}" />
    </c:if>
    <c:if test="${not empty role}">
        <input type="hidden" name="actions[createRelation][${relationid}].role" value="${role}" />
    </c:if>

    <%-- do the body--%>
    <jsp:doBody/>

    <%--put values back--%>
    <c:set var="action" value="${_action}" scope="request"/>
    <c:set var="modifier" value="${_modifier}" scope="request"/>
    <c:set var="nodetype" value="${_nodetype}" scope="request"/>
</c:if>