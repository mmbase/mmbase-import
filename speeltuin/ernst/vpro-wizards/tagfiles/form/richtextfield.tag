<%@ tag body-content="empty"  %>
<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>

<%@ attribute name="size" %>
<c:if test="${empty size}"><c:set var="size" value="small"/></c:if>
<c:if test="${size != 'small' && size != 'medium' && size != 'large'}">
    <util:throw message="form: richtextfield tag: field: ${fieldname}. The size attribute must be [small|medium|large]"/>
</c:if>

<%@ include file="fieldinit.tagf" %>

<%
    String fieldValue = (String) jspContext.getAttribute("fieldvalue");
    if(fieldValue == null){
        fieldValue = "";
    }
    boolean isHTML = org.mmbase.applications.vprowizards.spring.util.HTMLFilterUtils.isHTML(fieldValue);
    jspContext.setAttribute("textIsHtml",new Boolean(isHTML), PageContext.PAGE_SCOPE);
%>
<div class="inputBlock">
    <div class="fieldName">${fieldname}</div>
    <div class="fieldValue">
        <mm:import id="temp">
            <c:choose>
                <c:when test="${textIsHtml}">${fieldvalue}</c:when>
                <c:otherwise>
                    <mm:import id="fieldvalue">${fieldvalue}</mm:import>
                    <mm:write referid="fieldvalue" escape="p"/>
                </c:otherwise>
            </c:choose>
        </mm:import>

        <input type="hidden" name="actions[${modifier}${action}][${actionnr}].htmlField" value="${field}" />
        <c:set var="_action" value="actions[${modifier}${action}][${actionnr}].fields[${field}]" />
        <textarea onchange="disableRelated();" id="field_${nodetype}_${field}" name="${_action}"><mm:write referid="temp" escape="trimmer"/></textarea>
    </div>
</div>

<script type="text/javascript">
    var oFCKeditor = new FCKeditor( 'field_${nodetype}_${field}' ) ;
    oFCKeditor.BasePath = '${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/fckeditor/';
    oFCKeditor.Config['ToolbarStartExpanded'] = true ;
    <%-- TODO deze check eruit halen --%>
    <c:choose>
        <c:when test="${empty param.extraoptions}">
//            oFCKeditor.ToolbarSet	= 'VPROWizards' ;
            oFCKeditor.ToolbarSet	= 'VPROWizards' ;
        </c:when>
        <c:otherwise>
            oFCKeditor.ToolbarSet	= '${param.extraoptions}' ;
        </c:otherwise>
    </c:choose>

    oFCKeditor.Width='400';
    <c:if test="${size eq 'small'}">
        oFCKeditor.Height='200';
    </c:if>
    <c:if test="${size eq 'medium'}">
        oFCKeditor.Height='400';
    </c:if>
    <c:if test="${size eq 'large'}">
        oFCKeditor.Height='600';
    </c:if>
    oFCKeditor.ReplaceTextarea();

    <%-- Deze code controleert of het textarea is aangepast.
         Indien het textarea is aangepast dan wordt de functie
         disableRelated() aangeroepen.
    --%>
    function FCKeditor_OnComplete( editorInstance ){
        editorInstance.Events.AttachEvent( 'OnSelectionChange', dirtyCheck ) ;
        function dirtyCheck() {
            if(editorInstance.IsDirty()) {
                disableRelated();
            }
        }
    }
</script>