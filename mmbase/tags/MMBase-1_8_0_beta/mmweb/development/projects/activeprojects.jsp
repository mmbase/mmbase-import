<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="false"
%><mm:cloud><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp"
%>
<mm:import id="template">/development/projects/project.jsp</mm:import>
<div id="relatedcontent">
</div>
<div id="textcontent">
  <h1>Active Projects</h1>
  <mm:listnodescontainer type="project">
     <mm:constraint field="status" value="finished" inverse="true" />
     <mm:sortorder field="status" direction="up" />
     <mm:sortorder field="number" direction="down" />
     <mm:listnodes id="project">
       <h2><a href="<mm:url referids="portal,page,project,template" />"><mm:field name="title" /></a> (<mm:field name="status"/>)</h2>
       <mm:field name="intro" escape="p" />
     </mm:listnodes>
   </mm:listnodescontainer>
</div>
<%@ include file="/includes/alterfooter.jsp"%>
</mm:cloud>
