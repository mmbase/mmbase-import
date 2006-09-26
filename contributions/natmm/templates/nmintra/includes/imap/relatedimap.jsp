<mm:list nodes="<%= paginaID %>" path="pagina,posrel,images" max="1"
  ><mm:node element="images" jspvar="image"><%
    if(isPreview) { %><a href=""><% } 
      boolean resize = "1".equals(image.getStringValue("screensize"));
      %><img src="<mm:image template="<%= (resize ? "s(550)" : "" ) %>" />" alt="" border="0" usemap="#imagemap"<% 
      if(isPreview) { %>ismap<% } %>><% 
    if(isPreview) { %></a><% } 
  %></mm:node
></mm:list>
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