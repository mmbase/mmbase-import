<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=utf-8"%>
<mm:cloud>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp"%>
<td class="white" colspan="2" valign="top">

<mm:node referid="page">
<mm:related path="posrel,articles" orderby="posrel.pos" directions="UP" searchdir="destination">
<mm:node element="articles">
<%@include file="/includes/article.jsp"%>
</mm:node>
</mm:related>
</mm:node>
</td>

<%@include file="/includes/footer.jsp"%>
</mm:cloud>
