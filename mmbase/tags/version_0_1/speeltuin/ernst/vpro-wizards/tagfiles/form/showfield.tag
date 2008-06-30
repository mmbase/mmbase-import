<%@ tag body-content="empty"  %>
<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>

<%@ attribute name="guivalue" type="java.lang.Boolean" description="when true: try to find a resource bundle for this field, and if this succeeds, show the value that is keyd to the value of this field. if not found, show the guivalue for this field" %>
<%@ include file="fieldinit.tagf" %>
<form:noedit/>
<c:set var="_value"><spring:escapeBody htmlEscape="true">${fieldvalue}</spring:escapeBody></c:set>
<div class="inputBlock">
    <div class="fieldName">${fieldname}</div>
    <c:choose>
        <c:when test="${not empty guivalue}">
            <mm:cloud method="asis">
                <mm:node number="${currentnode}" notfound="skipbody">
                    <mm:field name="${field}">
                        <c:set var="fieldvalue" ><mm:write /></c:set>
                        <c:set var="guitype"><mm:fieldinfo type="guitype"/></c:set>


                        <%--a crappy way of testing if this is an resourcebundle.--%>
                        <c:if test="${fn:indexOf(guitype, '.') > -1}">
                            <c:set var="message" >
                                <fmt:bundle basename="${guitype}">
                                    <fmt:message key="${fieldvalue}" />
                                </fmt:bundle>
                            </c:set>
                        </c:if>

                        <%--show either the resource bundle message or the result of the gui function --%>
                        <c:choose>
                            <c:when test="${not empty message}">
                                <div class="fieldValue" id="field_${nodetype}_${field}_dummy">${message}</div>
                            </c:when>
                            <c:otherwise>
                                <div class="fieldValue" id="field_${nodetype}_${field}_dummy"> <mm:fieldinfo type="guivalue"/> </div>
                            </c:otherwise>
                        </c:choose>
                    </mm:field>
                </mm:node>
            </mm:cloud>
        </c:when>
        <c:otherwise>
            <div class="fieldValue" id="field_${nodetype}_${field}_dummy">${fieldvalue}</div>
        </c:otherwise>
    </c:choose>
</div>
