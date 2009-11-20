<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
    this plugin tries to find the flushname value in:
    1 the local scope
    2 in the request
    3 as param
    4 from the session.

    when it found the flushname it sets in in the session.

    -local scope is used when you set it directly on the (wizard) tags
    -param is used when you call a wizard with flushname as parameter
    -request is used by child tags of wizard
    -session is used to pass it to related wizards. this way you don't have to add (ugly) parameters everywhere

--%>
<%@ tag body-content="empty"  %>
<%@ attribute name="value" description="should contain the current value of the 'cachename' var on the page" %>
<%@ variable name-given="flushname" scope="AT_END"%>
<%@ variable name-given="from" scope="AT_END" description="for debugging purposes" %>

<c:set var="from" value="attribute" />

<c:if test="${empty pageScope.flushname}">
    <c:set var="flushname" value="${requestScope.flushname}" scope="page"/>
    <c:set var="from" value="request"  />
</c:if>

<c:if test="${empty pageScope.flushname}">
    <c:set var="flushname" value="${param.flushname}" scope="page"/>
    <c:set var="from" value="param" />
</c:if>

<c:if test="${empty pageScope.flushname}">
    <c:set var="flushname" value="${sessionScope.flushname}" scope="page"/>
    <c:set var="from" value="session" />
</c:if>

<c:if test="${not empty pageScope.flushname}">
    <c:set var="flushname" value="${flushname}" scope="request" />
    <c:set var="flushname" value="${flushname}" scope="session" />
</c:if>
