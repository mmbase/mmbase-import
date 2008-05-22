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
	%><%@include file="relatedareas.jsp" %><%
	targetObject = "pagina2";
	%><%@include file="relatedareas.jsp" 
%></map><%
if(isPreview) {
	targetObject = "artikel";
	%><%@include file="relatedcoordinates.jsp" %><%
	targetObject = "pagina2";
	%><%@include file="relatedcoordinates.jsp" %><%
} %>