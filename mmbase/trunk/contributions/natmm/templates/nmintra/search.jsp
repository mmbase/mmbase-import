<%@include file="includes/templateheader.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" 
%><%
String[] META_TAGS = {"dit", "is", "een", "test"};
%>
   <mm:import jspvar="paginaID" externid="p">-1</mm:import>
   <%
   String rootID = "home";
   String sQuery = request.getParameter("search");
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
   HashSet hsetTeaserNodes = new HashSet();
   HashSet hsetProducctypesNodes = new HashSet();
   HashSet hsetProductsNodes = new HashSet();
	HashSet hsetItemsNodes = new HashSet();
	HashSet hsetDocumentsNodes = new HashSet();
	HashSet hsetVacatureNodes = new HashSet();

   LuceneModule mod = (LuceneModule) Module.getModule("lucenemodule"); 

   if(mod!= null&&!sQuery.equals("")) {
      %><%@include file="includes/hashsets.jsp" %><%
   }
   %>
	<td><%@include file="includes/pagetitle.jsp" %></td>
	<td><% String rightBarTitle = "";
	    %><%@include file="includes/rightbartitle.jsp" 
	%></td>
	</tr>
	<tr>
	<td class="transperant">
	<div class="<%= infopageClass %>">
   <a name="top" />
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
                        %><li><a href="<%= templateUrl %>?p=<mm:field name="pagina.number"/>&article=<mm:field name="artikel.number"/>"><mm:field name="artikel.titel"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related>
                  <mm:related path="contentrel,teaser">
                     <mm:field name="teaser.number" jspvar="sID" vartype="String" write="false"><%
                     if(hsetTeaserNodes.contains(sID)){
                        %><li><a href="<%= templateUrl %>?p=<mm:field name="pagina.number"/>"><mm:field name="teaser.titel"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related>
						<mm:related path="posrel,producttypes" fields="producttypes.number">
                     <mm:field name="producttypes.number" jspvar="sID" vartype="String" write="false"><%
                     if(hsetProducctypesNodes.contains(sID)){
                        %><li><a href="<%= templateUrl %>?p=<mm:field name="pagina.number"/>&p=<mm:field name="producttypes.number"/>"><mm:field name="producttypes.title"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related>
						<mm:related path="posrel,producttypes,posrel,products">
                     <mm:field name="products.number" jspvar="sID" vartype="String" write="false"><%
                     if(hsetProductsNodes.contains(sID)){
                        %><li><a href="<%= templateUrl %>?p=<mm:field name="pagina.number"/>"><mm:field name="products.name"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related>
						<mm:related path="posrel,items">
                     <mm:field name="items.number" jspvar="sID" vartype="String" write="false"><%
                     if(hsetItemsNodes.contains(sID)){
                        %><li><a href="<%= templateUrl %>?p=<mm:field name="pagina.number"/>"><mm:field name="items.titel"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related>
						<mm:related path="posrel,documents">
                     <mm:field name="documents.number" jspvar="sID" vartype="String" write="false"><%
                     if(hsetDocumentsNodes.contains(sID)){
                        %><li><a href="<mm:field name="documents.url"/>"><mm:field name="documents.filename"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related>
						<mm:related path="contentrel,vacature">
                     <mm:field name="vacature.number" jspvar="sID" vartype="String" write="false"><%
                     if(hsetVacatureNodes.contains(sID)){
                        %><li><a href="<%= templateUrl %>?p=<mm:field name="pagina.number"/>"><mm:field name="vacature.titel"/></a></li><%
                     }
                     %></mm:field>
                  </mm:related>
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
</div>
</td>
<td><% 

// *********************************** right bar *******************************
%><img src="media/spacer.gif" width="10" height="1"></td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>