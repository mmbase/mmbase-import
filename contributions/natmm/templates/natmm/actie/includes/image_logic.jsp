<mm:related path="posrel,images" max="1"
><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"
><mm:field name="images.number" write="false" jspvar="images_number" vartype="String"><% 

if(!posrel_pos.equals("1")) { // 1 - linker kolom, geschaald op kolom breedte, vaste hoogte

   String imgFloat ="float:none;";
   String imgParams = "";
   if(posrel_pos.equals("2")){ // 2 - midden kolom, klein links
   	imgParams = "s(83)";
   	imgFloat = "float:left;margin-right:10px;";
   } else if(posrel_pos.equals("3")){ // 3 - midden kolom, klein rechts
   	imgParams = "s(83)";
   	imgFloat = "float:right;margin-left:10px;";
   } else if(posrel_pos.equals("4")){ // 4 - midden kolom, halve kolom links
   	imgParams = "s(170)";
   	imgFloat = "float:left;margin-right:10px;";
   } else if(posrel_pos.equals("5")){ // 5 - midden kolom, halve kolom rechts
   	imgParams = "s(170)";
   	imgFloat = "float:right;margin-left:10px;";
   } else if(posrel_pos.equals("6")){ // 6 - midden kolom, groot
   	imgParams = "s(340)";
   	imgFloat = "float:none;";
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
      %><mm:field name="alt_tekst" jspvar="alt_tekst" vartype="String" write="false"><%
         altTXT = alt_tekst; 
      %></mm:field>
   	<table style="width:1%;<%= imgFloat %>" border="0" cellspacing="0" cellpadding="0">
         <tr>
            <td style="padding:0px;margin:0px;text-align:right;">
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
      </table>
   </mm:node><%
   if(resetLink) { readmoreURL = ""; validLink = false; }
} 
%></mm:field
></mm:field
></mm:related>