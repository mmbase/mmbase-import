<mm:list nodes="<%= pageId %>" path="pagina,posrel,images" max="1"><%
	if(isPreview) { %><a href=""><% } 
		%><img src="<mm:node element="images"><mm:image /></mm:node>" alt="" border="0" usemap="#imagemap"<% 
		if(isPreview) { %>ismap<% } %>><% 
	if(isPreview) { %></a><% } 
%></mm:list>
<map name="imagemap"><%
	String targetObject = "artikel";
	String readmoreUrl = sUrl + "?p" + pageId + "&article=";
	%><%@include file="../includes/relatedareas.jsp" %><%
	readmoreUrl = sUrl "?p=";
	targetObject = "pagina2";
	%><%@include file="../includes/relatedareas.jsp" 
%></map><%
if(isPreview) {
	targetObject = "artikel";
	readmoreUrl = "../ " + sUrl + "?p=" + pageId + "&article=";
	%><%@include file="../includes/relatedcoordinates.jsp" %><%
	targetObject = "pagina2";
	readmoreUrl = "../" + sUrl + "?p=";
	%><%@include file="../includes/relatedcoordinates.jsp" %><%
} %>