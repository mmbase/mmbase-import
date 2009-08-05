<%@ tag import="java.util.*, nl.vpro.redactie.util.*" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<%@ attribute name="valuefield" required="true"  %>
<%@ attribute name="labelfield" required="true"  %>
<%@ attribute name="path" required="true"  %>
<%@ attribute name="nodes"  %>
<%@ attribute name="constraints"  %>
<%@ attribute name="searchdirs"   %>
<%@ attribute name="orderby"   %>
<%@ attribute name="directions"   %>
<%@ attribute name="distinct"   %>

<jsp:useBean id="_options" scope="request"class="nl.vpro.redactie.util.OptionlistBean"/>
<mm:cloud method="asis">
    <c:choose>
    <%--list with more steps then one.--%>
        <c:when test="${fn:indexOf(path, ',') > -1}">
            <mm:list path="${path}" fields="${valuefield},${labelfield}" nodes="${nodes}"
                constraints="${constraints}" searchdir="${searchdirs}" orderby="${orderby}" directions="${directions}" distinct="${distinct}">

                <c:set var="value" ><mm:field name="${valuefield}" /></c:set>
                <c:set var="label" ><mm:field name="${labelfield}" /></c:set>
                <jsp:setProperty name="_options" property="value" value="${value}"/>
                <jsp:setProperty name="_options" property="label" value="${label}"/>
            </mm:list>
        </c:when>

        <%-- list with one step--%>
        <c:otherwise>
            <mm:listnodes type="${path}" constraints="${constraints}" orderby="${orderby}" directions="${directions}"
                nodes="${nodes}">
                <c:set var="value" ><mm:field name="${valuefield}" /></c:set>
                <c:set var="label" ><mm:field name="${labelfield}" /></c:set>
                <jsp:setProperty name="_options" property="value" value="${value}"/>
                <jsp:setProperty name="_options" property="label" value="${label}"/>
            </mm:listnodes>
        </c:otherwise>
    </c:choose>

</mm:cloud>
