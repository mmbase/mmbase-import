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
    <mm:sortorder field="status" direction="up" />
    <mm:sortorder field="number" direction="down" />
    <mm:composite operator="AND">
      <mm:constraint field="status" value="application" inverse="true" />
      <mm:constraint field="status" value="contribution" inverse="true" />
    </mm:composite>
 
    <p><mm:listnodes id="projectlist">      
      <mm:changed>
	  [<a href="#<mm:field name="number" />"><mm:field name="status" /></a>]&nbsp;
      </mm:changed>
    </mm:listnodes></p>
  </mm:listnodescontainer>
  
  <mm:listnodes id="project" referid="projectlist">
    <mm:changed>
      <h1><a name="<mm:field name="number" />"><mm:field name="status"/></a></h1>
    </mm:changed>
    <h2><a href="<mm:url referids="portal,page,project,template" />"><mm:field name="title" /></a></h2>
    <p><mm:field name="intro" /></p>
  </mm:listnodes>

</div>
<%@ include file="/includes/alterfooter.jsp"%>
</mm:cloud>
