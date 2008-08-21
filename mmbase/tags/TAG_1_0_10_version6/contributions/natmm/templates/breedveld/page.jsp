<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<%@include file="include/inc_windowwidth.jsp" %>

<%
String pageId = request.getParameter("page"); 
if(pageId==null){ pageId="";}
String iartId = request.getParameter("iart"); 
if(iartId==null){ iartId="";}
String imageId = request.getParameter("image"); 
if(imageId==null){ imageId="";}
%>

<%-- import the response of the contact from in the session --%>
<%
String visitorsReactie = request.getParameter("reactie");
if(visitorsReactie!=null){ session.setAttribute("reactie", visitorsReactie); }
String visitorsName = request.getParameter("naam");
if(visitorsName!=null){ session.setAttribute("naam", visitorsName);}
String visitorsEmail = request.getParameter("email");
if(visitorsEmail!=null){ session.setAttribute("email", visitorsEmail);}
String visitorsPrivacy = request.getParameter("privacy");
if(visitorsPrivacy!=null){ session.setAttribute("privacy", visitorsPrivacy); }
%>

<mm:cloud>

<%@include file="include/pagecounter.jsp" %>

<html>
<head>
<title>Marian Breedveld - <mm:node number="<%= pageId %>"><mm:field name="titel" /></mm:node></title>
<%@include file="include/inc_cache.jsp" %>
<link rel="stylesheet" type="text/css" href="css/marianbreedveld.css">

<% String openComment = "<!--"; %>
<% String closeComment = "//-->"; %>
<SCRIPT LANGUAGE="JavaScript">
<%= openComment %>
<%@include file="script/scr_popup.js" %>
<%@include file="script/scr_mouseover.js" %>
<%= closeComment %>
</SCRIPT>
<META HTTP-EQUIV="imagetoolbar" CONTENT="no">
</head>

<%-- onLoad statement to prevent Netscape from caching the page --%>
<body class="background" topmargin="0" rightmargin="0" leftmargin="0" onLoad="if ('Navigator' == navigator.appName) document.forms[0].reset();">

<% if(pageId.equals("")) { %>
<p><b><font color="#CC0000">Error:</font></b><br>Er is geen pagina gespecificeerd.</p>
<% } else { %>

<%-- Iterator om te checken of er wel een template is --%>
<% boolean templateExists = false; %>

<%-- Maak een list om er achter te komen welke template aan deze pagina gerelateerd is --%>
<mm:list nodes="<%= pageId %>" path="pagina,gebruikt,paginatemplate" fields="paginatemplate.url">
   	<mm:field name="paginatemplate.url" jspvar="template_url" vartype="String" write="false">
	<a name="top"></a>
	<table align="center" cellpadding="0" cellspacing="0">
	<tr>
    	<td class="header" align="center">
			<jsp:include page="<%= template_url %>"> 
		        <jsp:param name="page" value="<%= pageId %>" />
		        <jsp:param name="iart" value="<%= iartId %>" />
				<jsp:param name="image" value="<%= imageId %>" />
			</jsp:include> 
		</td>
	</tr>
<%--
	<tr>
    	<td class="background" ><img src="media/spacer.gif" width="<%= windowWidth %>" height="10"></td>
	</tr>
	<tr>
    	<td class="line" ><img src="media/spacer.gif" width="<%= windowWidth %>" height="1"></td>
	</tr>
	<tr>
    	<td class="background" ><img src="media/spacer.gif" width="<%= windowWidth %>" height="10"></td>
	</tr>
--%>
	</table>		
	</mm:field>
  <% templateExists = true; %>
</mm:list>

<% if (!templateExists) { %>
<p><b><font color="#CC0000">Error:</font></b><br>Er moet nog een template gekoppeld worden aan deze pagina (<%= pageId %>).</p>
<% } %>

<% } %>

</body>

<%-- If you're really serious about the Pragma working, place another set of HEAD flags at the bottom of the document, before the end HTML flag and re-enter the Pragma. --%>
<head>
<%@include file="include/inc_cache.jsp" %>
</head>

</html>
</mm:cloud>
