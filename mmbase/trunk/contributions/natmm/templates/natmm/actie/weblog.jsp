<%@include file="../includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<%
PaginaHelper ph = new PaginaHelper(cloud);
%>
<%@include file="../includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="../includes/top4_head.jsp" %>
<div style="position:absolute"><%@include file="../includes/flushlink.jsp" %></div>
<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0" valign="top">
   <%@include file="../includes/top5b_pano.jsp" %>
</table>
<% 
Calendar cal = Calendar.getInstance();
cal.setTime(now);
cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0);
long nowDay = cal.getTime().getTime()/1000; // the begin of today
long oneDay = 24*60*60;
%>
<mm:node number="<%= paginaID %>">
  <%@include file="includes/navsettings.jsp" %>
  <% 
  if(artikelID.equals("-1")) { 
    // last article before tomorrow
    objectConstraint = "artikel.begindatum < '" + (nowDay+oneDay) + "'";  %>
    <mm:relatednodes type="artikel" path="contentrel,artikel" orderby="begindatum" directions="DOWN" max="1"
       constraints="<%= objectConstraint %>">
       <mm:field name="number" jspvar="artikel_number" vartype="String" write="false">
          <% artikelID = artikel_number;%>
       </mm:field>
    </mm:relatednodes>
    <%   
  } 
  %>
  <table cellspacing="0" cellpadding="0" width="744" align="center" border="0" valign="top">
    <tr>
      <td style="padding-right:0px;padding-left:10px;padding-bottom:10px;vertical-align:top;padding-top:4px">
        <%@include file="includes/homelink.jsp" %>
        <% 
        if(menuType==QUOTE) {
           // *** Flark specific code ***
           // last article before today
           objectConstraint = "artikel.begindatum < '" + nowDay + "'"; %>
           <mm:relatednodes type="artikel" path="contentrel,artikel" max="1" orderby="begindatum" directions="DOWN"
               constraints="<%= objectConstraint %>">
             <div style="padding-bottom:10px">
               <table cellSpacing="0" cellPadding="0" style="vertical-align:top;width:170px;border-color:828282;border-width:1px;border-style:solid">
                 <mm:relatednodes type="images" path="posrel,images" orderby="posrel.pos" max="1">
                   <tr>
                     <td><img src='<mm:image template="s(170)+part(0,0,170,98)" />'></td>
                   </tr>
                 </mm:relatednodes>
                 <tr>
                   <td style="padding:5px 10px 10px 10px">
                     <mm:field name="begindatum" jspvar="dummy" vartype="Long">
                       <% if (dummy.longValue() < nowDay - oneDay) { %><b>Vorige gast</b><% } else { %><b>Gisteren</b><% } %><br>
                     </mm:field>
                     <span style="font:bold 110%;color:red">></span> 
                     <a href="weblog.jsp?p=<%= paginaID%>&a=<mm:field name="number"/>" class="maincolor_link_shorty" style="font-weight:bold"><mm:field name="titel"/></a> op De Flark<br>
                     <a href="weblog.jsp?p=<%= paginaID%>&a=<mm:field name="number"/>" class="maincolor_link"  style="font-size:90%;">Lees verder</a>
                     <span class="colortxt">></span>
                   </td>
                 </tr>
               </table>
             </div>
           </mm:relatednodes>
           <% 

           // today's article
           objectConstraint = "artikel.begindatum > '" + nowDay + "' AND artikel.begindatum < '" + (nowDay+oneDay) + "'"; %>
           <mm:relatednodes type="artikel" path="contentrel,artikel" max="1" orderby="begindatum" directions="DOWN"
               constraints="<%= objectConstraint %>">
             <div style="padding-bottom:10px">
               <table cellSpacing="0" cellPadding="0" style="vertical-align:top;width:170px;border-color:828282;border-width:1px;border-style:solid">
                 <mm:relatednodes type="images" path="posrel,images" orderby="posrel.pos" max="1">
                   <tr>
                     <td><img src='<mm:image template="s(170)+part(0,0,170,98)" />'></td>
                   </tr>
                 </mm:relatednodes>
                 <tr>
                   <td style="padding:5px 10px 10px 10px">
                     <b>Gast van vandaag</b><br>
                     <span style="font:bold 110%;color:red">></span> 
                     <a href="weblog.jsp?p=<%= paginaID%>&a=<mm:field name="number"/>" class="maincolor_link_shorty" style="font-weight:bold"><mm:field name="titel"/></a> op De Flark<br>
                   </td>
                 </tr>
               </table>
             </div>
           </mm:relatednodes>
           <% 

           // first article after today
           objectConstraint = "artikel.begindatum > '" + (nowDay+oneDay) + "'"; 
           %>
           <mm:relatednodes type="artikel" path="contentrel,artikel" max="1" orderby="begindatum" directions="UP"
               constraints="<%= objectConstraint %>">
             <div style="padding-bottom:10px">
               <table cellSpacing="0" cellPadding="0" style="vertical-align:top;width:170px;border-color:828282;border-width:1px;border-style:solid">
                 <mm:relatednodes type="images" path="posrel,images" orderby="posrel.pos" max="1">
                   <tr>
                     <td><img src='<mm:image template="s(170)+part(0,0,170,98)" />'></td>
                   </tr>
                 </mm:relatednodes>
                 <tr>
                   <td style="padding:5px 10px 10px 10px">
                     <mm:field name="begindatum" jspvar="dummy" vartype="Long">
                       <% if (dummy.longValue() > nowDay + 2*oneDay) { %><b>Volgende gast</b><% } else { %><b>Morgen</b><% } %><br>
                     </mm:field>
                     <span style="font:bold 110%;color:red">></span> 
                     <a href="weblog.jsp?p=<%= paginaID%>&a=<mm:field name="number"/>" class="maincolor_link_shorty" style="font-weight:bold"><mm:field name="titel"/></a> op De Flark
                   </td>
                 </tr>
               </table>
             </div>
           </mm:relatednodes>
           <%
        } %>
        <%@include file="includes/mailtoafriend.jsp" %>
      </td>

      <td style="padding-right:0px;padding-left:10px;padding-bottom:10px;vertical-align:top;padding-top:10px">
         <mm:node number="<%= artikelID%>" notfound="skip">
         <mm:field name="intro" jspvar="text" vartype="String" write="false">
            <% if(text!=null) { 
                  text = HtmlCleaner.cleanText(text,"<",">","").trim();
               } else {
                  text = "";
               }
            %>
            <table cellspacing="0" cellpadding="0" style="vertical-align:top;width:350px">
            <tr align="left" valign="top">
               <td <% if(!text.equals("")) { %>style="width:170px;"<% } %>>
                 <mm:relatednodes type="images" path="posrel,images" orderby="posrel.pos"  max="1">
                   <img src="<mm:image template="s(170)+part(0,0,170,98)" />"><br>
                 </mm:relatednodes>
                 <div style="padding-left:6px;padding-top:8px;">
                   <mm:node number="<%= paginaID %>">
                     <div class="colortitle" style="font:bold 110%;"><mm:field name="titel"/></div>
                     <div style="padding-bottom:5px;"><b><mm:field name="kortetitel"/></b></div>
                   </mm:node>
                   <% 
                   if(text.equals("")) {
                     %> 
                     <span style="font:bold 110%;color:red">></span>
                     <span class="colortitle"><mm:field name="titel"/></span>
                     <span class="colortxt"><mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false"
                     ><mm:time time="<%=artikel_begindatum%>" format="d MMM yyyy"/></mm:field></span>
                     <%
                   }
                   %>
                 </div>
               </td>
               <% 
               if(!text.equals("")) {
                  %> 
                  <td style="padding-left:10px;padding-top:7px;">
                     <span style="font:bold 110%;color:red">></span>
                     <span class="colortitle"><mm:field name="titel"/></span><br/>
                     <span class="colortxt"><mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false"
                     ><mm:time time="<%=artikel_begindatum%>" format="d MMM yyyy"/></mm:field></span><br/>
                     <b><%= text %></b>
                  </td>
                  <%
               } %>
             </tr>
             <tr align="left" valign="top">
               <td colspan="2" style="padding:10px 0px 10px 7px">
                  <mm:field name="tekst"/>
                  <mm:relatednodes type="attachments" path="related,attachments" orderby="attachments.title">
                     <%
                      String imgName = ""; 
                      String docType = "";
                     %>
                     <mm:field name="filename" jspvar="dummy" vartype="String" write="false">
                        <%@include file="includes/attachmentsicon.jsp"%>
                     </mm:field>
                     <mm:first>
                        <table class="dotline"><tr><td height="3"></td></tr></table>
                     </mm:first>
                     <span style="padding-left:5px; padding-right:5px"><a href="<mm:attachment />"><img src="../<%= imgName 
                       %>" alt="download <%= docType %>: <mm:field name="title"
                       />" border="0" style="vertical-align:text-bottom" /></a></span>
                  </mm:relatednodes>
                  <% 
                  int iParCntr = 1;
                  boolean showNextDotLine = false;
                  boolean floatingText = true;
                  %>
                  <mm:field name="reageer" jspvar="showdotline" vartype="String" write="false"
                     ><mm:related path="posrel,paragraaf" fields="paragraaf.number" orderby="posrel.pos"
                        ><%@include file="../includes/relatedparagraph.jsp" 
                     %></mm:related
                     ><mm:related path="readmore,paragraaf" fields="paragraaf.number" orderby="readmore.pos"
                        ><%@include file="../includes/relatedparagraph.jsp" 
                     %></mm:related
                  ></mm:field>
               </td>
            </tr>
            </table>
         </mm:field>
         </mm:node>
      </td>
      <td style="padding-right:10px;padding-left:10px;padding-bottom:10px;padding-top:10px;vertical-align:top;width:190px;">
         <jsp:include page="includes/nav.jsp">
            <jsp:param name="a" value="<%= artikelID %>" />
            <jsp:param name="p" value="<%= paginaID %>" />
         </jsp:include>
      </td>
    </tr>
  </table>
</mm:node>
<%@include file="includes/footer.jsp" %>
</body>
<%@include file="../includes/sitestatscript.jsp" %>
</html>
</cache:cache>
</mm:cloud>
