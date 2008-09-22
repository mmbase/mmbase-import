<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--
    This tag is a wrapper for a try catch. you have to give the catch clouse as a
    fragment. Use the setExceptionAs and setMessgeAs to declare the variable names under which
    the exception class and the exception message wil be stored.

--%>
<%@ attribute name="setExceptionAs" required="true" type="java.lang.String" rtexprvalue="false"%>
<%@ attribute name="setMessageAs" required="true" type="java.lang.String" rtexprvalue="false"%>
<%@ attribute name="catchit" fragment="true" required="true" %>
<%@ attribute name="stacktrace" description="should a stacktrace be send to stdout? [true|false] defaults to false"  %>

<%@ variable name-from-attribute="setExceptionAs"  alias="exception" scope="NESTED"%>
<%@ variable name-from-attribute="setMessageAs"  variable-class="java.lang.String" alias="message" scope="NESTED"%>


<%
    try{
%>
    <jsp:doBody/>
<%
    }catch (Exception e){
%>
    <c:if test="${not empty setExceptionAs}">
        <c:set var="exception"><%=e.getClass().getName()%></c:set>
    </c:if>
    <c:if test="${not empty setMessageAs}">
        <c:set var="message"><%=e.getMessage()%></c:set>
    </c:if>
    <c:if test="${'true' == stacktrace}"> <%e.printStackTrace();%> </c:if>
    <jsp:invoke fragment="catchit"/>
<%
    }
%>
