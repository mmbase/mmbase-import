<% // *** list of vacatures, which link to vacature *** %>
<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<%@include file="includes/calendar.jsp" %>
<mm:locale language="nl"><% // used in <mm:time time=".." format="dd-MM-yyyy"/> %>
<br>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
   <td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;">
   <%@include file="includes/navleft.jsp" %>
   <br>
   <jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="sr" value="0" />
      <jsp:param name="tl" value="asis" />
   </jsp:include>
   </td>
   <% 
   String vacatureConstraint = "(vacature.embargo < '" + nowSec + "') AND (vacature.verloopdatum > '" + nowSec + "' ) AND (vacature.metatags LIKE '%vrijwilliger%')";

   if(vacatureID != null && !vacatureID.equals("-1")) {
     %>
       <td style="width:559px;vertical-align:top;padding:10px;padding-top:0px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
          <% 
        if (request.getParameter("p")!=null) { // link from intranet db
           String vacatureBronConstraint = "vacature.bron = '" + vacatureID + "'"; %>
           <mm:listnodes type="vacature" constraints="<%= vacatureBronConstraint %>">
             <mm:field name="number" jspvar="bron" vartype="String">
               <% vacatureID = bron; %>
             </mm:field>
           </mm:listnodes>
           <% 
         } %>
          <jsp:include page="includes/related_vrijwilliger_vacature.jsp">
             <jsp:param name="v" value="<%= vacatureID %>" />
          </jsp:include> 
       </td>      
     <%
   } else {
     %>   
       <td style="width:559px;vertical-align:top;padding:10px;padding-top:0px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
          <table width="539px;" cellspacing="0" cellpadding="0">
             <tr>
                <td style="vertical-align:top;width:364px;padding-right:10px">
               <%@include file="includes/page_intro.jsp" %>
               <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,vacature" fields="vacature.number,vacature.titel,vacature.embargo,vacature.omschrijving"
                      orderby="vacature.embargo" directions="down" constraints="<%= vacatureConstraint %>">
                 <mm:first>
                   <mm:import id="vacature_online" />
                   <table cellspacing="0" cellpadding="0">
                 </mm:first>
                 <tr>
                   <td style="vertical-align:top;width:65;">
                     <mm:field name="vacature.embargo" jspvar="vacature_embargo" vartype="String" write="false">
                       <mm:time time="<%=vacature_embargo%>" format="dd-MM-yyyy"/>
                     </mm:field>
                   </td>
                   <td style="vertical-align:top;width:3;">&nbsp;&nbsp;|&nbsp;&nbsp;</td>
                   <td style="vertical-align:top;">
                     <strong><a href="vrijwilliger_vacatures.jsp?id=<%= paginaID %>&v=<mm:field name="vacature.number"/>">
                       <mm:field name="vacature.titel"/></strong>
                     </a>
                   </td>
                 </tr>
                 <tr>
                   <td style="vertical-align:top;"></td>
                   <td style="vertical-align:top;width:3;"></td>
                   <td style="vertical-align:top;">
                     <mm:field name="vacature.omschrijving"/>
                   </td>
                 </tr>
                 <mm:last>
                   </table>
                 </mm:last>
               </mm:list>
               <mm:notpresent referid="vacature_online">
                      Er zijn op dit moment geen vacatures bij Natuurmonumenten.
               </mm:notpresent>
                </td>
                <td style="vertical-align:top;padding-left:10px;width:175px;">
                   <jsp:include page="includes/navright.jsp">
                      <jsp:param name="s" value="<%= paginaID %>" />
                      <jsp:param name="r" value="<%= rubriekID %>" />
                      <jsp:param name="lnr" value="<%= lnRubriekID %>" />
                   </jsp:include>
                   <jsp:include page="includes/shorty.jsp">
                      <jsp:param name="s" value="<%= paginaID %>" />
                      <jsp:param name="r" value="<%= rubriekID %>" />
                     <jsp:param name="rs" value="<%= styleSheet %>" />
                      <jsp:param name="sr" value="2" />
                   </jsp:include>
                </td>
             </tr>
          </table>
       </td>
     <% } %>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</mm:locale>
</cache:cache>
</mm:cloud>



