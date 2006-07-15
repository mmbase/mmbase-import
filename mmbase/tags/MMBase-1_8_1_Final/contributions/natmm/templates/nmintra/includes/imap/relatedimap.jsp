<mm:list nodes="<%= paginaID %>" path="pagina,posrel,images" max="1"><%
	if(isPreview) { %><a href=""><% } 
		%><img src="<mm:node element="images"><mm:image /></mm:node>" alt="" border="0" usemap="#imagemap"<% 
		if(isPreview) { %>ismap<% } %>><% 
	if(isPreview) { %></a><% } 
%></mm:list>
<map name="imagemap"><%
	String targetObject = "artikel";
	String readmoreUrl = sUrl + "?p" + paginaID + "&article=";
	%><%@include file="relatedareas.jsp" %><%
	readmoreUrl = sUrl + "?p=";
	targetObject = "pagina2";
	%><%@include file="relatedareas.jsp" 
%></map><%
if(isPreview) {
	targetObject = "artikel";
	readmoreUrl = "../ " + sUrl + "?p=" + paginaID + "&article=";
	%><%@include file="relatedcoordinates.jsp" %><%
	targetObject = "pagina2";
	readmoreUrl = "../" + sUrl + "?p=";
	%><%@include file="relatedcoordinates.jsp" %><%
} %>