<%@include file="../includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<%
PaginaHelper ph = new PaginaHelper(cloud);
if(rubriekExists&&pageExists) { 
   %>
   <%@include file="../includes/top2_cacheparams.jsp" %>
   <!-- cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" -->
   <mm:import id="subdir" />
   <% if(!offsetID.equals("0")){
      %><mm:import id="onload_statement">window.location='#bottom';</mm:import><%
   }
   %>
   <%@include file="../includes/top4_head.jsp" %>
   <table cellspacing="0" cellpadding="0" width="100%" align="center" border="0" valign="top">
      <%@include file="../includes/top5b_pano.jsp" %>
   </table>
   <mm:node number="<%= paginaID %>">
     <% 
     if(artikelID.equals("-1")) { 
       %>
       <mm:relatednodes type="artikel" path="contentrel,artikel" orderby="contentrel.pos" directions="UP" max="1">
          <mm:field name="number" jspvar="artikel_number" vartype="String" write="false">
             <% artikelID = artikel_number;%>
          </mm:field>
       </mm:relatednodes>
       <%   
     } 
     %>
     <table cellspacing="0" cellpadding="0" width="744" align="center" border="0" valign="top">
       <tr>
         <td style="padding-right:0px;padding-left:10px;padding-bottom:10px;vertical-align:top;padding-top:10px">
           <table class="dotline"><tr><td height="3"></td></tr></table>
           <table cellspacing="0" cellpadding="0" border="0" valign="top">
               <tr>
                  <td style="padding-top:2px;padding-right:7px;"><a href="a6-a9"><img src="../media/arrowleft_default.gif" border="0"></a></td>
                  <td><a href="a6-a9" class="hover" style="font-size:95%;">TERUG NAAR HOME</a></td>
               </tr>
           </table>
           <table class="dotline" style="margin-top:0px;"><tr><td height="3"></td></tr></table>
           <jsp:include page="../includes/teaser.jsp">
               <jsp:param name="s" value="<%= paginaID %>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
               <jsp:param name="sr" value="0" />
           </jsp:include>
           <table cellSpacing="0" cellPadding="0" border="0" style="padding-left:10px;width:170px;">
             <tr>
               <td><a href="mailto:"><img src="../media/email.gif" border="0"></a></td>
               <td style="padding-left:10px"><a href="mailto:" class="maincolor_link">Stuur deze pagina naar een vriend</a>
                 <span class="colortxt">></span></td>
             </tr>
           </table>
         </td>
      	<td style="vertical-align:top;width:100%;padding-left:10px;padding-right:10px;text-align:right;">
      	   <jsp:include page="includes/artikel_12_column.jsp">
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
               <jsp:param name="lnr" value="<%= lnRubriekID %>" />
               <jsp:param name="rnimageid" value="<%= rnImageID %>" />
               <jsp:param name="p" value="<%= paginaID %>" />
               <jsp:param name="a" value="<%= artikelID %>" />
               <jsp:param name="showpageintro" value="true" />
            </jsp:include>
      	</td>
       </tr>
     </table>
   </mm:node>
   <a name="bottom"></a>
   <%@include file="includes/footer.jsp" %>
   </body>
   <%@include file="../includes/sitestatscript.jsp" %>
   </html>
   <!-- /cache:cache -->
   <% 
} %>
</mm:cloud>
