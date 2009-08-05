<%@ tag import="java.util.*, nl.vpro.redactie.util.*"  body-content="empty" %>
<%@ attribute name="value" required="true"  %>
<%@ attribute name="label" required="true"  %>
<jsp:setProperty name="_options" property="value" value="${value}"/>
<jsp:setProperty name="_options" property="label" value="${label}"/>
