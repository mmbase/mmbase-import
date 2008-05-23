<%@include file="../includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<%@include file="../includes/top2_cacheparams.jsp" %>
<% if(!isPreview) { expireTime = 3600; } // update every hour, because of changing content in rubrieken %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="../includes/top4_head.jsp" %>
<div style="position:absolute"><%@include file="/editors/paginamanagement/flushlink.jsp" %></div>
<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0" valign="top">
   <%@include file="../includes/top5b_pano.jsp" %>
</table>
<table cellspacing="10" cellpadding="0" style="width:724px;" align="center" border="0">
   <%
   String[] shortyID = new String[1];
   int i=0;
   int shortyCount = 0;
   %>
   <mm:list nodes="<%= paginaID %>" path="pagina,rolerel,shorty" orderby="rolerel.pos" directions="UP">
      <mm:field name="shorty.number" jspvar="shorty_number" vartype="String" write="false">
      <% 
      if(shortyCount%2==0) { 
         %><tr><% 
      }
      shortyID[i] = shorty_number;
      String sImage = "-1";
      %>
      <%@include file="../includes/shorty_logic_2.jsp" %>
      <mm:list nodes="<%= shortyID[i] %>" path="shorty,posrel,images" max="1">
         <mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
            <% sImage = images_number; %>
         </mm:field>
      </mm:list>
         <td colspan="2" style="vertical-align:top;width:352px;border-color:#828282;border-width:1px;border-style:solid">
            <table cellspacing="0" cellpadding="0" style="width:352px;" border="0">
            <tr>
               <td style="width:<%= (sImage.equals("-1") ? "15" : "70") %>px;vertical-align:top;">
                  <img src="../media/news.gif" border="0"><%
                  if(!sImage.equals("-1")) { 
                     if(validLink) { %><a href="<%= readmoreURL %>" title="<%= altTXT %>" target="<%= readmoreTarget %>" ><% } 
                        %><mm:node number="<%= sImage %>"
                           ><img src="<mm:image template="s(56x54!)+part(0,0,56,54)" />" border="0" alt="<%= altTXT %>"></mm:node><% 
                     if(validLink) { %></a><% }
                  }
               %></td>
               <td style="padding:3px 5px 0px 5px;vertical-align:top;">
                  <mm:node number="<%= shortyID[i] %>">
                     <mm:field name="titel" write="false" jspvar="shorty_titel" vartype="String">
                     <mm:field name="titel_zichtbaar" write="false" jspvar="shorty_tz" vartype="String">
                     <mm:field name="omschrijving" write="false" jspvar="shorty_omschrijving" vartype="String">
                     <%
                     boolean titleIsShown = false;
                     if(!shorty_titel.equals("")&&!shorty_tz.equals("0")){
                        titleIsShown = true;
                        linkTXT = shorty_titel; 
                        %>
                        <mm:import id="divstyle">font-weight:bold;line-height:95%;</mm:import>
                        <%@include file="../includes/validlink.jsp" %>
                        <mm:remove referid="divstyle"/>
                        <%
                     }
                     shorty_omschrijving =  HtmlCleaner.cleanBRs(HtmlCleaner.cleanPs(shorty_omschrijving)).trim();
                     if(!shorty_omschrijving.equals("")){ 
                        int numberOfChars = 150;
                        if(sImage.equals("-1")) { numberOfChars += 10; }
                        if(titleIsShown) { numberOfChars -= 60; }
                        if(validLink) { numberOfChars -= 30; }
                        int spacePos = shorty_omschrijving.indexOf(" ",numberOfChars); 
                        if(spacePos>-1) { 
                           shorty_omschrijving = shorty_omschrijving.substring(0,spacePos) + "&hellip;";
                        }                   
                        %><%= shorty_omschrijving %><%
                     }
                     if(validLink){
                        %>&nbsp;&nbsp;<a href="<%= readmoreURL %>" style="font-size:90%;"
                           <%= (!readmoreTarget.equals("") ? "target='" + readmoreTarget + "'" : "" ) %>
                           <%= (!altTXT.equals("") ? "title='" + altTXT + "'" : "" ) %>
                           >Lees verder ></a><%
                     } 
                     %>
                  </mm:field>
                  </mm:field>
                  </mm:field>
               </mm:node>
            </td>
         </tr>
         </table>
      <mm:last>
         <%
         if(shortyCount%2==0) { 
            %><td colspan="2" style="vertical-align:top;width:352px;border-color:#828282;border-width:1px;border-style:solid"><img src="../media/news.gif" border="0"></td></tr><% 
         }
         %>
      </mm:last>
      <% 
      if(shortyCount%2==1) { 
         %></tr><% 
      }
      shortyCount++;
      %>
      </mm:field>
   </mm:list>
   <% String [] teaserNumber = { "-1", "-1", "-1" , "-1" }; %>
   <mm:list nodes="<%= paginaID %>" path="pagina,rolerel,teaser" constraints="rolerel.pos > 0 AND rolerel.pos < 5">
      <mm:field name="teaser.number"  jspvar="teaser_number" vartype="String" write="false">
      <mm:field name="rolerel.pos"  jspvar="rolerel_pos" vartype="Integer" write="false">                     
         <% 
         teaserNumber[rolerel_pos.intValue()-1] = teaser_number; 
         %>
      </mm:field>
      </mm:field>
   </mm:list>
   <mm:node number="<%= rubriekID %>" notfound="skip">
     <%
       int rubriekNum = -1;
     %>
     <mm:related path="parent,rubriek" orderby="parent.pos" searchdir="destination" max="4">
       <mm:first><tr></mm:first>
       <mm:node element="rubriek" jspvar="thisRubriek">
         <%
         rubriekNum++;
         styleSheet = thisRubriek.getStringValue("style");
         for(int s = 0; s< NatMMConfig.style1.length; s++) {
            if(styleSheet.indexOf(NatMMConfig.style1[s])>-1) { iRubriekStyle = s; } 
         }
         %>
         <mm:relatednodes type="pagina" path="posrel,pagina" orderby="posrel.pos" max="1">
           <mm:field name="number" jspvar="pageNumber" vartype="String" write="false">
           <%@include file="includes/navsettings.jsp" %>
           <td style="vertical-align:top;width:170px;border-color:#828282;border-width:1px;border-style:solid" 
               <%= (teaserNumber[rubriekNum].equals("-1") ? "rowspan='2'" : "" ) %> >
            <a href="<%= ph.createPaginaUrl(pageNumber,request.getContextPath()) %>" title="<mm:field name="titel"/>">
            <mm:list nodes="<%= "" + thisRubriek.getNumber() %>" path="rubriek,contentrel,images" constraints="contentrel.pos='0'">
               <mm:node element="images">
                 <img src="<mm:image template="s(170)+part(0,0,170,98)" />" border="0">
               </mm:node>
               <mm:import id="image_found" />
            </mm:list>
            <mm:notpresent referid="image_found">
               <mm:relatednodes type="artikel" path="contentrel,artikel" max="1"
                  constraints="<%= objectConstraint %>" orderby="<%= objectOrderby %>" directions="<%= objectDirections %>">
                  <mm:relatednodes type="images" path="posrel,images" orderby="posrel.pos" directions="UP" max="1">
                     <img src="<mm:image template="s(170)+part(0,0,170,98)" />" border="0">
                  </mm:relatednodes>
               </mm:relatednodes>
            </mm:notpresent>
            </a>
            <div style="padding:3px 5px 10px 5px">  
               <a href="<%= ph.createPaginaUrl(pageNumber,request.getContextPath()) %>" class="hover" style="font:bold 115%;color:#<%=NatMMConfig.color1[iRubriekStyle]%>;"><mm:field name="titel"/></a>
               <div style="line-height:110%;padding-bottom:9px;font:bold;"><mm:field name="kortetitel"/></div>
               <mm:field name="omschrijving"/>
               <mm:relatednodes type="artikel" path="contentrel,artikel" max="1"
                  constraints="<%= objectConstraint %>" orderby="<%= objectOrderby %>" directions="<%= objectDirections %>">
                  <span style="font:bold 110%;color:red">></span> 
                  <a href="<%= ph.createPaginaUrl(pageNumber,request.getContextPath()) 
                     %>" class="hover" style="font-weight:bold;color:#<%=NatMMConfig.color1[iRubriekStyle]%>" title="<mm:field name="titel"/>"><mm:field name="titel"/></a><%
                  %><mm:field name="intro" jspvar="text" vartype="String" write="false"><% 
                     if(text!=null) {
                        text = HtmlCleaner.cleanText(text,"<",">");
                        if(!text.trim().equals("")) {
		                     int spacePos = text.indexOf(" ",100); 
		                     if(spacePos>-1) { 
			                     text = text.substring(0,spacePos);
		                     }
                           %>: "<%= text %>&hellip;"<%
                        } 
                     } %>
       	         </mm:field>
               </mm:relatednodes>
               <% String sReadmore = "Lees verder"; %>
               <mm:relatednodes type="paginatemplate" path="gebruikt,paginatemplate">
                  <mm:field name="url" jspvar="url" vartype="String" write="false">
                     <% 
                     if(url.indexOf("bulletinboard.jsp")>-1) {
                        sReadmore = "Uw reactie";
                     }
                     %>
                  </mm:field>
               </mm:relatednodes>
               <div style="padding-top:3px;">
                  <a href="<%= ph.createPaginaUrl(pageNumber,request.getContextPath()) %>" style="font:90%;color:#<%=NatMMConfig.color1[iRubriekStyle]%>;"
							title="<mm:field name="titel"/>"><%= sReadmore %></a> <span style="color:#<%=NatMMConfig.color1[iRubriekStyle]%>">></span>
               </div>
            </div>
           </td>
           </mm:field>
         </mm:relatednodes>
       </mm:node>
       <mm:last></tr></mm:last>
     </mm:related>
   </mm:node>
   <%
   boolean isFirst = true;
   for(int r=0; r <4; r++) { 
      if(!teaserNumber[r].equals("-1")) {
         if(isFirst) {
            %><tr><%
            isFirst = false;
         }
         shortyID[i] = teaserNumber[r];
         %>
         <%@include file="../includes/shorty_logic_2.jsp" %>
         <mm:node number="<%= shortyID[i] %>">
            <mm:relatednodes type="images" max="1">
               <td style="vertical-align:top;width:172px;border-width:0px">
               <%
               if(validLink) { %><a href="<%= readmoreURL %>" title="<%= altTXT %>" target="<%= readmoreTarget %>" ><% } 
                  %><img src="<mm:image template="s(172)" />" border="0" alt="<%= altTXT %>"><% 
               if(validLink) { %></a><% }
               %>
               </td>
            </mm:relatednodes>
         </mm:node>
         <%
      }
   } 
   if(!isFirst) { %></tr><% }
   %>
</table>
<%@include file="includes/footer.jsp" %>
</body>
<%@include file="../includes/sitestatscript.jsp" %>
</html>
</cache:cache>
</mm:cloud>
