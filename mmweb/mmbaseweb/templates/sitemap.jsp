<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="false"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/header.jsp"
%>
<td class="white" colspan="2" valign="top">
<mm:node referid="page"><h2><mm:field name="title" /></h2></mm:node>

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
				<li><a href="/index.jsp?portal=<mm:write referid="p" />&page=<mm:field name="pages2.number" />"><mm:field name="pages2.title" /></a></li>
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
</div>
</td>
<%@ include file="/includes/footer.jsp"
%></mm:cloud>
