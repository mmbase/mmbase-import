<%@ tag import="java.util.*, org.mmbase.applications.vprowizards.spring.util.*"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="options" description="a comma separated list of value/lable pairs example: 1=een,2=twee"  %>
<%@ attribute name="delimiter" description="wheat character to use to split the options string" %>
<%--
    use this optionlist with some nested option tags, to create an optionlist manually.
--%>
<jsp:useBean id="_options" scope="request"class="org.mmbase.applications.vprowizards.spring.util.OptionlistBean"/>

<c:choose>
    <c:when test="${empty options}"> <jsp:doBody var="_dummy"/> </c:when>
    <c:otherwise>
        <c:set var="delim" value="," />
        <c:if test="${not empty delimiter}"><c:set var="delim" value="${delimiter}" /></c:if>
        <c:forEach var="option" items="${fn:split(options, ',')}">
            <c:set var="value" value="${fn:split(option, '=')}"/>
            <jsp:setProperty name="_options" property="value" value="${value[0]}"/>
            <jsp:setProperty name="_options" property="label" value="${value[1]}"/>
        </c:forEach>
    </c:otherwise>
</c:choose>
