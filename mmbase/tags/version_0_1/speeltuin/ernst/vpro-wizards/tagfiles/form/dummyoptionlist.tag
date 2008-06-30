<%@ tag import="java.util.*, nl.vpro.redactie.util.*" body-content="empty" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--test the optionlist idear--%>
<jsp:useBean id="_options" scope="request"class="nl.vpro.redactie.util.OptionlistBean"/>
<jsp:setProperty name="_options" property="value" value="1"/>
<jsp:setProperty name="_options" property="label" value="tonen"/>
<jsp:setProperty name="_options" property="value" value="0"/>
<jsp:setProperty name="_options" property="label" value="niet tonen"/>

