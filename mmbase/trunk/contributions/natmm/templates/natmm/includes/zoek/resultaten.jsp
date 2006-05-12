<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.*,
      org.mmbase.module.Module,
      org.mmbase.bridge.Cloud,
      org.mmbase.bridge.Node,
      org.mmbase.bridge.NodeList,
      nl.leocms.evenementen.Evenement,
      nl.leocms.util.PaginaHelper,
		net.sf.mmapps.modules.lucenesearch.LuceneModule,
      org.apache.lucene.index.IndexReader,
      org.apache.lucene.analysis.*,
      org.apache.lucene.search.*,
		net.sf.mmapps.modules.lucenesearch.util.*,
      org.apache.lucene.queryParser.QueryParser,
      org.apache.lucene.document.Document" %>
<%
String[] META_TAGS = {"dit", "is", "een", "test"};
%>
<mm:cloud jspvar="cloud">
   <mm:import jspvar="paginaID" externid="p">-1</mm:import>
   <%
   String rootID = request.getParameter("root");
   String sQuery = request.getParameter("query");
   String sMeta = request.getParameter("trefwoord");
   String sCategory = request.getParameter("categorie");
   if(sCategory==null) { sCategory = ""; }
   boolean categorieExists = false;
   %><mm:node number="<%=  sCategory %>" notfound="skipbody"
      ><mm:nodeinfo type="type" write="false" jspvar="nType" vartype="String"><%
         categorieExists = nType.equals("rubriek");
      %></mm:nodeinfo
   ></mm:node><%
   if(!categorieExists) { sCategory = ""; }

   HashSet hsetAllowedNodes = new HashSet();
   HashSet hsetPagesNodes = new HashSet();
   HashSet hsetCategories = new HashSet();

   HashSet hsetArticlesNodes = new HashSet();
   HashSet hsetArtDossierNodes = new HashSet();
   HashSet hsetNatuurgebiedenNodes = new HashSet();
   HashSet hsetFormulierNodes = new HashSet();
   HashSet hsetEvenementNodes = new HashSet();

   LuceneModule mod = (LuceneModule) Module.getModule("lucenemodule"); 
   if(mod!= null&&!sQuery.equals("")) {
      %><%@include file="hashsets.jsp" %><%
   }
   %>
   <a name="top" />
   <form action="">
      <input type="hidden" name="p" value="<%= paginaID %>">
      <mm:list nodes="<%= paginaID %>" path="pagina,posrel,rubriek" fields="rubriek.number">
         <input type="hidden" name="r" value="<mm:field name="rubriek.number" />">
      </mm:list>
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
         <input type="hidden" name="p" value="<%= paginaID %>">
         <tr>
            <td class="maincolor" style="width:177px;padding:5px;line-height:0.85em;">&nbsp;ZOEKTERM&nbsp;</td>
            <td class="maincolor" style="width:177px;padding:0px;padding-right:1px;vertical-align:top;"><input name="query_frm" type="text" value="<%= ( sQuery == null ? "" : sQuery ) %>" style="width:100%;border:0"></td>
         </tr>
         <tr>
            <td colspan="2" style="padding-left:80px;line-height:0.85em;height:7px;"></td>
         </tr>
         <tr>
            <td class="maincolor" style="width:177px;padding:5px;line-height:0.85em;">&nbsp;RUBRIEKEN&nbsp;</td>
            <td class="maincolor" style="width:177px;padding:0px;vertical-align:top;">
               <select name="categorie" style="width:100%;border:0;font-size:11px;" />
                  <option value="">ALLE RUBRIEKEN</option>
                  <mm:list nodes="<%= request.getParameter("root") %>" path="rubriek1,parent,rubriek2" orderby="parent.pos">
                     <mm:field name="rubriek2.number" jspvar="sCategoryNumber" vartype="String">
                     <mm:field name="rubriek2.naam" jspvar="sCategoryName" vartype="String">
                        <option value="<%= sCategoryNumber %>" <%
                           if((sCategory != null) && (sCategory.equals(sCategoryNumber))) { %> selected="selected" <% } 
                           %>><%= sCategoryName.toUpperCase() %></option>
                     </mm:field>
                     </mm:field>
                  </mm:list>
               </select></td>
         </tr>
         <tr>
            <td colspan="2" style="padding-left:80px;line-height:0.85em;height:7px;"></td>
         </tr>
         <%--
         <tr>
            <td style="padding-left:80px">&nbsp;</td>
            <td class="leftnavrubriek" style="font-weight:bold;">&nbsp;TREFWOORD&nbsp;</td>
            <td width="100%" class="leftnavrubriek" style="padding:1px">
               <select name="trefwoord" style="width:100%; height:18px">
                  <option></option><%
                        for (int f = 0; f < META_TAGS.length; f++)
                        {  %><option <% if((sMeta != null) && (sMeta.equals(META_TAGS[f]))) { %> selected="selected" <% } 
                              %>><%=META_TAGS[f]%></option><%
                        }
                     %>
               </select>
            </td>
         </tr>
         <tr>
            <td height="7" colspan="3"></td>
         </tr>
         --%>
         <tr>
            <td>
            </td>
            <td>
               <input type="submit" value="OPNIEUW ZOEKEN" class="submit_image" style="width:100%;"  />
            </td>
         </tr>
     </table>
   </form>
   <br/>
   <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table>
   <% if(hsetCategories.size()==0) {
      %>Er zijn geen zoekresultaten gevonden, die voldoen aan uw zoekcriteria.<%
   } else { 
      %> De volgene zoekresultaten zijn gevonden in de categorieën<% 
   }
      boolean bFirst = true;
      for (Iterator it = hsetCategories.iterator(); it.hasNext();)
      {
         String sRubriek = (String) it.next();
         if(!bFirst) { %> | <% }
         %><mm:node number="<%=sRubriek%>">
            <a href="zoek.jsp?<%= request.getQueryString() %>#<mm:field name="number" />"><b><mm:field name="naam"/></b></a>
         </mm:node><%
         bFirst = false;
      }
   %>
   <br/><br/>
   <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table>
   <%
   // *** Show rubrieken
   if (hsetCategories.size() > 0) {

      for (Iterator it = hsetCategories.iterator(); it.hasNext(); ) {
         String sRubriek = (String) it.next();

         HashSet hsetPagesForThisCategory = new HashSet(); %>
         <mm:node number="<%=sRubriek%>">
            <mm:relatednodes type="pagina">
               <mm:field name="number" jspvar="sID" vartype="String" write="false"><%
                     hsetPagesForThisCategory.add(sID);
               %></mm:field>
            </mm:relatednodes>
            <a name="<mm:field name="number" />" />
            <span class="colortitle"><mm:field name="naam"/></span>
            <br/><%
            
            bFirst = true;
            for (Iterator itp = hsetPagesNodes.iterator(); itp.hasNext(); ) {
               String sPageID = (String) itp.next();

               if(!hsetPagesForThisCategory.contains(sPageID)) {
                  continue;
               }

               String templateUrl = "index.jsp";

               %><mm:node number="<%=sPageID%>"><%
                  if (!bFirst) { %><br/><% } %>
                  <b><mm:field name="titel"/></b>
                  <ul style="margin:0px;margin-left:16px;">
                  <mm:related path="gebruikt,template">
                     <mm:field name="template.url" jspvar="dummy" vartype="String" write="false">
                        <% templateUrl = dummy; %>
                     </mm:field>
                  </mm:related>
                  <mm:related path="contentrel,artikel" fields="artikel.number">
                     <mm:field name="artikel.number" jspvar="sID" vartype="String" write="false"><%
                     if(hsetArticlesNodes.contains(sID)){
                        %><li><a href="<%= templateUrl %>?id=<mm:field name="artikel.number"/>"><mm:field name="artikel.titel"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related>
                  <mm:related path="posrel,dossier,posrel,artikel" fields="dossier.number,artikel.number">
                     <mm:field name="artikel.number" jspvar="sID" vartype="String" write="false"><%
                     if(hsetArtDossierNodes.contains(sID)){
                        %><li><a href="<%= templateUrl %>?d=<mm:field name="dossier.number"/>&id=<mm:field name="artikel.number"/>"><mm:field name="artikel.titel"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related>
                  <mm:related path="contentrel,provincies,pos4rel,natuurgebieden" fields="natuurgebieden.number">
                     <mm:field name="natuurgebieden.number" jspvar="sID" vartype="String" write="false"><%
                     if(hsetNatuurgebiedenNodes.contains(sID)){
                        String sAddUrl = "";
								if (templateUrl.equals("routes.jsp")){
									%><mm:list nodes="<%= sID %>" path="natuurgebieden,rolerel,artikel">
											<mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
												<% sAddUrl = "&a=" + artikel_number; %>
											</mm:field>
									</mm:list>
							<% } %>	
								<li><a href="<%= templateUrl %>?n=<mm:field name="natuurgebieden.number"/><%= sAddUrl %>"><mm:field name="natuurgebieden.naam"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related><% 
                  if(templateUrl.equals("events.jsp")) {
                     for(Iterator ite = hsetEvenementNodes.iterator(); ite.hasNext(); ) {
                        String thisEvent = (String) ite.next();
                        %><mm:node number="<%= thisEvent %>">
                           <li><a href="events.jsp?p=agenda&id=<%= Evenement.getNextOccurence(thisEvent) %>"><mm:field name="titel" /></a>
                         </mm:node><%
                     }
                  } %>
                  </ul>
               </mm:node><%
               bFirst = false;
            }
         %></mm:node>
         <div align="right"><a href="#top"><img src="media/arrowup_zoek.gif" border="0" /></a></div>
         <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table><%
      }
   }
%><br/>
</mm:cloud>
