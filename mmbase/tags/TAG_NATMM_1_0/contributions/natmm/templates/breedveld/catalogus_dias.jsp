<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<mm:import externid="username" from="parameters" />
<mm:cloud method="http" logon="$username" jspvar="cloud">

<% String pageId = request.getParameter("page");
   if (pageId == null ) pageId = "";
   if (!pageId.equals("")){ 
%>
<% String openComment = "<!--"; %>
<% String closeComment = "//-->"; %>
<SCRIPT LANGUAGE="JavaScript">
<%= openComment %>
<%@include file="script/scr_popup.js" %>
<%@include file="script/scr_mouseover.js" %>
<%= closeComment %>
</SCRIPT>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_windowwidth.jsp" %>

<%-- following piece of code depends on page and windowWidth --%>
<% String cacheKey = "catalogus_" + pageId + "_" + windowWidth; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application"><!-- <%= new java.util.Date() %> -->

<%	int numberInRow = (windowWidth/115); %> 

<%@include file="include/inc_pieceloader.jsp" %>

<%-- table of images --%>	
<% imageCounter = 0; %>
<% String imageId = ""; %>
<% String pieceDescription = ""; %>

<table border="1" cellspacing="5" cellpadding="5">

<%-- while loop over liggende images --%>
<tr>
<%	imageCounter = 0;
   imageId = listLiggend[imageCounter];
   while(!listLiggend[imageCounter].equals("-1")) { %>
	<td>
	<a href="javascript:popup('piece_gallery.jsp?imageId=<%= imageId %>','popup<%= imageId%>','no')">
	<%@include file="include/inc_piecedescr.jsp" %>
	<mm:node number="<%= imageId %>">
		<img src=<mm:image template="s(100)" /> width="100" border="0" title="<%= pieceDescription %>"></a></td>
	</mm:node>
	<% imageCounter++;
	   imageId = listLiggend[imageCounter];
	   if(imageCounter%numberInRow==0) { %> </tr><tr> <% } 
   } %>
<%-- fill row --%>
<% while(!(imageCounter%numberInRow==0)) { %>
	 <td><img src="media/spacer.gif" width="100" height="1"></td> 
	<% imageCounter++; %>
<% } %>
</tr>

<%-- while loop over staande images --%>
<tr>
<%	imageCounter = 0;
   imageId = listStaand[imageCounter];
	while(!imageId.equals("-1")) { %>
	<td align="center" valign="middle">
	<a href="javascript:popup('piece_gallery.jsp?imageId=<%= imageId %>','popup<%= imageId %>','no')">
	<%@include file="include/inc_piecedescr.jsp" %>
	<mm:node number="<%= imageId %>">
		<img src=<mm:image template="s(100)" /> width="100" border="0" title="<%= pieceDescription %>"></a></td>
	</mm:node>
	<% imageCounter++; %>
	<%  imageId = listStaand[imageCounter]; %>
	<% if(imageCounter%numberInRow==0) { %> </tr><tr> <% } %>
<% } %>
<%-- fill row --%>
<% while(!(imageCounter%numberInRow==0)) { %>
	 <td align="center" valign="middle"><img src="media/spacer.gif" width="100" height="1"></td> 
	<% imageCounter++; %>
<% } %>
</tr>

</table>

</cache:cache>

<% } %>

</mm:cloud>
