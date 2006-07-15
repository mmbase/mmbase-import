<%@ page import="javax.servlet.http.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<%-- read window width from cookies set on loading and resizing of index.jsp --%>
<%@include file="include/inc_windowsize.jsp" %>

<mm:cloud>
<% 
String pageId = request.getParameter("page"); 
if(pageId==null){ pageId="";}
String iartId = request.getParameter("iart"); 
if(iartId==null){ iartId="";}
String imageId = request.getParameter("image"); 
if(imageId==null){ imageId="";}
String lanId = request.getParameter("lan"); 
if(lanId!=null){ session.setAttribute("language", lanId); }

String jumploc = request.getQueryString();
String jumpId = "";
if(jumploc==null){
	jumploc = "";
} else {
	jumploc = jumploc.substring(jumploc.lastIndexOf("=")+1);
	if(jumploc.indexOf("&")>-1) { // there is a & after the last =
		jumpId = jumploc.substring(jumploc.indexOf("&")+1);
	}
	jumploc = "#" + jumpId;
}
%>

<%-- if page not defined take homepage --%>
<% String homePageId = ""; %>
<mm:list nodes="marianbreedveld" path="rubriek,posrel,pagina" fields="pagina.number">
	<mm:field name="pagina.number" jspvar="dummy02" vartype="String" write="false">
		<% homePageId = dummy02; %>	
	</mm:field>
</mm:list>
<% if(pageId.equals("")) pageId = homePageId; %>

<%-- replace object alias of pageId by their number --%>
<% String oaliasConstraint = "oalias.name ='" + pageId + "'"; %>
<mm:list path="oalias" fields="oalias.name,oalias.destination" constraints="<%= oaliasConstraint %>">
    <mm:field name="oalias.destination" jspvar="dummy03" vartype="String" write="false">
		<% pageId = dummy03; %>	
	</mm:field>		
</mm:list>

<%-- log page and article title --%>
<% String page_title = ""; %>
<mm:node number="<%= pageId %>">
	<mm:field name="titel" jspvar="dummy" vartype="String" write="false">
			<% page_title = dummy; %>
	</mm:field>
</mm:node>

<html>
<head>
<title>Marian Breedveld - <%= page_title %></title>
<link rel="stylesheet" type="text/css" href="css/marianbreedveld.css">

<% String openComment = "<!--"; %>
<% String closeComment = "//-->"; %>
<SCRIPT LANGUAGE="JavaScript">
<%= openComment %>
<%@include file="script/scr_windowsize.js" %>
<%@include file="script/scr_cookies.js" %>
<%= closeComment %>
</SCRIPT>

<META name="description" content="Marian Breedveld is an artist who lives and works in Rotterdam, the Netherlands. The website of Marian Breedveld provides an overview of her past and present work.">

<META name="keywords" content="marian breedveld, doesburg, ateliers 63, parijs, rotterdam, painting, oil, canvas, bernard jordan, mk, rob de vries">

</head>

<%-- for IE ROWS=85 is sufficient --%>
<FRAMESET ROWS="100,*" FRAMEBORDER=0 BORDER=0 onLoad="javascript:setWindowSize()" onResize="javascript:setWindowSize()">
	<frame src="nav.jsp?page=<%= pageId %>&iart=<%= iartId %>" name="nav">
	<frame src="page.jsp?page=<%= pageId %>&iart=<%= iartId %>&image=<%= imageId %><%= jumploc %>" name="page">
</frameset>

</html>
</mm:cloud>

