<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%--
<%@ variable name-given="guitype" scope="NESTED" %>
--%>
<%@ include file="fieldinit.tagf" %>

<%--
    this tag shows an optionlist for the given field. the options are derived from a nested
    optionlist tag.
--%>

<%--find the optionlist (make shure noting is written to the ouput)--%>
<c:remove var="_options"/>

<c:set var="guitype" value="${guitype}" scope="request" />
    <jsp:doBody var="dummy"/>
<c:remove var="guitype" scope="request" />

<c:if test="${empty _options}">
    <util:throw message="form:optionfield tag: no options found. use some optionlist provider tag."/>
</c:if>


<div class="inputBlock">
    <div class="fieldName">${fieldname}</div>
    <div class="fieldValue">
        <select onchange="disableRelated();" name="actions[${modifier}${action}][${actionnr}].fields[${field}]" id="field_${nodetype}_${field}">
            <c:if test="${modifier == 'create' && empty fieldvalue}">
                <option value="" >-- kies een optie --</option>
            </c:if>
            <c:forEach items="${_options.list}" var="option">
                <c:choose>
                    <c:when test="${option.value == fieldvalue}">
                        <option value="${option.value}" selected="selected">${option.label}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${option.value}">${option.label}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
    </div>
</div>
