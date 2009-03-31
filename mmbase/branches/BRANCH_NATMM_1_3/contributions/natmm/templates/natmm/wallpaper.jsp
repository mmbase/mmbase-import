<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>

<%
if(imgID.equals("-1")){
   %>
   <%@include file="includes/top2_cacheparams.jsp" %>
   <cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
	<%@include file="includes/top3_nav.jsp" %>
	<%@include file="includes/top4_head.jsp" %>
	<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
	<% if (isNaardermeer.equals("true")) { %>      
      <div style="position:absolute; left:681px; width:70px; height:216px; background-image: url(media/natmm_logo_rgb2.gif); background-repeat:no-repeat;"></div>
  <% } %>
   <br>
   <table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <tr>
   	<td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;">
      	<%@include file="includes/navleft.jsp" %>
      	<br>
      	<jsp:include page="includes/teaser.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="sr" value="0" />
         </jsp:include>
   	</td>
   	<td style="vertical-align:top;width:559px;padding-left:10px;padding-right:10px;">
   	  <jsp:include page="includes/fun/fun_index.jsp">
   	      <jsp:param name="p" value="<%= paginaID %>" />
   	      <jsp:param name="r" value="<%= rubriekID %>" />
   	      <jsp:param name="isNaardermeer" value="<%= isNaardermeer %>" />
   	   </jsp:include>
   	</td>
   </tr>
   </table>
	<%@include file="includes/footer.jsp" %>
	</cache:cache><%
} else { %>
	<%@include file="includes/top4_head.jsp" %>
   <mm:import jspvar="size" externid="size">small</mm:import><% 
   String imgParams = "s(500!x320!)";
   if(size.equals("medium")){ 
   	imgParams = "s(800x600)";
   } else if(size.equals("large")){ 
   	imgParams = "s(1024x768)";
   }
   if(size.equals("small")){ // the intro page
		String requestURL = javax.servlet.http.HttpUtils.getRequestURL(request).toString();
      requestURL = requestURL.substring(0,requestURL.lastIndexOf("/"));
      %><mm:node number="<%= imgID %>">
         <html>
         <head>
            <meta http-equiv="imagetoolbar" content="no" />
            <title><mm:field name="titel" /></title>
            <script type="text/javascript" language="javaScript" src="scripts/launchcenter.js"></script>
         </head>
         <body bgcolor="#ffffff" leftmargin=10 topmargin=2 marginwidth="0" marginheight="0">
			   <table border="0" cellspacing="2" cellpadding="2">
					<tr>
						<td width="7"></td>
						<td align="left" style="font-family:Arial;font-size:70%;">
							<span class="colortitle">Wallpaper</span>
						</td>
						<td width="105" align="right"><a href="javascript:window.close()"><img src="media/close_fun.gif" border="0"></a>
						</td>
						<td width="5"></td>	
					</tr>
               <tr>
						<td width="7"></td>
                  <td colspan="4">
                     <img src="<mm:image template="<%=imgParams%>" />" border="0" alt="<mm:field name="titel" /> ">
                  </td>
               </tr>
					<tr>
						<td width="7"></td>
						<td style="font-family:Arial;font-size:70%;">
							<mm:relatednodes type="dossier"><strong><mm:field name="naam"/></strong></mm:relatednodes>
							&nbsp;|&nbsp;<mm:field name="titel"/>
						</td>
						<td width="105" style="font-family:Arial;font-size:70%;">&nbsp;&nbsp;Resolutie</td>
						<td width="5"></td>
					</tr>
               <tr>
						<td width="7"></td>
                  <td style="font-family:Arial;font-size:70%;" width="380">
							<nobr>Klik op een van de resoluties om deze wallpaper te downloaden.</nobr>
						</td>
						<td width="105">
							<%-- NMCMS-639 --%>
							<input type="submit" value="800 x 600" class="submit_image" style="width:105;align:center;" onclick="javascript:OpenWindow('<%= requestURL + "/" + (isSubDir? "../" : "" ) %>wallpaper.jsp?i=<%= imgID %>&size=medium','','toolbar=no,menubar=no,location=no,height=600,width=800,scrollbars=yes,resizable=yes');"/>
						</td>
						<td width="5"></td>
					</tr>	
					<tr>
						<td width="7"></td>	
						<td rowspan="2" style="font-family:Arial;font-size:70%;">
							Klik vervolgens met de rechtermuisknop op de foto en tenslotte op<br><strong>"Als
                     achtergrond gebruiken"</strong> om de foto op uw bureaublad te plaatsen.
						</td>
						<td width="105">
							<table class="dotline"><tr><td height="3"></td></tr></table>
						</td>	
						<td width="5"></td>
					</tr>
					<tr>
						<td width="7"></td>
						<td width="105">
							<%-- NMCMS-639 --%>
							<input type="submit" value="1024 x 768" class="submit_image" style="width:105;align:center;" onclick="javascript:OpenWindow('<%= requestURL + "/" + (isSubDir? "../" : "" ) %>wallpaper.jsp?i=<%= imgID %>&size=large','','toolbar=no,menubar=no,location=no,height=768,width=1024,scrollbars=yes,resizable=yes');"/>
						</td>
						<td width="5"></td>
               </tr>
            </table>
         </body>
      </html>
      </mm:node><%
   } else { 
      %><mm:node number="<%= imgID %>">
         <html>
         <head>
            <meta http-equiv="imagetoolbar" content="no" />
            <script type="text/javascript" language="javaScript" src="scripts/launchcenter.js"></script>
            <title><mm:field name="titel" /></title>
         </head>
         <body leftmargin=0 topmargin=0 marginwidth="0" marginheight="0" onload="window.opener.close()">
            <div id="overDiv" style="position:absolute; visibility:hide;z-index:1;"></div>
            <script language="javascript" src="scripts/overlib.js"></script>
               <%-- NMCMS-639 --%>
               <a href="javascript:void(0);" onclick="javascript:window.close()" onMouseOver="overlib('<b>Klik met de rechtermuisknop</b> op de foto en tenslotte op &quot;Als achtergrond gebruiken&quot; om de foto  op uw bureaublad te plaatsen <p><b>Klik met de linkermuisknop</b> om dit venster te sluiten')" onMouseOut="nd()">
                  <img src="<mm:image />" border="0" alt="<mm:field name="titel" />"></a>
            <%@include file="includes/image_metadata.jsp" %>
         </body>
      </html>
   </mm:node><%
   }
} %>
</mm:cloud>
