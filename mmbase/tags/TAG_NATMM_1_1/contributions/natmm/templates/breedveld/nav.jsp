<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_windowwidth.jsp" %>
<%@include file="include/inc_language.jsp" %>

<%
String pageId = request.getParameter("page"); 
if(pageId==null){ pageId="";}
String iartId = request.getParameter("iart"); 
if(iartId==null){ iartId="";}
%>
<%-- following piece of code depends on pageId, iartId, language and windowWidth --%>
<% String cacheKey = "navigatie_" + pageId + "_" + iartId + "_" +language + "_" + windowWidth; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" ><!-- <%= new java.util.Date() %> -->

<mm:cloud>

<html>
<head>
<title>Marian Breedveld - <mm:node number="<%= pageId %>"><mm:field name="titel" /></mm:node></title>
<link rel="stylesheet" type="text/css" href="css/marianbreedveld.css">

</head>

<body class="background" topmargin="0" rightmargin="0" leftmargin="0">
<table align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td class="background"><img src="media/spacer.gif" width="<%= windowWidth %>" height="10"></td>
	</tr>
	<tr>
        <td class="background" width="<%= windowWidth %>" align="center">
		<a href="index.jsp" target="_top"><img title="www.marianbreedveld.com" src="media/MarianBreedveld.small.gif" border="0"></a>
		</td>
	</tr>
	<tr>
    	<td class="background" ><img src="media/spacer.gif" width="<%= windowWidth %>" height="1"></td>
	</tr>
	<tr>
    	<td class="background" align="center">
		<%-- inner table with navigation --%>
		<table align="center" cellpadding="0" cellspacing="0">
		<tr>
		<% String pageNumber = "-1"; %>
		<% int pageCount = 0; %>
		<% int selectedPageCount = -1; %>
		<% int [] pageTitleLength = new int[100]; %>
		<% String [] pageTitle = new String[10]; %>
		<% String pageHref = ""; %>
		<mm:list nodes="marianbreedveld"
			path="rubriek1,parent,rubriek2,posrel,pagina"
			orderby="posrel.pos" directions="UP"
        	fields="rubriek2.naam,pagina.number">
				<mm:field name="pagina.number" jspvar="dummy04" vartype="String" write="false">
					<% pageNumber = dummy04; %>	
				</mm:field>
				<mm:field name="pagina.titel" jspvar="dummy05" vartype="String" write="false">
					<% pageTitle[pageCount] = lan(language, dummy05); %>	
				</mm:field>
				<% pageHref = "index.jsp?page=" + pageNumber; %>
				<% if(!iartId.equals("")) pageHref += "&iart=" +  iartId; %>
				<%-- include iart in href to find last selected category in cv --%>
				<td class="background">&nbsp;&nbsp;&nbsp;<a target="_top"
					href="<%= pageHref %>" 
					<% if(pageNumber.equals(pageId)) { %>
						class="dark_boldlink"
						<% 	selectedPageCount = pageCount; %>
					<% } else { %>
						class="light_boldlink"
					<% } %>
				><%= pageTitle[pageCount] %></a>&nbsp;&nbsp;&nbsp;</td>
				<% pageTitleLength[pageCount] = 6 + pageTitle[pageCount].length(); %>
				<% pageCount++; %>
		</mm:list>
			<% pageHref = "index.jsp?page=" + pageId; %>
			<% if(!iartId.equals("")) pageHref += "&iart=" +  iartId; %>
			<td class="light">&nbsp;
			<A href="<%= pageHref %>&lan=dutch" target="_top"
				<% if(language.equals("dutch")) { %> class="dark_boldlink"	<% } else { %>	class="light_boldlink" <% } %>
				>N</A>/<A href="<%= pageHref %>&lan=english" target="_top"
				<% if(language.equals("english")) { %> class="dark_boldlink"	<% } else { %>	class="light_boldlink" <% } %>
				>E</A>/<A href="<%= pageHref %>&lan=french" target="_top"
				<% if(language.equals("french")) { %> class="dark_boldlink"	<% } else { %>	class="light_boldlink" <% } %>
				>F</A>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
    	<td class="line" ><img src="media/spacer.gif" width="<%= windowWidth %>" height="1"></td>
	</tr>
	<tr>
    	<td class="background" align="center">
		<%-- inner table which follows navigation --%>
		<table align="center" cellpadding="0" cellspacing="0">
		<tr>
		<% for(int i=0; i < pageCount; i++) { %>
			<td class="bold"><%
				for(int j=0; j < pageTitleLength[i]; j++) {
					if(j==(pageTitleLength[i]/2)) {
						if(selectedPageCount==i) {	
							%><IMG height=8 alt="" vspace=0 src="media/arrow_down_dg.gif" width=10 align=top border=0 name="nav_<%= pageTitle[i] %>"><%
						} else { 
							%><IMG height=8 alt="" vspace=0 src="media/spacer.gif" width=10 align=top border=0 name="nav_<%= pageTitle[i] %>"><%
						}
					} else {
						%>&nbsp;<%
					} 
				} %></td>
		<% } %>
		<td class="light">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		</tr>
		</table>
		</td>
	</tr>
</table>
</body>
</html>
</mm:cloud>

</cache:cache>