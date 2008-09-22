<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ variable name-given="guitype" scope="NESTED" %>
<%@ include file="fieldinit.tagf" %>
<%--
    this tag shows an optionlist for the given field. the options are derived from a nested
    optionlist tag.
--%>

<%--find the optionlist (make shure noting is written to the ouput)--%>
<c:remove var="_options"/>
<jsp:doBody var="dummy"/>

<c:if test="${empty _options}">
    <util:throw message="form:radiofield tag: no options found. use some optionlist provider tag."/>
</c:if>

<div class="inputBlock">
    <div class="fieldName">${fieldname}</div>
    <div class="fieldValue">
        <c:forEach items="${_options.list}" var="option">
            <c:choose>
                <c:when test="${fieldvalue == option.value}"><c:set var="checked" >checked="checked"</c:set></c:when>
                <c:otherwise> <c:remove var="checked" /> </c:otherwise>
            </c:choose>
            <input type="radio" class="radio"
                onchange="disableRelated();"
                name="action[${modifier}${action}][${actionnr}].fields[${field}]"
                value="${option.value}"
                ${checked}
                id="field_${nodetype}_${field}_${option.value}"/>${option.label}<br>
        </c:forEach>
    </div>
</div>
