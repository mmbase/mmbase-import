<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=utf-8" %>
<mm:cloud>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>

<td>
<table>
<%-- List all releases --%>
<tr><td><h2>Releases</h2></td></tr>
<mm:node number="$page">
	<mm:related path="releases,mmevents" fields="releases.number,mmevents.start" orderby="mmevents.start" directions="DOWN">
<tr><td>
			<mm:import id="releasedate" reset="true"><mm:field name="mmevents.start"/></mm:import>
			<mm:node element="releases">
				<h3><mm:field name="name"/> <mm:field name="version"/></h3>
				<mm:locale language="en"><mm:time time="$releasedate" format="MMMM dd, yyyy"/></mm:locale>
				<mm:field name="html(intro)"><mm:isnotempty><p><mm:write/></p></mm:isnotempty></mm:field>
				<mm:field name="html(body)"><mm:isnotempty><p><mm:write/></p></mm:isnotempty></mm:field>
				<ul>
				<mm:related path="posrel,urls" orderby="posrel.pos,urls.description" directions="DOWN">
					<li>
					<mm:node element="urls">
						<a href="<mm:field name="url"/>"><mm:field name="description"/></a>
					</mm:node>
					</li>
					
				</mm:related>
				</ul>
			</mm:node>
</td></tr>
	</mm:related >
</mm:node>
</table>
</td>
<%@include file="/includes/footer.jsp" %>
</mm:cloud>
