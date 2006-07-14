<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>
<%@ page import="java.util.*" %>

<%@include file="include/inc_windowwidth.jsp" %>

<%
String pageId = request.getParameter("page"); 
if(pageId==null){ pageId="";}
%>

<%-- following piece of code depends on page, language and windowWidth --%>
<% String cacheKey = "homepage_" + pageId + "_" + windowWidth; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" ><!-- <%= new java.util.Date() %> -->

<mm:cloud>

<% boolean imageFound = false; %>
<% String imageId = ""; %>
<mm:list nodes="<%= pageId %>"
	path="pagina,posrel,images"
	constraints="images.titel_zichtbaar='1'" fields="images.number">
	<mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
		<% imageId = images_number; %>
	</mm:field>
	<% imageFound = true; %>
</mm:list>
<% if(!imageFound) { %>
	<%	Hashtable catalogusImages = new Hashtable();
		int numberOfImages = 0; 
	%>
	<mm:list nodes="catalogus"
		path="pagina,posrel,images"
		fields="images.number,posrel.pos"
		orderby="posrel.pos" directions="UP">
		<mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
			<%	catalogusImages.put(new Integer(numberOfImages),images_number); 
				numberOfImages ++;
			%>
		</mm:field>
	</mm:list>
	<%	int selectedImage = (int) Math.floor(numberOfImages*Math.random());
		imageId = (String) catalogusImages.get(new Integer(selectedImage));
	%>
<% } %>
<mm:node number="<%= imageId %>">
	<%  int imageSize= (4*windowWidth)/6; %>
	<mm:field name="screensize" jspvar="images_layout" vartype="String" write="false">
		<% if(images_layout.equals("Staand")) { imageSize = (5*windowWidth)/16; } %>
	</mm:field>
	<% String imageTemplate = "s(" + imageSize + ")"; %>
		<img src=<mm:image template="<%= imageTemplate %>" /> alt="" border="0">
</mm:node>

</mm:cloud>

</cache:cache>
