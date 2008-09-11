<% // include from shorty, teaser, article or paragraph
%><mm:related path="posrel,images" max="1"
><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"
><mm:field name="images.number" write="false" jspvar="images_number" vartype="String"><% 


if(javax.servlet.http.HttpUtils.getRequestURL(request).indexOf("weblog.jsp")>0) {

	// images have different position for weblog articles, compare the two option_lists
	if(posrel_pos.equals("1")) { posrel_pos = "2";
	} else if(posrel_pos.equals("5")) { posrel_pos = "1";
	} else if(posrel_pos.equals("6")) { posrel_pos = "4";
	}

} 

// *** position and size of images related to shorties and teasers
// imgFormat == "" is default
// Articles: when posrel.pos should be 1 or 7, imgFormat has to be "rightcolumn" to show the image
// Teasers and shorties: when imgFormat is "half_shorty" the image should be scalled to 50% of the columnwidth


boolean imagePartOfColumn = (imgFormat.equals("rightcolumn") || imgFormat.equals("fittothirdcolumn")) ^ !(posrel_pos.equals("1") || posrel_pos.equals("7"));

boolean isShortyOrTeaserImage = imgFormat.indexOf("shorty")>-1;
boolean fitToThirdColumn = imgFormat.indexOf("fittothirdcolumn")>-1;

if(isShortyOrTeaserImage || fitToThirdColumn || imagePartOfColumn) {

	String imgFloat ="float:none;";
	String imgParams = "";
	if(posrel_pos.equals("6")) { posrel_pos = "0"; } 

	if(!isShortyOrTeaserImage) { // *** article
	
		if(posrel_pos.equals("0")){
			imgFloat = "float:left;margin-right:10px;margin-bottom:5px;margin-top:3px;";
		} else if(posrel_pos.equals("5")){
			imgFloat = "float:none;";
		} else if(posrel_pos.equals("2")){
			imgParams = "s(83)";
			imgFloat = "float:left;margin-right:10px;margin-bottom:5px;margin-top:3px;";
		} else if(posrel_pos.equals("3")){
			imgParams = "s(83)";
			imgFloat = "float:right;margin-left:10px;margin-bottom:5px;margin-top:3px;";
		} else if(posrel_pos.equals("1") || fitToThirdColumn){
			imgParams = "s(165)";
			imgFloat = "float:none;";
		} else if(posrel_pos.equals("7")){
			imgParams = "";
			imgFloat = "float:none;";
		} else if(posrel_pos.equals("4")){
			if(imgFormat.equals("route")) {
				imgParams = "s(500)";
			} else {
				imgParams = "s(352)";
				imgFloat = "float:center;padding-bottom:10px;";
			}
		}
	
	} else { 
		// *** shorty and teaser
		// imgFormat: shorty, half_shorty, orgsize_shorty
		// posrel_pos: 0= original size, 1= scaled
		// shortyRol: 0= left column (only used for teasers), 1= middle column, 2= right column
		
		if(imgFormat.equals("half_shorty")&&posrel_pos.equals("1")) {
			if(!shortyRol.equals("1")) {
				imgParams = "s(83)";
			} else {
				imgParams = "s(165)";
			}
		} else if(imgFormat.equals("shorty")&&posrel_pos.equals("1")) {
			if(!shortyRol.equals("1")) {
				imgParams = "s(165)";
			} else {
				imgParams = "s(354)";
			}
		} else {
			imgParams = "";
		}
	} 
	boolean resetLink = false;
	%><mm:node number="<%= images_number %>"><%
						
			if(readmoreURL.equals("")) { 
				%><mm:field name="reageer" jspvar="showpopup" vartype="String" write="false"><%
					if(showpopup.equals("1")) {
						String requestURL = javax.servlet.http.HttpUtils.getRequestURL(request).toString();
						requestURL = requestURL.substring(0,requestURL.lastIndexOf("/")); 
						readmoreURL = "javascript:launchCenter('" + requestURL + "/" + (isSubDir? "../" : "" ) + "includes/fotopopup.jsp?i="+ images_number + "&rs=" + styleSheet + "','foto',600,600,'location=no,directories=no,status=no,toolbars=no,scrollbars=no,resizable=yes');setTimeout('newwin.focus();',250);";
						validLink = true;
						resetLink = true;
					} else {
						validLink = false;
					}
				%></mm:field><% 
			}
			if(!isShortyOrTeaserImage) { 
				%><mm:field name="alt_tekst" jspvar="alt_tekst" vartype="String" write="false"><%
					altTXT = alt_tekst; 
				%></mm:field><% 
			} %><%-- role=<%= shortyRol %>,iForm=<%=imgFormat %>,iFloat=<%=imgFloat %>,iPar=<%= imgParams %>,rmUrl=<%= (validLink ? readmoreURL : "" ) %> --%>
			<table style="width:1%;<%= imgFloat %>" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td style="padding:0 0 0 0;margin:0px;text-align:right;">
					<%
						if(validLink){
							if(readmoreURL.indexOf("javascript:")>-1) { 
								%>
								<div style="position:relative;left:-17px;top:7px;"><div style="visibility:visible;position:absolute;top:0px;left:0px;"><a href="javascript:void(0);" onClick="<%= readmoreURL %>"><img src="<%= (isSubDir? "../" : "" ) %>media/zoom.gif" border="0" alt="klik voor vergroting" /></a></div></div>
								<a href="javascript:void(0);" onClick="<%= readmoreURL %>">
								<%
							} else {
								%><a href="<%= readmoreURL %>" <%= (!readmoreTarget.equals("") ? " target=\"" + readmoreTarget + "\"" : "" ) %>><% 
							} 
						}
						%><img src="<mm:image template="<%=imgParams%>"/>" alt="<%= altTXT %>"  border="0"><%
						if(validLink){
							%></a><%
						}
					%>
					</td>
				</tr>
				<mm:field name="bron"
					><mm:isnotempty
						><tr><td class='imagecaption'>Foto: <mm:write /></td></tr>
					</mm:isnotempty
				></mm:field>
			</table>
		</mm:node><%
		if(resetLink) { readmoreURL = ""; validLink = false; }
} 
%></mm:field
></mm:field
><mm:remove referid="relatedimagefound" 
/><mm:import id="relatedimagefound" 
/></mm:related>