<mm:related path="posrel,images" max="1"
><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"
><mm:field name="images.number" write="false" jspvar="images_number" vartype="String"><% 

// *** position and size of images related to shorties and teasers
// imgFormat == "" is default
// Articles: when posrel.pos should be 1 or 7, imgFormat has to be "rightcolumn" to show the image
// Teasers and shorties: when imgFormat is "half_shorty" the image should be scalled to 50% of the columnwidth

	String imgFloat ="float:none;";
	String imgParams = "";
	if(posrel_pos.equals("6")) { posrel_pos = "0"; } 

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
	} else if(posrel_pos.equals("1") ){
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
	
	boolean resetLink = false;
	%><mm:node number="<%= images_number %>">
		<mm:field name="reageer" jspvar="showpopup" vartype="String" write="false"><%
			if(showpopup.equals("1")) {
				readmoreURL = "javascript:launchCenter('slideshow.jsp?i=" + images_number + "&language=" + language + "', 'center', 550, 740);"; 
				validLink = true;
				resetLink = true;
			} else {
				validLink = false;
			}
		%></mm:field>
		<mm:field name="alt_tekst" jspvar="alt_tekst" vartype="String" write="false"><%
			altTXT = alt_tekst; 
		%></mm:field>
		<table style="width:1%;<%= imgFloat %>" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding:0px;margin:0px;text-align:right;">
				<% if(validLink){	%>
						<div style="position:relative;left:-17px;top:7px;"><div style="visibility:visible;position:absolute;top:0px;left:0px;"><a href="javascript:void(0);" onClick="<%= readmoreURL %>"><img src="<%= (isSubDir? "../" : "" ) %>media/zoom.gif" border="0" alt="klik voor vergroting" /></a></div></div>
						<a href="javascript:void(0);" onClick="<%= readmoreURL %>">
				<% }	
				%><img src="<mm:image template="<%=imgParams%>"/>" alt="<%= altTXT %>"  border="0"><%
				if(validLink) { 
					%></a><%
				}	%>
				</td>
			</tr>
			<mm:field name="bron"
				><mm:isnotempty
					><tr><td class='imagecaption'>Bron: <mm:write /></td></tr>
				</mm:isnotempty
			></mm:field>
		</table>
	</mm:node><%
	if(resetLink) { readmoreURL = ""; validLink = false; }
%></mm:field
></mm:field
><mm:remove referid="relatedimagefound" 
/><mm:import id="relatedimagefound" 
/></mm:related>
