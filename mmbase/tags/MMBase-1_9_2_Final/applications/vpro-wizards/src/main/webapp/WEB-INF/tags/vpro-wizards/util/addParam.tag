<%--
    params that are added to a page will be added to all links generated bij the wizards
--%>
<%@ tag import="java.util.*" body-content="empty"  %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>
<%@ attribute name="name" required="true"  %>
<%@ attribute name="value"   %>
<%@ attribute name="parameter"   description="when this is set, the value is taken from the request parameters"%>
<c:choose>
    <c:when test="${not empty value}">
        <c:set var="v" value="${value}" />
    </c:when>
    <c:otherwise>
        <c:if test="${empty parameter}">
            <util:throw message="tag:addParameter. Geen waarde gevonden voor parameter ${name}. Je moet of het value attribuut of het parameter attribuut invullen"/>
        </c:if>
        <c:if test="${empty param[parameter] }">
            <util:throw message="tag:addParameter. De opgegeven parameter ${name} bestaat niet in de request als ${parameter}"/>
        </c:if>
        <c:set var="v" value="${param[parameter]}" />
    </c:otherwise>
</c:choose>
<%

{
    Map params = (Map)jspContext.findAttribute("___params");
    if(params == null){
        params = new HashMap();
        jspContext.setAttribute("___params", params, PageContext.REQUEST_SCOPE);
    }

    String name = (String) jspContext.getAttribute("name");
    String v = (String) jspContext.getAttribute("v");
    if(v != null){
        params.put(name,v);
    }
}
%>
