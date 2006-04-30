<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="false"
%><mm:cloud><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp"
%>
<mm:import id="template">/development/projects/project.jsp</mm:import>
<div id="relatedcontent">
</div>
<div id="textcontent">
  
  <mm:listnodescontainer type="project">

    <mm:composite operator="OR">
      <mm:constraint field="status" value="application" />
      <mm:constraint field="status" value="contribution" />
    </mm:composite>
    <mm:sortorder field="title" />
    <mm:listnodes id="project">
      <h2><a href="<mm:url referids="portal,page,project,template" />"><mm:field name="title" /></a></h2>
      <p>
	(<mm:field name="status" />) 
	<mm:field name="intro" />
      </p>
    </mm:listnodes>
  </mm:listnodescontainer>

</div>
<%@ include file="/includes/alterfooter.jsp"%>
</mm:cloud>
