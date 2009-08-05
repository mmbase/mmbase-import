<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="false"
%><mm:cloud><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp"
%>
<div id="pagecontent">
<mm:node referid="page"><h2><mm:field name="title" /></h2></mm:node>

<table border="0" width="100%" cellspacing="0" cellpadding="4">
<tr valign="top">
<td>
<mm:node number="page_homepage">
  <h4>MMBase</h4>
  <%-- nivo 1 --%>
  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
  	orderby="posrel.pos">
	<mm:first><ul><li><a href="/">Homepage</a></li></mm:first>
	<li>
	  <a href="<mm:url page="index.jsp">
		<mm:param name="portal">home</mm:param>
		<mm:param name="page"><mm:field name="number" /></mm:param>
	  </mm:url>"><mm:field name="title" /></a>
	  <%-- nivo 2 --%>
	  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
	  	orderby="posrel.pos">
		<mm:first><ul></mm:first>
		<li><a href="<mm:url page="index.jsp">
			<mm:param name="portal">home</mm:param>
			<mm:param name="page"><mm:field name="number" /></mm:param>
		</mm:url>"><mm:field name="title" /></a></li>
		<mm:last></ul></mm:last>
	  </mm:relatednodes>
	  <%-- /nivo 2 --%>
	</li>
	<mm:last></ul></mm:last>
  </mm:relatednodes>
  <%-- /nivo 1 --%>
</mm:node>
</td><td>
<mm:node number="portal_foundation">
  <h4>Foundation</h4>
  <%-- nivo 1 --%>
  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
  	orderby="posrel.pos">
	<mm:first><ul>
	<li>
	<a href="<mm:url page="index.jsp">
	  <mm:param name="portal">foundation</mm:param>
	  <mm:param name="page"><mm:field name="number" /></mm:param>
	</mm:url>"><mm:field name="title" /></a>
	</li></mm:first>
	  <%-- nivo 2 --%>
	  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
		orderby="posrel.pos" constraints="posrel.pos > 5">
		<li>
		  <a href="<mm:url page="index.jsp">
			<mm:param name="portal">foundation</mm:param>
			<mm:param name="page"><mm:field name="number" /></mm:param>
		  </mm:url>"><mm:field name="title" /></a>
		  <%-- nivo 3 --%>
		  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
			orderby="posrel.pos">
			<mm:first><ul></mm:first>
			<li><a href="<mm:url page="index.jsp">
				<mm:param name="portal">foundation</mm:param>
				<mm:param name="page"><mm:field name="number" /></mm:param>
			</mm:url>"><mm:field name="title" /></a></li>
			<mm:last></ul></mm:last>
		  </mm:relatednodes>
		  <%-- /nivo 3 --%>
		</li>
	  </mm:relatednodes>
	  <%-- /nivo 2 --%>
	<mm:last></ul></mm:last>
  </mm:relatednodes>
  <%-- /nivo 1 --%>
</mm:node>
</td><td>
<mm:node number="portal_developers">
  <h4>Developers</h4>
  <%-- nivo 1 --%>
  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
  	orderby="posrel.pos">
	<mm:first><ul>
	<li>
	  <a href="<mm:url page="index.jsp">
		<mm:param name="portal">portal_developers</mm:param>
		<mm:param name="page"><mm:field name="number" /></mm:param>
	  </mm:url>"><mm:field name="title" /></a>
	</li></mm:first>
	<%-- nivo 2 --%>
	<mm:relatednodes type="pages" searchdir="destination" role="posrel"
	  orderby="posrel.pos" constraints="posrel.pos > 5">
	  <li>
		<a href="<mm:url page="index.jsp">
		  <mm:param name="portal">portal_developers</mm:param>
		  <mm:param name="page"><mm:field name="number" /></mm:param>
		</mm:url>"><mm:field name="title" /></a>
		<%-- nivo 3 --%>
		<mm:relatednodes type="pages" searchdir="destination" role="posrel"
		  orderby="posrel.pos">
		  <mm:first><ul></mm:first>
		  <li><a href="<mm:url page="index.jsp">
			  <mm:param name="portal">portal_developers</mm:param>
			  <mm:param name="page"><mm:field name="number" /></mm:param>
		  </mm:url>"><mm:field name="title" /></a></li>
		  <mm:last></ul></mm:last>
		</mm:relatednodes>
		<%-- /nivo 3 --%>
	  </li>
	</mm:relatednodes>
	<%-- /nivo 2 --%>
	<mm:last></ul></mm:last>
  </mm:relatednodes>
  <%-- /nivo 1 --%>
</mm:node>
</td>
</tr>
</table>
</div>
<%@ include file="/includes/alterfooter.jsp"%>
</mm:cloud>
