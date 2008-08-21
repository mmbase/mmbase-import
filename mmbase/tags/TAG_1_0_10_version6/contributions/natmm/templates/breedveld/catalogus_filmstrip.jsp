<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_initdate.jsp" %>
<%@include file="include/inc_windowwidth.jsp" %>

<% String pageId = request.getParameter("page") ; 
   if(page!=null) {
%>

<%-- following piece of code depends on pageId, language and windowWidth --%>
<% String cacheKey = "catalogus_" + pageId + "_" +  language + "_" + windowWidth; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" ><!-- <%= new java.util.Date() %> -->

	
<mm:cloud>

<% String imageId = ""; %>
<% int numberOfImages = 0; %>
<% boolean thisIsFirst = true; %>
<mm:list nodes="<%= pageId %>"
	path="pagina,posrel,images"
	fields="images.number,posrel.pos"
	orderby="posrel.pos" directions="UP">
	<mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
	<% if(thisIsFirst) {
			imageId = images_number; thisIsFirst=false;
	   }
	   else {
	   		imageId += "," + images_number;
	   }
	   numberOfImages++;
	 %>
	</mm:field>
</mm:list>

<%
   String previousImage = "-1";
   String nextImage = "-1";
   String thisImage = "";
   String otherImages = "";
%>
<table border="0" cellspacing="0" cellpadding="0" align="center">
<tr>
<td class="background"><img src="/media/spacer.gif" border="0" width="1" height="<%= (windowWidth/15) %>"></td>
</tr>
<tr>
<% int imageCounter = 0; %>
<% while(imageCounter<numberOfImages) { %>
	<%@include file="include/inc_splitimagelist.jsp" %>
	<td class="background"><img src="/media/spacer.gif" width="12" height="1" border="0"></td>
	<td class="background"><img src="/media/spacer.gif" width="1" height="1" border="0"></td>
	<td class="background">
	<img src="/media/spacer.gif" width="40" height="1" border="0"><br>
	<a href="javascript:popup('image_gallery.jsp?imageId=<%= imageId %>','popup<%= thisImage %>','no')">
	<mm:node number="<%= thisImage %>">
		<img src=<mm:image template="s(40)" /> border="0" alt="<%= lan(language,"Klik voor afbeelding op orginele grootte") %>"></a><br>
	</mm:node>
	<img src="/media/spacer.gif" width="40" height="1" border="0"></td>
	<td class="background"><img src="/media/spacer.gif" width="1" height="1" border="0"></td>
	<td class="background"><img src="/media/spacer.gif" width="12" height="1" border="0"></td>
	<% imageId = nextImage; %>
	<% imageCounter++; %>
<% } %>
</tr>
</table>

</mm:cloud>

</cache:cache>


<% } %>