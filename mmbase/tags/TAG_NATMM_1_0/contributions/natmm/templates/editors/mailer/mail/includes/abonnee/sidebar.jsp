<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase">
<%-- get default text --%>
<%@include file="../defaulttext.jsp"%>

<mm:import externid="active"/>
<%-- grmbl, no spaces and returns allowed in ul because of an IE-bug --%>
<div id="sidebar">
<h3>Menu</h3>
<ul><li><a href="dossier.jsp" <mm:compare referid="active" value="profiel">class="active"</mm:compare>>Profiel</a></li></ul>
</div>
</mm:cloud>
</mm:content>

