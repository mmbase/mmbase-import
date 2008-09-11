<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<%@include file="../shoppingcart/vars.jsp" %>
<%@include file="../time.jsp" %>
<%@include file="../calendar.jsp" %>
<mm:cloud jspvar="cloud">
<%
String styleSheet = request.getParameter("rs");
PaginaHelper ph = new PaginaHelper(cloud);
shop_itemHref = "shoppingcart.jsp?p=bestel&u=" + shop_itemID;
%>
<mm:node number="<%= shop_itemID %>">
	<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding-left:3px;padding-right:3px;">
				<strong><mm:field name="titel" /></strong><br>
				<mm:field name="intro"
						><mm:isnotempty><span class="black"><mm:write /></span></mm:isnotempty
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
						><img style="float:left;margin-left:70px;margin-top:10px;" src="media/shop/<mm:write />.gif"></mm:compare
					></mm:compare
				></mm:isnotempty
			></mm:field>
			</td>
			<td style="padding-right:3px;vertical-align:middle;">
			  <%@include file="price.jsp" %>
			</td>
		</tr>
		<tr><%@include file="shoppingcart.jsp"%></tr>
		<tr><td class="maincolor" colspan="2"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td></tr>
	</table>
	<table width="100%" cellspacing="0" cellpadding="0"><%
	  // ************************************* Show discounts (if any) *******************************
	  String timestr = "-1";
     %><mm:related path="posrel,discounts"
			><mm:field name="discounts.startdate" jspvar="startdate" vartype="Long" write="false"
			><mm:field name="discounts.enddate" jspvar="enddate" vartype="Long" write="false"><%
			if(nowSec<=enddate.longValue()) { // do not show old discounts
					%><tr><td style="padding:3px;">
					<div class="colortitle"
						><mm:field name="discounts.title" jspvar="discounts_title" vartype="String" write="false"
							><%= discounts_title.toUpperCase() 
						%></mm:field
					></div>
					<mm:field name="discounts.body" /><br>
					 Deze actie loopt <%
						if(startdate.longValue()>=nowSec) { // only show startdate if discount is not started
							%> van <%
							timestr = startdate.toString(); %><%@include file="timestring.jsp" %><%
						} 
						%> tot en met <%
						timestr = enddate.toString(); %><%@include file="timestring.jsp" %>.
					</tr></td>
					<tr><td><img src="media/trans.gif" height="4" width="1" border="0" alt=""></td></tr>
					<tr><td class="maincolor"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td></tr><%
			} 
			%></mm:field
			></mm:field
		></mm:related><%
		// ************************************* the combi-discount: the source shop_item **************
		%>
		<mm:related path="discountrel,pools"
			><mm:field name="discountrel.startdate" jspvar="startdate" vartype="Long" write="false"
			><mm:field name="discountrel.enddate" jspvar="enddate" vartype="Long" write="false"><%
			if(nowSec<=enddate.longValue()) { // do not show old discountrel
				%><tr><td style="padding:3px;">
				<div class="colortitle"
					><mm:field name="discountrel.title" jspvar="discountrel_title" vartype="String" write="false"
						><%= discountrel_title.toUpperCase() 
					%></mm:field
				></div>
				<mm:field name="discountrel.body" /><br>
				<mm:field name="pools.number" jspvar="pools_number" vartype="String" write="false"
					><mm:list nodes="<%= pools_number %>" path="pools,posrel,items" orderby="posrel.pos" directions="UP"
						constraints="<%= "items.number != '" +  shop_itemID + "'" %>"
					><mm:first>Deze aanbieding is geldig als u ook <mm:remove referid="size"
					/><mm:size id="size"
						><mm:compare referid="size" value="1">het volgende artikel</mm:compare
						><mm:compare referid="size" value="1" inverse="true">&eacute;&eacute;n van de volgende artikelen</mm:compare
					></mm:size
					> bestelt: </mm:first
					><mm:first inverse="true">, </mm:first
					><a href="<mm:url page="shop_items.jsp"><mm:param name="u"><mm:field name="items.number" /></mm:param></mm:url
						>"><mm:field name="items.titel" /></a><mm:last>.</mm:last
					></mm:list
				></mm:field> Deze actie loopt <%
					if(startdate.longValue()>=nowSec) { // only show startdate if discount is not started
						%> van <%
						timestr = startdate.toString(); %><%@include file="timestring.jsp" %><%
					} 
					%> tot en met <%
					timestr = enddate.toString(); %><%@include file="timestring.jsp" 
				%>.			
				</tr></td>
				<tr><td><img src="media/trans.gif" height="4" width="1" border="0" alt=""></td></tr>
				<tr><td class="maincolor"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td></tr><%
			} 
			%></mm:field
			></mm:field
		></mm:related><%
		//  ************************************* the combi-discount: the target shop_item ***************
		%><mm:list path="items1,discountrel,pools,posrel,items2" 
			constraints="<%= "items2.number = '" + shop_itemID + "' AND items1.number != '" + shop_itemID + "'" %>"
			><mm:field name="discountrel.startdate" jspvar="startdate" vartype="Long" write="false"
			><mm:field name="discountrel.enddate" jspvar="enddate" vartype="Long" write="false"><%
			if(nowSec<=enddate.longValue()) { // do not show old discountrel
				%><tr><td style="padding:3px;">
				<div class="colortitle"
					><mm:field name="discountrel.title" jspvar="discountrel_title" vartype="String" write="false"
						><%= discountrel_title.toUpperCase() 
					%></mm:field
				></div>
				<mm:field name="discountrel.body" /><br>
				Geniet dus nu van deze aanbieding op <a href="<mm:url page="shop_items.jsp"
						><mm:param name="u"><mm:field name="items1.number" /></mm:param></mm:url
						>"><mm:field name="items1.titel" /></a>.
				Deze actie loopt <%
					if(startdate.longValue()>=nowSec) { // only show startdate if discount is not started
						%> van <%
						timestr = startdate.toString(); %><%@include file="timestring.jsp" %><%
					} 
					%> tot en met <%
					timestr = enddate.toString(); %><%@include file="timestring.jsp" 
				%>.			
				</tr></td>
				<tr><td><img src="media/trans.gif" height="4" width="1" border="0" alt=""></td></tr>
				<tr><td class="maincolor"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td></tr><%
			} 
			%></mm:field
			></mm:field
		></mm:list><%
		// ************************************* Show the body (and related articles) *************************************
		%><tr><td style="padding:3px;"><a name="body"></a>
					<div class="colortitle"><bean:message bundle="LEOCMS" key="items.description" /></div>
					<mm:field name="body"><mm:isnotempty><span class="black"><mm:write /></span></mm:isnotempty></mm:field>
					<mm:related path="posrel,link"  orderby="posrel.pos" directions="UP"
						><li><a target="_blank" href="<mm:field name="link.url" />" title="<mm:field name="link.alt_tekst"/>">
						<mm:field name="link.titel" /></a><br>
					</mm:related>
          <mm:related path="posrel,artikel" orderby="posrel.pos">
            <mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
            <br/><br/>
            <jsp:include page="../artikel_1_column.jsp">
               <jsp:param name="o" value="<%= artikel_number %>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
            </jsp:include>
            </mm:field>
          </mm:related>
				</td>
			</tr>
			<%
   	// ************************************* Show the thumbnails ********************************
   	%><%
   	String imageId = "";
   	offsetID= "";
   	int totalNumberOfThumbs = 0;
   	boolean thisIsNotFirst = false;
   	%><mm:list nodes="<%= shop_itemID %>" path="items,posrel,images"
   		orderby="posrel.pos" directions="UP"
   		><mm:field name="images.number" jspvar="images_number" vartype="String" write="false"><% 
   			if(thisIsNotFirst) { 
   				imageId += ","; 
   			} else {
   				offsetID= images_number;
   			}
   			thisIsNotFirst = true;
   			imageId += images_number; 
   			totalNumberOfThumbs++;
   		%></mm:field
   	></mm:list
   	><mm:list nodes="<%= shop_itemID %>" path="items,posrel,attachments"
   		orderby="posrel.pos" directions="UP"
   		><mm:field name="attachments.number" jspvar="attachments_number" vartype="String" write="false"><% 
   			if(thisIsNotFirst) { 
   				imageId += ","; 
   			} else {
   				offsetID= attachments_number;
   			}
   			thisIsNotFirst = true;
   			imageId += attachments_number;
   			totalNumberOfThumbs++;
   		%></mm:field
   	></mm:list><%
   	
   	String slideshowUrl = "includes/shop/slideshow.jsp?p=" + paginaID + "&u=" +  shop_itemID + "&offset=" +  offsetID;
      shop_itemHref = ph.createPaginaUrl(paginaID,request.getContextPath()) + "?u=" + shop_itemID;
      
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
   			%><mm:import id="thumbs"><bean:message bundle="LEOCMS" key="items.lookcloser" /></mm:import
   			><tr><td><a name="thumbs"></a><img src="media/trans.gif" height="8" width="1" border="0" alt=""></td></tr>
   			<tr><td class="maincolor"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td></tr>
   			<tr><td style="padding:3px;"><div class="colortitle"><mm:write referid="thumbs" /></div></td></tr>
   			<tr><td>
   			<table cellspacing="0" cellpadding="0" width="100%"><tr><%
   		} 
   		if(thisThumbNailNumber%numberInRow!=0) { 
   			%><td width="16"><img src="media/trans.gif" height="1" width="16" border="0" alt=""></td><%
   		} 
   		%><td class="thumbnailparent" width="<%= 100/numberInRow %>%">
   				<table cellspacing="0" cellpadding="0" class="thumbnail">
   					<tr><td class="thumbnail">
   						<%@include file="../splitimagelist.jsp" 
   						%><a href="<%= shop_itemHref %>" onClick="javascript:launchCenter('<%= "/" + ph.getSubDir(cloud,paginaID) + slideshowUrl + "&i=" + imageId %>', 'center', 550, 740);setTimeout('newwin.focus();',250);">
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
   			<a href="<%= shop_itemHref %>" onClick="javascript:launchCenter('<%=  "/" + ph.getSubDir(cloud,paginaID) + slideshowUrl + "&i=" + imageId 
   				%>', 'center', 550, 740);setTimeout('newwin.focus();',250);"><mm:node number="<%= thisImage %>"><mm:field name="titel" /></mm:node></a>
   		</td><%
   		imageId = nextImage; 
   		thisThumbNailNumber++;
   		if(thisThumbNailNumber%numberInRow==0) { %></tr>
   			<tr><td colspan="<%= (numberInRow*2)-2 %>"><img src="media/trans.gif" height="16" width="1" border="0" alt=""></td></tr>
   			<tr><% 
   		} 
   	}
   	if(thisThumbNailNumber>0) {
   		while(thisThumbNailNumber%numberInRow!=0) { 
   			thisThumbNailNumber++;
   			%><td width="16"><img src="media/trans.gif" height="1" width="16" border="0" alt=""></td>
   			<td><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td><%			
   		}
   			%></tr></table>
   		</td></tr>
   		<tr><td><img src="media/trans.gif" height="4" width="1" border="0" alt=""></td></tr><%
   	}
   	%>
   </table>
</mm:node>
</mm:cloud>