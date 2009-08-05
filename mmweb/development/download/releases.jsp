<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="relatedcontent">
<mm:list path="pages2,releases,mmevents"
  fields="releases.number,mmevents.start" 
  orderby="mmevents.start" directions="DOWN">
  <mm:first>
  <h4>All releases</h4>
  <ul>
  </mm:first>
  <mm:import id="releasedate" reset="true"><mm:field name="mmevents.start"/></mm:import>
  <mm:node element="releases">
    <li><strong><a href="#r<mm:field name="number" />"><mm:field name="name" /> <mm:field name="version"/></a></strong><br />
    <mm:locale language="en"><mm:time time="$releasedate" format="MMMM dd, yyyy"/></mm:locale></li>
  </mm:node>
  <mm:last></ul></mm:last>
</mm:list>
</div><!-- /div relatedcontent -->
<div id="textcontent">
<%-- List all releases --%>
<h2><mm:node number="$page"><mm:field name="title" /></mm:node></h2>
<mm:node number="$page">
	<mm:related path="releases,mmevents" fields="releases.number,mmevents.start" orderby="mmevents.start" directions="DOWN">
		<mm:import id="releasedate" reset="true"><mm:field name="mmevents.start"/></mm:import>
		<mm:node element="releases">
			<h3><a id="r<mm:field name="number" />"></a><mm:field name="name"/> <mm:field name="version"/></h3>
			<mm:locale language="en"><p><mm:time time="$releasedate" format="MMMM dd, yyyy"/></p></mm:locale>
			<mm:field name="intro"><mm:isnotempty><p class="intro"><mm:write/></p></mm:isnotempty></mm:field>
			<mm:field name="body" escape="p"><mm:isnotempty><p><mm:write/></p></mm:isnotempty></mm:field>
			<ul>
			<mm:related path="posrel,urls" orderby="posrel.pos,urls.description" directions="UP">
				<mm:node element="urls">
					<li><a href="<mm:field name="url" />"><mm:field name="description"/></a></li>
				</mm:node>
			</mm:related>
			</ul>
		</mm:node>
	</mm:related >
</mm:node>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
