<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" %>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" orderby="contentrel.pos" max="1"
	><%@include file="includes/relatedarticle.jsp" 
%></mm:list><%
imageId = ""; 
int numberOfImages = 0; 
boolean thisIsFirst = true; 
%><mm:list nodes="<%= paginaID %>"
	path="pagina,posrel,images"
	orderby="posrel.pos" directions="UP"
	><mm:field name="images.number" jspvar="images_number" vartype="String" write="false"
	><% if(thisIsFirst) {
			imageId = images_number; thisIsFirst=false;
	   } else {
	   		imageId += "," + images_number;
	   }
	   numberOfImages++;
	 %></mm:field
></mm:list><%

String previousImage = "-1";
String nextImage = "-1";
String thisImage = "";
String otherImages = "";

%><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr><%
	int imageCounter = 0;
	int numberInRow = 5;
	while(imageCounter<numberOfImages) { 
		%><%@include file="includes/splitimagelist.jsp" 
		%><mm:node number="<%= thisImage %>"
			><td>
				<a href="#" onClick="javascript:launchCenter('<%= requestURL %>slideshow.jsp?r=<%= rubriekId %>&p=<%= paginaID %>&i=<%= imageId %>', 'center', 550, 740,'resizable=1'); setTimeout('newwin.focus();',250); return false;">
					<img src="<mm:image template="s(110)" />" width="110" height="71" border="0" title="Klik op de foto om te vergroten" class="thumb">
				</a></td>
			</mm:node
		><% imageId = nextImage; 
		imageCounter++;
		if((imageCounter%numberInRow)==0) {
			%></tr><tr> <% }
	}
	while((imageCounter%numberInRow)!=0) { 
		%><td><img src="media/spacer.gif" width="110" height="1" border="0"></td><%
		imageCounter++;
	} %></tr>
</table>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>