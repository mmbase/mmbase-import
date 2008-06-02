<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="fieldname" %>
<%@ attribute name="relatename" %>
<%@ attribute name="decouplename" %>
<%@ attribute name="destinationnode" %>
<%@ attribute name="relationrole" %>
<%@ attribute name="defaultrelated" type="java.lang.Boolean" %>

<c:if test="${empty relationid}"><c:set var="relationid" scope="request" value="2000"/></c:if>
<c:set var="relationid" scope="request" value="${relationid+1}"/>

<div class="inputBlock">
    <div class="fieldName">${fieldname}</div>
    <div class="fieldValue">
    <c:choose>
        <c:when test="${empty nodenr}">
            <c:set var="actiontype" scope="request">createCheckboxRelationActions</c:set>

            <c:remove var="checked" /><c:if test="${not defaultrelated}"><c:set var="checked" >checked="checked"</c:set></c:if>
            <input class="radio" type="radio" onclick="disableRelated();" name="createCheckboxRelationActions[${relationid}].relate" ${checked}  value="false"  id="field_${nodetype}_${field}_false"/>${decouplename}<br>

            <c:remove var="checked" /><c:if test="${defaultrelated}"><c:set var="checked" >checked="checked"</c:set></c:if>
            <input class="radio" type="radio" onclick="disableRelated();" name="createCheckboxRelationActions[${relationid}].relate"${checked} value="true"  id="field_${nodetype}_${field}_true"/>${relatename}<br>

            <input type="hidden" name="createCheckboxRelationActions[${relationid}].referSource" value="new" />
            <input type="hidden" name="createCheckboxRelationActions[${relationid}].destination" value="${destinationnode}" />
            <input type="hidden" name="createCheckboxRelationActions[${relationid}].role" value="${relationrole}" />
        </c:when>
        <c:otherwise>
            <mm:cloud method="asis">
            <c:set var="actiontype" scope="request">updateCheckboxRelationActions</c:set>
            <c:set var="destnode"><mm:node number="${destinationnode}"><mm:field name="number"/></mm:node></c:set>
            <c:set var="destnodetype"><mm:node number="${destinationnode}"><mm:nodeinfo type="type"/></mm:node></c:set>

            <mm:node number="${nodenr}">
                <c:set var="relationExists">false</c:set>
                <c:set var="relationExists"><mm:relatednodes type="${destnodetype}" constraints="number=${destnode}" max="1">true</mm:relatednodes></c:set>
                <input class="radio" type="radio" onclick="disableRelated();" name="updateCheckboxRelationActions[${relationid}].relate" value="false"
                    <c:if test="${not relationExists}">checked="checked"</c:if>
                /> ${decouplename}<br>
                <input class="radio" type="radio" onclick="disableRelated();" name="updateCheckboxRelationActions[${relationid}].relate" value="true"
                    <c:if test="${relationExists}">checked="checked"</c:if>
                /> ${relatename}<br>
                <input type="hidden" name="updateCheckboxRelationActions[${relationid}].source" value="${nodenr}" />
                <input type="hidden" name="updateCheckboxRelationActions[${relationid}].destination" value="${destinationnode}" />
                <input type="hidden" name="updateCheckboxRelationActions[${relationid}].role" value="${relationrole}" />
            </mm:node>
            </mm:cloud>
        </c:otherwise>
    </c:choose>
    <jsp:doBody/>
    </div>
</div>