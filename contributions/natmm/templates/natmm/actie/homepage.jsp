<%@include file="../includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<%
PaginaHelper ph = new PaginaHelper(cloud);
%>
<%@include file="../includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<mm:import id="subdir" />
<%@include file="../includes/top4_head.jsp" %>
<div style="position:absolute"><%@include file="../includes/flushlink.jsp" %></div>
<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0" valign="top">
   <%@include file="../includes/top5b_pano.jsp" %>
</table>
<table cellspacing="10" cellpadding="0" style="width:724px;vertical-align:top;" align="center" border="0">
   <tr>
   <mm:node number="<%= rubriekID %>" notfound="skip">
     <%
       int rubriekNum = 0;
     %>
     <mm:related path="parent,rubriek" orderby="parent.pos" searchdir="destination" max="4">
       <mm:node element="rubriek" jspvar="thisRubriek">
         <%
         rubriekNum++;
         styleSheet = thisRubriek.getStringValue("style");
         for(int s = 0; s< style1.length; s++) {
            if(styleSheet.indexOf(style1[s])>-1) { iRubriekStyle = s; } 
         }
         %>
         <mm:relatednodes type="pagina" path="posrel,pagina" orderby="posrel.pos" max="1">
           <%@include file="includes/navsettings.jsp" %>
           <td style="vertical-align:top;width:170px;border-color:828282;border-width:1px;border-style:solid">
            <a href="index.jsp?p=<mm:field name="number"/>" title="<mm:field name="titel"/>">
            <mm:list nodes="<%= "" + thisRubriek.getNumber() %>" path="rubriek,contentrel,images" constraints="contentrel.pos='0'">
               <mm:node element="images">
                 <img src="<mm:image template="s(170)+part(0,0,170,98)" />" border="0">
               </mm:node>
               <mm:import id="image_found" />
            </mm:list>
            <mm:notpresent referid="image_found">
               <mm:relatednodes type="artikel" path="contentrel,artikel" max="1"
                  constraints="<%= artikelConstraint %>" orderby="<%= artikelOrderby %>" directions="<%= artikelDirections %>">
                  <mm:relatednodes type="images" path="posrel,images" orderby="posrel.pos" max="1">
                     <img src="<mm:image template="s(170)+part(0,0,170,98)" />" border="0">
                  </mm:relatednodes>
               </mm:relatednodes>
            </mm:notpresent>
            </a>
            <div style="padding:3px 5px 10px 5px">  
               <div style="font:bold 115%;color:#<%=color1[iRubriekStyle]%>;"><mm:field name="titel"/></div>
               <div style="line-height:110%;padding-bottom:9px;font:bold;"><mm:field name="kortetitel"/></div>
               <mm:field name="omschrijving"/>
               <mm:relatednodes type="artikel" path="contentrel,artikel" max="1"
                  constraints="<%= artikelConstraint %>" orderby="<%= artikelOrderby %>" directions="<%= artikelDirections %>">
                  <span style="font:bold 110%;color:<%= (rubriekNum == 1 ? "red" : color2[iRubriekStyle] )%>">></span> 
                  <span style="font-weight:bold;color:#<%=color1[iRubriekStyle]%>"><mm:field name="titel"/></span><%
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
                  <mm:field name="number" jspvar="pageNumber" vartype="String" write="false">
                     <a href="<%= ph.createPaginaUrl(pageNumber, request.getRequestURI()) %>" style="font:90%;color:#<%=color1[iRubriekStyle]%>;" title="<mm:field name="titel"/>"><%= sReadmore %></a> <span style="color:#<%=color1[iRubriekStyle]%>">></span>
                  </mm:field>
               </div>
            </div>
           </td>
         </mm:relatednodes>
       </mm:node>
     </mm:related>
   </mm:node>
   </tr>
</table>
</body>
<%@include file="../includes/sitestatscript.jsp" %>
</html>
</cache:cache>
</mm:cloud>
