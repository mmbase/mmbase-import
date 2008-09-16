<% shop_itemHref =  "shoppingcart.jsp?p=bestel&u=" + shop_itemId; %>
<mm:node number="<%= shop_itemId %>">
	<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding-left:3px;padding-right:3px;">
				<strong><mm:field name="titel" /></strong><br>
				<mm:field name="intro" jspvar="articles_intro" vartype="String" write="false"
						><mm:isnotempty><span class="black"><%@include file="../cleanarticlesintro.jsp" %></span></mm:isnotempty
				></mm:field>
			</td>
		</tr>
	</table><%
	//	************************************* Show the image, price and shoppingcart ************* 
	%>
	<table width="100%" cellspacing="0" cellpadding="0" background="<mm:related path="posrel,images" constraints="posrel.pos=1"
				><mm:node element="images"><mm:image template="s(195x80)" /></mm:node
			></mm:related
		>" style="background-position:bottom left;background-repeat:no-repeat;">
		<tr>
			<td rowspan="2" width="45%" height="100px">
			<mm:field name="type"
				><mm:isnotempty
					><mm:compare value="standaard" inverse="true"
					><mm:compare value="niet_te_koop" inverse="true"
						><img style="float:left;margin-left:70px;margin-top:10px;" src="media/<mm:write />.gif"></mm:compare
					></mm:compare
				></mm:isnotempty
			></mm:field>
			</td>
			<td class="middle" style="padding-right:3px;">
			<%@include file="price.jsp" %>
			</td>
		</tr>
		<tr><%@include file="shoppingcart.jsp"%></tr>
		<tr><td class="titlebar" colspan="2"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr>
		</table>
		<table width="100%" cellspacing="0" cellpadding="0"><%
		//	************************************* Show discounts (if any) *******************************
		%><mm:related path="posrel,discounts"
			><mm:field name="discounts.startdate" jspvar="startdate" vartype="String" write="false"
			><mm:field name="discounts.enddate" jspvar="enddate" vartype="String" write="false"><%
			if(nowSec<=Long.parseLong(enddate)) { // do not show old discounts
					%><tr><td style="padding:3px;">
					<div class="subtitle"
						><mm:field name="discounts.title" jspvar="discounts_title" vartype="String" write="false"
							><%= discounts_title.toUpperCase() 
						%></mm:field
					></div>
					<mm:field name="discounts.body" /><br>
					 Deze actie loopt <%
						if(Long.parseLong(startdate)>=nowSec) { // only show startdate if discount is not started
							%> van <%
							timestr = startdate; %><%@include file="timestring.jsp" %><%
						} 
						%> tot en met <%
						timestr = enddate; %><%@include file="timestring.jsp" %>.
					</tr></td>
					<tr><td><img src="media/spacer.gif" height="4" width="1" border="0" alt=""></td></tr>
					<tr><td class="titlebar"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr><%
			} 
			%></mm:field
			></mm:field
		></mm:related><%
		// ************************************* the combi-discount: the source shop_item **************
		%>
		<mm:related path="discountrel,pools"
			><mm:field name="discountrel.startdate" jspvar="startdate" vartype="String" write="false"
			><mm:field name="discountrel.enddate" jspvar="enddate" vartype="String" write="false"><%
			if(nowSec<=Long.parseLong(enddate)) { // do not show old discountrel
				%><tr><td style="padding:3px;">
				<div class="subtitle"
					><mm:field name="discountrel.title" jspvar="discountrel_title" vartype="String" write="false"
						><%= discountrel_title.toUpperCase() 
					%></mm:field
				></div>
				<mm:field name="discountrel.body" /><br>
				<mm:field name="pools.number" jspvar="pools_number" vartype="String" write="false"
					><mm:list nodes="<%= pools_number %>" path="pools,posrel,items" orderby="posrel.pos" directions="UP"
						constraints="<%= "items.number != '" +  shop_itemId + "'" %>"
					><mm:first>Deze aanbieding is geldig als u ook <mm:remove referid="size"
					/><mm:size id="size"
						><mm:compare referid="size" value="1">het volgende artikel</mm:compare
						><mm:compare referid="size" value="1" inverse="true">&eacute;&eacute;n van de volgende artikelen</mm:compare
					></mm:size
					> besteld: </mm:first
					><mm:first inverse="true">, </mm:first
					><a href="<mm:url page="shop_items.jsp"><mm:param name="u"><mm:field name="items.number" /></mm:param></mm:url
						>"><mm:field name="items.titel" /></a><mm:last>.</mm:last
					></mm:list
				></mm:field> Deze actie loopt <%
					if(Long.parseLong(startdate)>=nowSec) { // only show startdate if discount is not started
						%> van <%
						timestr = startdate; %><%@include file="timestring.jsp" %><%
					} 
					%> tot en met <%
					timestr = enddate; %><%@include file="timestring.jsp" 
				%>.			
				</tr></td>
				<tr><td><img src="media/spacer.gif" height="4" width="1" border="0" alt=""></td></tr>
				<tr><td class="titlebar"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr><%
			} 
			%></mm:field
			></mm:field
		></mm:related><%
		//  ************************************* the combi-discount: the target shop_item ***************
		%><mm:list path="items1,discountrel,pools,posrel,items2" 
			constraints="<%= "items2.number = '" + shop_itemId + "' AND items1.number != '" + shop_itemId + "'" %>"
			><mm:field name="discountrel.startdate" jspvar="startdate" vartype="String" write="false"
			><mm:field name="discountrel.enddate" jspvar="enddate" vartype="String" write="false"><%
			if(nowSec<=Long.parseLong(enddate)) { // do not show old discountrel
				%><tr><td style="padding:3px;">
				<div class="subtitle"
					><mm:field name="discountrel.title" jspvar="discountrel_title" vartype="String" write="false"
						><%= discountrel_title.toUpperCase() 
					%></mm:field
				></div>
				<mm:field name="discountrel.body" /><br>
				Geniet dus nu van deze aanbieding op <a href="<mm:url page="shop_items.jsp"
						><mm:param name="u"><mm:field name="items1.number" /></mm:param></mm:url
						>"><mm:field name="items1.titel" /></a>.
				Deze actie loopt <%
					if(Long.parseLong(startdate)>=nowSec) { // only show startdate if discount is not started
						%> van <%
						timestr = startdate; %><%@include file="timestring.jsp" %><%
					} 
					%> tot en met <%
					timestr = enddate; %><%@include file="timestring.jsp" 
				%>.			
				</tr></td>
				<tr><td><img src="media/spacer.gif" height="4" width="1" border="0" alt=""></td></tr>
				<tr><td class="titlebar"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr><%
			} 
			%></mm:field
			></mm:field
		></mm:list><%
		// ************************************* Show the body *************************************
		%><mm:import id="body">BESCHRIJVING</mm:import
		><tr><td style="padding:3px;"><a name="body"></a>
					<div class="subtitle"><mm:write referid="body" /></div>
					<mm:field name="body"><mm:isnotempty><span class="black"><mm:write /></span></mm:isnotempty></mm:field>
					<mm:related path="posrel,link"  orderby="posrel.pos" directions="UP"
						><li><a target="_blank" href="<mm:field name="link.url" />" title="<mm:field name="link.alt_tekst"/>">
						<mm:field name="link.titel" /></a><br>
					</mm:related>
          <mm:related  path="posrel,artikel" orderby="posrel.pos">
            <br/><br/>
            <%@include file="../relatedarticle.jsp"%>
          </mm:related>
				</td>
			</tr><%
		// ************************************* Related products (from dienstenpakketten) ************
	%><mm:related path="posrel,products" orderby="posrel.pos" directions="UP"
		><mm:first
		   ><mm:import id="product">UIT DE DIENSTENPAKKETTEN</mm:import
   	   ><tr><td><img src="media/spacer.gif" height="4" width="1" border="0" alt=""></td></tr>
   	   <tr><td class="titlebar"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr>
   	   <tr><td style="padding:3px;"><a name="product">
			   <div class="subtitle"><mm:write referid="product" /></div>
	   </mm:first
	   ><mm:node element="products"
	      ><mm:field name="titel" jspvar="articles_intro" vartype="String" write="false" 
	      ><mm:field name="number" id="products_number" write="false" 
	      /><mm:related path="posrel1,producttypes,posrel1,pagina" max="1"
	         ><li><a href="producttypes.jsp?p=<mm:field name="pagina.number" 
		         />&pool=<mm:field name="producttypes.number" />&product=<mm:write referid="products_number" 
		            />"><%@include file="../cleanarticlesintro.jsp" %></a><br>
		   </mm:related
		   ></mm:field
		></mm:node
		><mm:last>
		   </tr></td>
		</mm:last
	></mm:related><%
	// ************************************* Show the thumbnails ********************************
	%><%
	imageId = "";
	offsetId = "";
	int totalNumberOfThumbs = 0;
	boolean thisIsNotFirst = false; 
	%><mm:list nodes="<%= shop_itemId %>" path="items,posrel,images"
		orderby="posrel.pos" directions="UP" constraints="posrel.pos > 1"
		><mm:field name="images.number" jspvar="images_number" vartype="String" write="false"><% 
			if(thisIsNotFirst) { 
				imageId += ","; 
			} else {
				offsetId = images_number;
			}
			thisIsNotFirst = true;
			imageId += images_number; 
			totalNumberOfThumbs++;
		%></mm:field
	></mm:list
	><mm:list nodes="<%= shop_itemId %>" path="items,posrel,attachments"
		orderby="posrel.pos" directions="UP"
		><mm:field name="attachments.number" jspvar="attachments_number" vartype="String" write="false"><% 
			if(thisIsNotFirst) { 
				imageId += ","; 
			} else {
				offsetId = attachments_number;
			}
			thisIsNotFirst = true;
			imageId += attachments_number;
			totalNumberOfThumbs++;
		%></mm:field
	></mm:list><%
	
	String slideshowUrl = "slideshow.jsp?p=" + paginaID + "&u=" +  shop_itemId + "&offset=" +  offsetId;

	String previousImage = "-1";
	String nextImage = "-1";
	String thisImage = "";
	String otherImages = "";
	int totalNumberOfImages = 1;
   	int thisImageNumber = 1;
	
	int thisThumbNailNumber = 0;
	int numberInRow = 5;
	while(thisThumbNailNumber<totalNumberOfThumbs) { 
		if(thisThumbNailNumber==0) { 
			%><mm:import id="thumbs">KIJK DICHTERBIJ</mm:import
			><tr><td><a name="thumbs"></a><img src="media/spacer.gif" height="8" width="1" border="0" alt=""></td></tr>
			<tr><td class="titlebar"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr>
			<tr><td style="padding:3px;"><div class="subtitle"><mm:write referid="thumbs" /></div></td></tr>
			<tr><td>
			<table cellspacing="0" cellpadding="0" width="100%"><tr><%
		} 
		if(thisThumbNailNumber%numberInRow!=0) { 
			%><td width="16"><img src="media/spacer.gif" height="1" width="16" border="0" alt=""></td><%
		} 
		%><td class="thumbnailparent" width="<%= 100/numberInRow %>%">
				<table cellspacing="0" cellpadding="0" class="thumbnail">
					<tr><td class="thumbnail">
						<%@include file="../splitimagelist.jsp" 
						%><a href="<%= pageUrl %>" onClick="javascript:launchCenter('<%= "/" + ph.getSubDir(cloud,paginaID) + slideshowUrl + "&i=" + imageId %>', 'center', 550, 740);setTimeout('newwin.focus();',250);">
						<mm:listnodes type="images" constraints="<%= "number='" + thisImage + "'" %>">
							<img src="<mm:image template="s(32x54)" />" border="0" alt="">
						</mm:listnodes
						><mm:listnodes type="attachments" constraints="<%= "number='" + thisImage + "'" %>"
							><mm:field name="filename" jspvar="attachments_filename" vartype="String" write="false"><%
								if(attachments_filename.indexOf(".pdf")>-1){ 
									%><img src="media/pdf.gif" alt="" border="0"><% 
								} else if(attachments_filename.indexOf(".doc")>-1){ 
									%><img src="media/word.gif" alt="" border="0"><% 
								} else if(attachments_filename.indexOf(".xls")>-1){ 
									%><img src="media/xls.gif" alt="" border="0"><% 
								} else if(attachments_filename.indexOf(".ppt")>-1){
									%><img src="media/ppt.gif" alt="" border="0"><%
								} else if(attachments_filename.indexOf(".mp3")>-1){
									%><img src="media/mp3.gif" alt="" border="0"><%
								} else if(attachments_filename.indexOf(".mpeg")>-1||attachments_filename.indexOf(".mpg")>-1){
									%><img src="media/mpg.gif" alt="" border="0"><%
								} else if(attachments_filename.indexOf(".swf")>-1){
									%><img src="media/flash.gif" alt="" border="0"><%
								} else {
									%><img src="media/download.gif" alt="" border="0"><%
								}
							%></mm:field
						></mm:listnodes
						></a>
					</td></tr>
				</table>
			<a href="<%= pageUrl %>" onClick="javascript:launchCenter('<%=  "/" + ph.getSubDir(cloud,paginaID) + slideshowUrl + "&i=" + imageId 
				%>', 'center', 550, 740);setTimeout('newwin.focus();',250);"><mm:node number="<%= thisImage %>"><mm:field name="title" /></mm:node></a>
		</td><%
		imageId = nextImage; 
		thisThumbNailNumber++;
		if(thisThumbNailNumber%numberInRow==0) { %></tr>
			<tr><td colspan="<%= (numberInRow*2)-2 %>"><img src="media/spacer.gif" height="16" width="1" border="0" alt=""></td></tr>
			<tr><% 
		} 
	}
	if(thisThumbNailNumber>0) {
		while(thisThumbNailNumber%numberInRow!=0) { 
			thisThumbNailNumber++;
			%><td width="16"><img src="media/spacer.gif" height="1" width="16" border="0" alt=""></td>
			<td><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td><%			
		}
			%></tr></table>
		</td></tr>
		<tr><td><img src="media/spacer.gif" height="4" width="1" border="0" alt=""></td></tr><%
	}
	// ************************************* Show articles ********************************
	%><mm:remove referid="thumbnailsfound" 
	/><mm:list nodes="<%= shop_itemId %>" path="items,posrel,artikel" orderby="posrel.pos" directions="UP">
	<tr><td><a name="<mm:field name="artikel.number" />"></a><img src="media/spacer.gif" height="8" width="1" border="0" alt=""></td></tr>
	<tr><td  class="titlebar"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr>
	<tr><td style="padding:3px;">
				<div class="subtitle"><mm:field name="artikel.titel_fra" jspvar="articles_subtitle" vartype="String" write="false"
						><%=articles_subtitle.toUpperCase() 
				%></mm:field></div>
				<mm:field name="artikel.intro" />
			</td>
	</tr>
	<mm:last><tr><td><img src="media/spacer.gif" height="4" width="1" border="0" alt=""></td></tr></mm:last
	></mm:list>
</table>
</mm:node>