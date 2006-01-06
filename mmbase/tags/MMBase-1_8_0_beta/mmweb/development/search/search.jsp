<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ page language="java" contentType="text/html; charset=iso8859-1" %>
<mm:cloud>
<%@ include file="/includes/getids.jsp" %>
<%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">
<mm:node number="page_search">
<h2>Search</h2>
<mm:include page="search_htdig.jsp" />
</mm:node>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
