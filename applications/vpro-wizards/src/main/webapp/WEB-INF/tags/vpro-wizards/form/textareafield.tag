<%@ tag body-content="empty"  %>
<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>

<%@ attribute name="size" %>
<c:if test="${empty size}"><c:set var="size" value="small"/></c:if>
<c:if test="${size != 'small' && size != 'medium' && size != 'large'}">
    <util:throw message="form:textareafield tag: field: ${fieldname}. The size attribute must be [small|medium|large]"/>
</c:if>

<%@ include file="fieldinit.tagf" %>
<div class="inputBlock">
    <div class="fieldName">${fieldname}</div>
    <div class="fieldValue">
        <c:set var="_action" value="actions[${modifier}${action}][${actionnr}].fields[${field}]" />
        <textarea onkeydown="disableRelated();" class="${size}" id="field_${nodetype}_${field}" name="${_action}">${fieldvalue}</textarea>
    </div>
</div>