<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_initdate.jsp" %>
<%@include file="include/inc_screensize.jsp" %>

<% int windowWidth = screenSize.width; %>
<!-- window width in image_gallery is <%= windowWidth %> -->

<% String imageId = request.getParameter("imageId") ; 
   if(imageId!=null) {
%>

<%-- following items of code depends on imageId, language and windowWidth --%>
<% String cacheKey = "imageGallery_" + imageId + "_" + language + "_" + windowWidth; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" ><!-- <%= new java.util.Date() %> -->

<%
   String previousImage = "-1";
   String nextImage = "-1";
   String thisImage = "";
   String otherImages = "";
%>

<%@include file="include/inc_splitimagelist.jsp" %>

<% String lang = "";
	if (language.equals("english")){
		lang = "_eng";
	} else if (language.equals("french")) {
		lang = "_fra";
	}%>

<mm:cloud>

<mm:list nodes="<%= thisImage %>" path="images" fields="images.title,images.screensize">
<mm:node element="images">
<% String thisImageLayout = ""; %>
<mm:field name="screensize" jspvar="dummy" vartype="String" write="false">
	<% thisImageLayout = dummy; %>
</mm:field>

<HTML>
<HEAD>
  <TITLE>
	<% boolean hasTitle = false; %>
	<mm:related path="posrel,items"
		fields="items.titel,items.number">
		<mm:field name="items.titel" jspvar="items_title" vartype="String" write="false">
			<% if(items_title.indexOf("Zonder titel")>-1) { %>
				<%= lan(language,"Zonder titel") %> <%= items_title.substring(12) %>
			<% } else { %>
				<%= items_title %>
			<% } %>
		</mm:field>
		<mm:node element="items">
			<mm:related path="stock,organisatie"	
					fields="organisatie.naam" constraints="organisatie.titel_zichtbaar='1'">
					- <%= lan(language,"Collectie") %> <mm:field name="organisatie.naam"/>
			</mm:related>
		</mm:node>
		<% hasTitle = true; %>
	</mm:related>
	<mm:related path="posrel,projects,posrel,projecttypes">
			<% String project_type = ""; %>
			<mm:field name="projecttypes.name" jspvar="dummy05" vartype="String" write="false">
					<% project_type = dummy05; %>
			</mm:field>
			<mm:node element="projects">
			<%-- look for translation of title --%>
			<%@include file="include/project_titel.jsp" %>
			<% if(hasTitle) { %> - <% } %>
			<%= lan(language,project_type) %> - <%= project_title %>
			<% hasTitle = true; %>
			</mm:node>
	</mm:related>
	<% if(!hasTitle) { %>
		<mm:related path="posrel,organisatie,contentrel,pagina"	
			fields="organisatie.naam" constraints="pagina.titel='Links'">
			<mm:field name="organisatie.naam"/>
		</mm:related>
	<% } %>
  </TITLE>
  <link rel="stylesheet" type="text/css" href="css/marianbreedveld.css">

  <% String openComment = "<!--"; %>
  <% String closeComment = "//-->"; %>
  <SCRIPT LANGUAGE="JavaScript">
	<%= openComment %>
	<%@include file="script/scr_mouseover.js" %>
	<%= closeComment %>
  </SCRIPT>
  
  <META HTTP-EQUIV="imagetoolbar" CONTENT="no">

</HEAD>
<BODY class="background" topmargin="0" rightmargin="0" leftmargin="0">
	<% String pageHref = "image_gallery.jsp?imageId="; %>
	<div align="center">
	<img src="media/spacer.gif" width="<%= windowWidth %>" height="<%= (windowWidth/10) %>" border="0"><br>
	<%@include file="include/inc_image.jsp" %>
	</div>
</BODY>
</HTML>
</mm:node>
</mm:list>
</mm:cloud>

</cache:cache>

<% } %>