<mm:related path="posrel,images" max="1"
><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"
><mm:field name="images.number" write="false" jspvar="images_number" vartype="String"><% 

	String imgFloat ="float:none;";
  String imgWidth = "";
	if(posrel_pos.equals("-1")) { posrel_pos = "0"; } 
  
  // this include supports the settings of option_lists/images_position_onecolumn.xml
  
	if(posrel_pos.equals("0")){          // original size, left
		imgFloat = "float:left;margin-right:10px;margin-bottom:5px;margin-top:3px;";
	} else if(posrel_pos.equals("5")){   // original size, no floating text
		imgFloat = "float:none;";
	} else if(posrel_pos.equals("2")){   // small left
		imgWidth = "83";
		imgFloat = "float:left;margin-right:10px;margin-bottom:5px;margin-top:3px;";
	} else if(posrel_pos.equals("3")){   // small right
		imgWidth = "83";
		imgFloat = "float:right;margin-left:10px;margin-bottom:5px;margin-top:3px;";
	} else if(posrel_pos.equals("4")){   // large
		imgWidth = "682";
		imgFloat = "float:center;padding-bottom:10px;";
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
		<div class="caption" style="width:<%= imgWidth %>px;<%= imgFloat %>">
         <% if(validLink){	%>
               <div style="position:relative;right:7px;top:7px;"><div style="visibility:visible;position:absolute;top:0px;right:0px;"><a href="javascript:void(0);" onClick="<%= readmoreURL %>"><img src="<%= (isSubDir? "../" : "" ) %>media/zoom.gif" border="0" alt="<bean:message bundle="<%= "VANHAM." + language %>" key="cv.click.to.enlarge" />" /></a></div></div>
               <a href="javascript:void(0);" onClick="<%= readmoreURL %>">
         <% }	
         %><img src="<mm:image template="<%= (!"".equals(imgWidth) ? "s(" + imgWidth + ")" : "" ) %>"/>" alt="<%= altTXT %>"  border="0"><%
         if(validLink) { 
            %></a><%
         }	%>
			<mm:field name="bron"
				><mm:isnotempty
					><bean:message bundle="<%= "VANHAM." + language %>" key="slide.photography" />: <mm:write />
				</mm:isnotempty
			></mm:field>
		</div>
	</mm:node><%
	if(resetLink) { readmoreURL = ""; validLink = false; }
%></mm:field
></mm:field
><mm:remove referid="relatedimagefound" 
/><mm:import id="relatedimagefound" 
/></mm:related>
