<%@ tag body-content="empty"  %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>

<%@ include file="fieldinit.tagf" %>
<%--set this flag in the request so the form container can set enctype on the form element --%>
<mm:write value="true" write="false" request="filefield"/>
<mm:cloud method="asis">
    <div class="inputBlock">
        <div class="fieldName">${fieldname}</div>
        <div class="fieldValue">
            <%--
                if there is a node, then show it. if it is an image, provide a prefiew with a link to the full image.
                otherwise create an attachment link
            --%>
            <c:if test="${modifier == 'update'}">
                <c:choose>
                    <c:when test="${nodetype == 'images'}">
                        <util:image urlvar="fullurl" nodenr="${currentnode}"/>
                        <util:image urlvar="previewurl" nodenr="${currentnode}" template="+s(40)+part(0x0x40x40)+s(!40x!40)"/>
                        <a target="image" href="${fullurl}">
                            <img src="${previewurl}"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <mm:node number="${currentnode}"><mm:field name="gui()" escape="none"/></mm:node>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <input type="file" name="actions[${modifier}${action}][${actionnr}].file" value="" onchange="disableRelated();" class="file" id="field_${nodetype}_${field}">
        </div>
    </div>
</mm:cloud>
