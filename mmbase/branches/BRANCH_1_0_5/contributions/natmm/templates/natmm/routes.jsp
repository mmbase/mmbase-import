<%@page import="nl.leocms.evenementen.forms.SubscribeForm" %>
<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<mm:import jspvar="actie" externid="actie">show</mm:import>
<% if(actie.equals("zoek")){ expireTime = 0; } %>
<!-- cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" -->
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<%@include file="/editors/mailer/util/memberid_get.jsp" %>
<br>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
   <td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
      <%@include file="includes/navleft.jsp" %>
      <br>
      <jsp:include page="includes/teaser.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="0" />
      </jsp:include>
   </td><%
   // October 2, 2006: routes available for all
   if(false && memberid==null) {

      %><%@include file="includes/checkmembership.jsp" %><%

   } else { 

      %>
      <%@include file="/editors/mailer/util/memberid_set.jsp" %>
      <mm:import jspvar="rt_w" externid="rt_Wandelroute" id="rt_w"></mm:import>
      <mm:import jspvar="rt_f" externid="rt_Fietsroute" id="rt_f"></mm:import>
      <mm:import jspvar="rt_k" externid="rt_Kanoroute" id="rt_k"></mm:import>
      <mm:import jspvar="rl_1" externid="rl_1" id="rl_1"></mm:import>
      <mm:import jspvar="rl_2" externid="rl_2" id="rl_2"></mm:import>
      <mm:import jspvar="rl_3" externid="rl_3" id="rl_3"></mm:import>
      <mm:import jspvar="rl_4" externid="rl_4" id="rl_4"></mm:import>
      <mm:import jspvar="rl_5" externid="rl_5" id="rl_5"></mm:import>
      <mm:import jspvar="routeLengte" externid="rl">-1</mm:import>
      <td style="vertical-align:top;width:374px;padding:10px;padding-top:0px;">
      <% 
      if(!artikelID.equals("-1")) { 
         // *** this can be reached from a shorty or teaser *** 
         %><jsp:include page="includes/artikel_12_column.jsp">
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="lnr" value="<%= lnRubriekID %>" />
            <jsp:param name="rnimageid" value="<%= rnImageID %>" />
            <jsp:param name="p" value="<%= paginaID %>" />
            <jsp:param name="a" value="<%= artikelID %>" />
            <jsp:param name="showdate" value="true" />
         </jsp:include><%
      } else { 
         %>
         <%@include file="includes/page_intro.jsp" %>
         <jsp:include page="includes/shorty.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sr" value="1" />
         </jsp:include>
         <% 
         boolean foundRoute = false;
         String searchConVar = "";
         String searchPath = "provincies,pos4rel,natuurgebieden,rolerel,artikel";
         if(actie.equals("zoek")){
            // *** first take care that no choice produces no result, than construct the query
            // *** metatag contains the length of the route
            searchConVar = "(artikel.type ='0'"; 
            %>
            <mm:present referid="rt_w"><%searchConVar += " OR artikel.type ='Wandelroute'"; %></mm:present>
            <mm:present referid="rt_f"><%searchConVar += " OR artikel.type ='Fietsroute'"; %></mm:present>
            <mm:present referid="rt_k"><%searchConVar += " OR artikel.type ='Kanoroute'"; %></mm:present>
            <%searchConVar += ") AND (artikel.metatags = '0'";  %>
            <mm:present referid="rl_1"><%searchConVar += " OR artikel.titel_fra LIKE '%1;%'"; %></mm:present>
            <mm:present referid="rl_2"><%searchConVar += " OR artikel.titel_fra LIKE '%2;%'"; %></mm:present>
            <mm:present referid="rl_3"><%searchConVar += " OR artikel.titel_fra LIKE '%3;%'"; %></mm:present>
            <mm:present referid="rl_4"><%searchConVar += " OR artikel.titel_fra LIKE '%4;%'"; %></mm:present>           
            <mm:present referid="rl_5"><%searchConVar += " OR artikel.titel_fra LIKE '%5;%'"; %></mm:present>
            <% searchConVar += ")";
         } else if(!natuurgebiedID.equals("-1")) {
            // This is to catch the jumps from natuurgebieden.jsp and zoek.jsp.
            searchConVar = "natuurgebieden.number='" + natuurgebiedID + "'";
            searchPath = "natuurgebieden,rolerel,artikel";
         }
         if(!searchConVar.equals("")) {
            searchConVar += " AND ";
         }
         searchConVar += (new SearchUtil()).articleConstraint(nowSec, quarterOfAnHour);
         %>
         <table width="100%">
            <mm:list nodes="<%= provID %>" 
                  path="<%= searchPath %>"
                  distinct="true" 
                  fields="artikel.titel,artikel.number,artikel.type"
                  constraints="<%= searchConVar %>"
                  orderby="artikel.status">
                  <tr>
                     <td style="vertical-align:top;white-space: nowrap;">
                        <mm:field name="artikel.status"/>&nbsp;|
                        <mm:field name="artikel.titel_eng" jspvar="weidegang" vartype="String">
                           <% if (weidegang != null && weidegang.equals("1")) { %>
                              <img src="media/logos/weidemelk.jpg" width="40px" style="vertical-align:top;"/>
                           <% }  %>
                        </mm:field>
                      </td> 
                      <td style="vertical-align:top;">
                        <a class="maincolor_link" href="javascript:void(0);" onclick="javascript:launchCenter('/natmm-internet/natmm/route_pop.jsp?a=<mm:field name="artikel.number"
                           />&rs=<%=styleSheet%>', 'route', 600, 800,'location=no,directories=no,status=no,toolbars=no,scrollbars=yes,resizable=yes');setTimeout('newwin.focus();',250);">
                           <mm:field name="artikel.titel"/></a></td>
                      <td style="vertical-align:top;">|&nbsp;<mm:field name="artikel.type" /></td></tr>
                  <tr><td colspan="3"><table class="dotline"><tr><td height="3"></td></tr></table></td></tr>
                  <% foundRoute =true; %>
            </mm:list>
            <% if(!foundRoute){%>
               <tr><td>Er is helaas geen route gevonden met de door u opgegeven criteria. <br>
               Probeert u het eens met een ruimer zoekprofiel.</td></tr>
            <% } %>
         </table>
         <br><br>
      <% } %>
      </td>
      <td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
      <jsp:include page="includes/navright.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="lnr" value="<%= lnRubriekID %>" />
      </jsp:include>
      <form action="routes.jsp" method="post" name="route" id="route">
         <input type="hidden" name="p" value="<%=paginaID%>">
         <input type="hidden" name="actie" value="zoek">
         <span class="colortitle">Soort route</span><br>
            &nbsp;<input type="checkbox" name="rt_Wandelroute" value="Wandelroute" <% if((!actie.equals("zoek")) || (rt_w != null)){%>checked<% } %>> Wandelroute <br>
            &nbsp;<input type="checkbox" name="rt_Fietsroute" value="Fietsroute" <% if((!actie.equals("zoek")) || (rt_f != null)){%>checked<% } %>> Fietsroute <br>
            &nbsp;<input type="checkbox" name="rt_Kanoroute" value="Kanoroute" <% if((!actie.equals("zoek")) || (rt_k != null)){%>checked<% } %>> Kanoroute <br>
         <br>
         <span class="colortitle">Afstand in kilometers</span><br>
            &nbsp;<input type="checkbox" name="rl_1" value="1" <% if((!actie.equals("zoek")) || (rl_1 != null)){%>checked<% } %>> 0-5 <br>
            &nbsp;<input type="checkbox" name="rl_2" value="2" <% if(rl_2 != null){%>checked<% } %>> 5-10 <br>
            &nbsp;<input type="checkbox" name="rl_3" value="3" <% if(rl_3 != null){%>checked<% } %>> 10-15 <br>
            &nbsp;<input type="checkbox" name="rl_4" value="4" <% if(rl_4 != null){%>checked<% } %>> 15-20 <br>
            &nbsp;<input type="checkbox" name="rl_5" value="5" <% if(rl_5 != null){%>checked<% } %>> 20 en meer <br>
            <br>
         </div>
         <table border="0" cellspacing="0" cellpadding="0">
         <tr>
            <td align="center" valign="top">
               <span class="colortitle">Klik op een provincie:</span><BR>
               <table border="0" cellspacing="3" cellpadding="0">
                  <tr><td height="3" colspan="3" background="media/dotline.gif"></td></tr>
                  <tr>
                     <td width="1" background="media/v_dotline.gif"></td>
                     <td width="115" height="134" valign="top">
                        <% // in some situations the map gives a js error, maybe it assumes to be in the root or something like this %>
                        <jsp:include page="includes/natuurgebieden/map.jsp" flush="true">
                           <jsp:param name="prov" value="<%=provID%>"/>
                           <jsp:param name="mapAction" value="form"/>
                        </jsp:include >
                     </td>
                     <td width="1" background="media/v_dotline.gif"></td>
                  </tr>
                  <tr><td height="3" colspan="3" background="media/dotline.gif"></td></tr>
               </table>
            </td>
         </tr>
         </table>
         <br>
         <span class="colortitle">Kies een provincie</span><br>
         &nbsp;<select name="prov" style="width:130px" onchange="selectLayer(this.value)">
            <option value="-1">Alle provincies</option>
            <% 
            String [] provAlias = { "prov_gr",  "prov_dr", "prov_fr",   "prov_nh",       "prov_fl",   "prov_ov",    "prov_ge",    "prov_ut", "prov_nb",       "prov_zu",      "prov_li", "prov_ze" };
            String [] provName = { "Groningen", "Drenthe", "Friesland", "Noord-Holland", "Flevoland", "Overijssel", "Gelderland", "Utrecht", "Noord-Brabant", "Zuid-Holland", "Limburg", "Zeeland" };
            for(int i=0; i<provAlias.length; i++) {
               %>
               <option <%= (provID.equals(provAlias[i]) ? "selected": "") %> value="<%= provAlias[i] %>"><%= provName[i] %></option>
               <%
            }
            %>            
         </select><br><br>
         <div align="right"><input type="submit" value="ZOEK ROUTE" class="submit_image" style="width:130;"  /></div>
      </form>
      </td><%
   } %>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
<!-- /cache:cache --> 
</mm:cloud>



