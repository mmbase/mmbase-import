<%@ tag body-content="empty"  %>
<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ include file="fieldinit.tagf" %>

<c:set var="_value"><spring:escapeBody htmlEscape="true">${fieldvalue}</spring:escapeBody></c:set>
<div class="inputBlock">
    <div class="fieldName">${fieldname}</div>
    <div class="fieldValue">
        <input onkeydown="disableRelated();" type="text" size="80" name="actions[${modifier}${action}][${actionnr}].fields[${field}]" value="${_value}" id="field_${nodetype}_${field}"/>
    </div>
</div>
