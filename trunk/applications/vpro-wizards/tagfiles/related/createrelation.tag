<%--
    creates a relation within an existing form.
    that can be a form:container or a related:add or a related:view tag body
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>

<%@ attribute name="referSource" %>
<%@ attribute name="source" %>
<%@ attribute name="referDestination" %>
<%@ attribute name="destination" %>
<%@ attribute name="role" %>
<%@ attribute name="sortfield" description="this is the field in the relation that a sorting value will be inserted in. (like posrel.pos, although for posrel this field is set by default)"  %>
<%@ attribute name="sortposition" description="[begin|end] where to insert the new relation in the sorted list of existing relations (between given source and destination). defaults to 'end'"  %>

<c:if test="${empty relationid}"><c:set var="relationid" scope="request" value="200"/></c:if>
<c:set var="relationid" scope="request" value="${relationid+1}"/>

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
    <input type="hidden" name="actions[createRelation][${relationid}].destinationNodeNr" value="${destination}" />
</c:if>
<c:if test="${not empty role}">
    <input type="hidden" name="actions[createRelation][${relationid}].role" value="${role}" />
</c:if>
