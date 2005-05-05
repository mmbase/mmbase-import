<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="false"
%><mm:cloud><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp"
%>
<mm:import externid="project" required="true" />
<mm:node referid="project">
<div id="relatedcontent"><br />
  <%@ include file="/includes/persons.jsp" %>
  <mm:import id="backtemplate">/development/projects/project.jsp</mm:import>
  <%@ include file="/includes/attachment.jsp" %>
  <%@ include file="/includes/urls.jsp" %>
  <%@ include file="/includes/documentation.jsp" %>
</div>
<div id="textcontent">
  <h1><mm:field name="title" /></h1>
  <mm:field name="body" escape="p" />
</mm:node>

</div>
<%@ include file="/includes/alterfooter.jsp"%>
</mm:cloud>
