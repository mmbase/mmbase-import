<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="true"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/header.jsp"
%>
<td class="white" colspan="2" valign="top">
<mm:node referid="page"><h2><mm:field name="title" /></h2></mm:node>

<table border="0" width="100%" cellspacing="0" cellpadding="4">
<tr valign="top">
<td>
<mm:node number="page_homepage">
  <h4>MMBase</h4>
  <!-- nivo 1 -->
  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
  	orderby="posrel.pos">
	<mm:first><ul><li><a href="/">Homepage</a></li></mm:first>
	<li><a href="<mm:url page="index.jsp">
		<mm:param name="portal">home</mm:param>
		<mm:param name="page"><mm:field name="number" /></mm:param>
	</mm:url>"><mm:field name="title" /></a></li>
	  <!-- nivo 2 -->
	  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
	  	orderby="posrel.pos">
		<mm:first><ul></mm:first>
		<li><a href="<mm:url page="index.jsp">
			<mm:param name="portal">home</mm:param>
			<mm:param name="page"><mm:field name="number" /></mm:param>
		</mm:url>"><mm:field name="title" /></a></li>
		<mm:last></ul></mm:last>
	  </mm:relatednodes>
	  <!-- /nivo 2 -->
	<mm:last></ul></mm:last>
  </mm:relatednodes>
  <!-- /nivo 1 -->
</mm:node>
</td><td>
<mm:node number="portal_foundation">
  <h4>Foundation</h4>
  <!-- nivo 1 -->
  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
  	orderby="posrel.pos">
	<mm:first><ul></mm:first>
	<li><a href="<mm:url page="index.jsp">
		<mm:param name="portal">foundation</mm:param>
		<mm:param name="page"><mm:field name="number" /></mm:param>
	</mm:url>"><mm:field name="title" /></a></li>
	<!-- nivo 2 -->
	<mm:relatednodes type="pages" searchdir="destination" role="posrel"
	  orderby="posrel.pos">
	  <li><a href="<mm:url page="index.jsp">
		  <mm:param name="portal">foundation</mm:param>
		  <mm:param name="page"><mm:field name="number" /></mm:param>
	  </mm:url>"><mm:field name="title" /></a></li>
	  <!-- nivo 3 -->
	  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
		orderby="posrel.pos">
		<mm:first><ul></mm:first>
		<li><a href="<mm:url page="index.jsp">
			<mm:param name="portal">foundation</mm:param>
			<mm:param name="page"><mm:field name="number" /></mm:param>
		</mm:url>"><mm:field name="title" /></a></li>
		<mm:last></ul></mm:last>
	  </mm:relatednodes>
	  <!-- /nivo 3 -->
	</mm:relatednodes>
	<!-- /nivo 2 -->
	<mm:last></ul></mm:last>
  </mm:relatednodes>
  <!-- /nivo 1 -->
</mm:node>
</td><td>
<mm:node number="portal_developers">
  <h4>Developers</h4>
  <!-- nivo 1 -->
  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
  	orderby="posrel.pos">
	<mm:first><ul></mm:first>
	<li><a href="<mm:url page="index.jsp">
		<mm:param name="portal">portal_developers</mm:param>
		<mm:param name="page"><mm:field name="number" /></mm:param>
	</mm:url>"><mm:field name="title" /></a></li>
	<!-- nivo 2 -->
	<mm:relatednodes type="pages" searchdir="destination" role="posrel"
	  orderby="posrel.pos">
	  <li><a href="<mm:url page="index.jsp">
		  <mm:param name="portal">portal_developers</mm:param>
		  <mm:param name="page"><mm:field name="number" /></mm:param>
	  </mm:url>"><mm:field name="title" /></a></li>
	  <!-- nivo 3 -->
	  <mm:relatednodes type="pages" searchdir="destination" role="posrel"
		orderby="posrel.pos">
		<mm:first><ul></mm:first>
		<li><a href="<mm:url page="index.jsp">
			<mm:param name="portal">portal_developers</mm:param>
			<mm:param name="page"><mm:field name="number" /></mm:param>
		</mm:url>"><mm:field name="title" /></a></li>
		<mm:last></ul></mm:last>
	  </mm:relatednodes>
	  <!-- /nivo 3 -->
	</mm:relatednodes>
	<!-- /nivo 2 -->
	<mm:last></ul></mm:last>
  </mm:relatednodes>
  <!-- /nivo 1 -->
</mm:node>
</td>
</tr>
</table>


<%-- 
<mm:node number="home">
<ul>
	<li><b><a href="/index.jsp?portal=<mm:field name="number" />"><mm:field name="name" /></a></b></li>
	<mm:related path="posrel,pages"
		fields="pages.number,pages.title,posrel.pos"
		searchdir="destination">
		<mm:first><ul></mm:first>
		<li><a href="/index.jsp?portal=home&page=<mm:field name="pages.number" id="pg" />"><mm:field name="pages.title" /></a></li>
		<mm:last></ul></mm:last>
		<mm:list nodes="$pg"
			path="pages1,posrel,pages2"
			fields="pages2.number,pages2.title,posrel.pos"
			orderby="posrel.pos" searchdir="destination">
			<mm:first><ul></mm:first>
			<li><a href="/index.jsp?portal=home&page=<mm:field name="pages2.number" />"><mm:field name="pages2.title" /></a></li>
			<mm:last></ul></mm:last>
		</mm:list>
		<mm:remove referid="pg" />
	</mm:related>
	<mm:list path="portals1,posrel,portals2"
		fields="posrel.pos,portals2.number,portals2.name"
		orderby="posrel.pos" searchdir="destination">
		<mm:first><ul></mm:first>
		<li><b><a href="/index.jsp?portal=<mm:field name="portals2.number" id="p" />"><mm:field name="portals2.name" /></a></b></li>
		<mm:list nodes="$p"
			path="portals,posrel,pages"
			fields="pages.number,pages.title,posrel.pos"
			searchdir="destination">
			<mm:first><ul></mm:first>
			<li><a href="/index.jsp?portal=<mm:write referid="p" />&page=<mm:field name="pages.number" id="pg" />"><mm:field name="pages.title" /></a></li>
			<mm:list nodes="$pg"
				path="pages1,posrel,pages2"
				fields="pages2.number,pages2.title,posrel.pos"
				orderby="posrel.pos" searchdir="destination">
				<mm:first><ul></mm:first>
				<li><a href="/index.jsp?portal=<mm:write referid="p" />&page=<mm:field name="pages2.number" id="ppg" />"><mm:field name="pages2.title" /></a></li>
				<!-- page page -->

					<mm:list nodes="$ppg"
						path="pages1,posrel,pages2"
						fields="pages2.number,pages2.title,posrel.pos"
						orderby="posrel.pos" searchdir="destination">
						<mm:first><ul></mm:first>
						<li><a href="/index.jsp?portal=<mm:write referid="p" />&page=<mm:field name="pages2.number" />"><mm:field name="pages2.title" /></a></li>
						<mm:last></ul></mm:last>
					</mm:list>

					<mm:remove referid="ppg" />

				<!-- /page page -->
				<mm:last></ul></mm:last>
			</mm:list>
			<mm:last></ul></mm:last>
			<mm:remove referid="pg" />
		</mm:list>
		<mm:remove referid="p" />
		<mm:last></ul></mm:last>
	</mm:list>
</ul>
</mm:node>
--%>
</td>
<%@ include file="/includes/footer.jsp"
%></mm:cloud>
