<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_initdate.jsp" %>
<%@include file="include/inc_windowwidth.jsp" %>

<% String thisImage = request.getParameter("image") ;
   if(thisImage==null) { thisImage=""; }
   String pageId = request.getParameter("page") ; 
   if(page!=null) {
   
%>

<%-- following piece of code depends on pageId, thisImage, language and windowWidth --%>
<% String cacheKey = "catalogus_browser_" + pageId + "_" + thisImage + "_" +  language + "_" + windowWidth; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" ><!-- <%= new java.util.Date() %> -->
	
<mm:cloud>

<% String thisImageLayout =""; %>
<% String previousImage =""; %>
<% String nextImage =""; %>
<% String lastImage =""; %>
<% String firstImage =""; %>
<% boolean thisIsFirst = false; %>

<mm:list nodes="<%= pageId %>"
	path="pagina,posrel,images"
	fields="images.number,images.screensize,posrel.pos"
	orderby="posrel.pos" directions="UP">
	<mm:field name="images.screensize" jspvar="images_layout" vartype="String" write="false">
	<mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
	<%	if(thisImage.equals("")) { thisImage = images_number; thisImageLayout = images_layout; }
      if(lastImage.equals("")&&thisImage.equals(images_number)) { thisIsFirst = true; }
		if(firstImage.equals("")) { firstImage = images_number; } // save for wrap-around
		if(thisImage.equals(lastImage)) { nextImage = images_number; } // thisImage found in previous loop; set nextImage
		if(thisImage.equals(images_number)) { previousImage = lastImage; thisImageLayout = images_layout; } // thisImage found in present loop; set previousImage
		lastImage = images_number;
	%>
	</mm:field>
	</mm:field>
</mm:list>
<%	if(thisIsFirst) { previousImage = lastImage; } // wrap around if thisImage is first in list
	if(nextImage.equals("")) { nextImage = firstImage; } // wrap around if thisImage is last in list
%>

<% String pageHref = "page.jsp?page=" +  pageId + "&image=";
	String lang = "";
	if (language.equals("english")){
		lang = "_eng";
	} else if (language.equals("french")) {
		lang = "_fra";
	}%>

<%@include file="include/inc_image.jsp" %>

</mm:cloud>

</cache:cache>

<% } %>