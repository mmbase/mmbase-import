<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_screensize.jsp" %>

<% String imageId = request.getParameter("imageId") ;
%>

<%-- following piece of code depends on imageId, language and screenSize.width --%>
<% String cacheKey = "piece_gallery_" + imageId + "_" + language + "_" + screenSize.width; %>
<%-- @include file="include/inc_cacheopen.jsp" --%>

<mm:cloud>

<mm:list nodes="<%= imageId %>" path="images,posrel,items"
	fields="images.title,items.titel,items.number,items.year,items.piecesize,items.material">


<%	int	imageSize = (3*screenSize.width)/5;
	int	tableWidth = (3*screenSize.width)/4;
	int	spaceHeight = (screenSize.width/12);
%>

<%-- get the listOfImages from the session context --%>
<%@include file="include/inc_pieceloader.jsp" %>

<HTML>
<HEAD>
  <TITLE><mm:field name="items.titel" /></TITLE>
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

<%-- determine previous and next image for image with id equals imageId, with wrap around --%>
<%	boolean imageFound = false;
	String previousImage = "-1";
	String nextImage = "-1";
	imageCounter = 0;
	while(!listStaand[imageCounter].equals("-1")) {
		imageCounter ++;
	}
	String lastImageStaand = listStaand[imageCounter-1];
	String lastImageLiggend = "-1";
	imageCounter = 0;
	while(!listLiggend[imageCounter].equals("-1")&&!imageFound ) { 
		lastImageLiggend = listLiggend[imageCounter];
		if(imageId.equals(lastImageLiggend)) {
			imageFound = true;
			if(imageCounter>0){
				previousImage = listLiggend[imageCounter -1];
			} else {
				previousImage = lastImageStaand;
			}
			nextImage = listLiggend[imageCounter +1];
			if(nextImage.equals("-1"))
			{	nextImage = listStaand[0];
			}
		}
 		imageCounter ++;
	}
	imageCounter = 0;
	while(!listStaand[imageCounter].equals("-1")&&!imageFound) {
		if(imageId.equals(listStaand[imageCounter])) {
			imageFound = true;
			if(imageCounter>0){
				previousImage = listStaand[imageCounter -1];
			} else {
				previousImage = lastImageLiggend;
			}
			nextImage = listStaand[imageCounter +1];
			if(nextImage.equals("-1"))
						{	nextImage = listLiggend[0];
			}
		}		
 		imageCounter ++;
	}	
%>

  <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0" ALIGN="center">
  <tr>
    <td class="background" colspan="3"><img src="media/spacer.gif" width="<%= tableWidth %>" height="<%= spaceHeight %>"></td>
  </tr>
  <TR>
    <TD align="center" colspan="3">
	<mm:node number="<%= imageId %>">
		<% String imageTemplate = "s(" + imageSize + ")"; %>
		<img src=<mm:image template="<%= imageTemplate %>" /> alt="" border="0"></TD>
	</mm:node>
  </TR>
  <tr>
    <td class="background" colspan="3"><img src="media/spacer.gif" width="<%= tableWidth %>" height="<%= (spaceHeight/2) %>"></td>
  </tr>
  <TR>
  	<td class="background"><img src="media/spacer.gif" width="10" height="10"></td>
  	<TD class="background" align="center">
			<span class="bold">
				<mm:field name="items.titel" jspvar="piece_title" vartype="String" write="false">
					<% if(piece_title.indexOf("Zonder titel")>-1) { %>
						<%= lan(language,"Zonder titel") %> <%= piece_title.substring(12) %><br>
					<% } else { %>
						<%= piece_title %><br>
					<% } %>
				</mm:field>
				<mm:field name="items.piecesize" />	
				<mm:field name="items.material" jspvar="piece_material" vartype="String" write="false">
					<% if(piece_material.indexOf("olieverf op linnen")>-1) { %>
							<%= lan(language,"olieverf op linnen") %><br>
					<% } else if(piece_material.indexOf("tempera op papier")>-1) { %>
							<%= lan(language,"tempera op papier") %><br>
					<% } else { %>
							<%= piece_material %><br>
					<% } %>
				</mm:field>
				<% boolean partOfCollection = false; %>
				<mm:node element="items">
					<mm:related path="stock,organisatie,posrel,organisatie_type"
						constraints="organisatie_type.naam='Collectie'"
						fields="organisatie.naam">
						(<%= lan(language,"Collectie") %> <mm:field name="organisatie.naam" />)
						<% partOfCollection = true; %>
					</mm:related>
					<mm:related path="stock,organisatie,posrel,organisatie_type"
						constraints="organisatie_type.naam='Privecollectie'"
						fields="organisatie.naam">
						(<%= lan(language,"Privecollectie") %> <mm:field name="organisatie.naam" />)
						<% partOfCollection = true; %>
					</mm:related>
				</mm:node>	
				<% if(!partOfCollection) { %> <br> <% } %>
			</span>
  	</TD>
  	<td class="background"><img src="media/spacer.gif" width="10" height="10"></td>
  </TR>
  <tr>
    <td class="background" colspan="3"><img src="media/spacer.gif" width="<%= tableWidth %>" height="10"></td>
  </tr>
  <TR>
    <TD align="left" class="background">
	<% if(!previousImage.equals("-1")) { %>
		<A onmouseover="changeImages('pijl_previous', 'media/arrow_left_dr.gif'); window.status=''; return true;"
		     onmouseout="changeImages('pijl_previous', 'media/arrow_left_lg.gif'); window.status=''; return true;"
		  	 class="light_boldlink" href="piece_gallery.jsp?imageId=<%= previousImage %>">
		  <IMG height=12 alt="" hspace=4 src="media/arrow_left_lg.gif" width=8 align=absMiddle border=0 name=pijl_previous><%= lan(language,"vorige") %>
		</A>
	<% } else { %>
		&nbsp;
	<% } %>
	</TD>
	<TD align="center" class="background">
		<A class="light_boldlink" HREF="javascript:self.close()" onClick="self.close(); return false;">
			<%= lan(language,"sluit dit venster") %>
		</A>
	</TD>
	<TD align="right" class="background">
	<% if(!nextImage.equals("-1")) { %>
		<A onmouseover="changeImages('pijl_next', 'media/arrow_right_dr.gif'); window.status=''; return true;"
		   onmouseout="changeImages('pijl_next', 'media/arrow_right_lg.gif'); window.status=''; return true;"
		   class="light_boldlink" href="piece_gallery.jsp?imageId=<%= nextImage %>">
		  <%= lan(language,"volgende") %><IMG height=12 alt="" hspace=4 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl_next>
		</A>
	<% } else { %>
		&nbsp;
	<% } %>
	</TD>	
  </TR>
  <tr>
      <td class="background" colspan="3"><img src="media/spacer.gif" width="<%= tableWidth %>" height="<%= spaceHeight %>"></td>
  </tr>
  </TABLE>
</BODY>
</HTML>
</mm:list>
</mm:cloud>

<%-- @include file="include/inc_cacheopen.jsp" --%>
