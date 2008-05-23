<% // *** article + articles in middle and left column *** %>
<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
   <%@include file="includes/top1_params.jsp" %>
   <%@include file="includes/top2_cacheparams.jsp" %>
   <cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
   <%@include file="includes/top3_nav.jsp" %>
   <%@include file="includes/top4_head.jsp" %>
   <%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
   <br/>
   <table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
      <tr>
    <td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
            <%@include file="includes/navleft.jsp" %>
            <br/>
            <jsp:include page="includes/teaser.jsp">
               <jsp:param name="s" value="<%= paginaID %>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
               <jsp:param name="sr" value="0" />
            </jsp:include>
   </td>
   <td style="width:374;vertical-align:top;padding-left:10px;padding-bottom:10px;">
     <%@include file="includes/page_intro.jsp" %>
	  <mm:node number="<%=paginaID%>">
	     <mm:related path="posrel,dossier" orderby="posrel.pos">
  	      <mm:node element="dossier">
		   <% 
		   String imageId = "";
		   String offsetID= "";
		   int totalNumberOfThumbs = 0;
		   int totalNumberOfImages = 0;
         boolean thisIsNotFirst = false; %>
         <mm:related path="posrel,images" orderby="posrel.pos" fields="images.number">
            <mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
            <% 
            if(thisIsNotFirst) { 
                imageId += ","; 
        	   } else { 
               offsetID= images_number; 
        	   } 
        	   thisIsNotFirst = true; 
            imageId += images_number; 
            totalNumberOfThumbs++; 
            %>
            </mm:field>
         </mm:related>
         <% 
         String slideshowUrl = "includes/slideshow.jsp?o=" + offsetId;
         String previousImage = "-1";
	      String nextImage = "-1";
	      String thisImage = "";
  		   String otherImages = "";
	      int thisImageNumber = 1;
	      int thisThumbNailNumber = 0;
	  	   int numberInRow = 5; 
	      while(thisThumbNailNumber<totalNumberOfThumbs) { %>
	 	      <%@include file="includes/splitimagelist.jsp"%>
			   <a href="javascript:void(0);" onClick="javascript:launchCenter('<%= slideshowUrl %>&i=<%= imageId %>&r=<%= rubriekID %>', 'center', 400, 600);setTimeout('newwin.focus();',250);"><%
	  	         %><mm:node number="<%= thisImage %>"><img src="<mm:image template="s(x106)" />" border="0" alt="" style="margin-bottom:10px;margin-right:10px;"></mm:node></a>
			   <% 
			   imageId = nextImage; 
			   thisThumbNailNumber++;
	       } %>
		   </mm:node>
	     </mm:related>
      </mm:node>
      <jsp:include page="includes/shorty.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="1" />
      </jsp:include>
   </td>
   <td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
      <jsp:include page="includes/shorty.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="2" />
      </jsp:include>
      <img src="media/trans.gif" height="1px" width="165px;" />
   </td>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>



