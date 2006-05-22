<%@page import="
   org.mmbase.module.Module,
	net.sf.mmapps.modules.lucenesearch.LuceneModule,
	net.sf.mmapps.modules.lucenesearch.util.*,
	org.apache.lucene.index.IndexReader,
	org.apache.lucene.analysis.*,
   org.apache.lucene.search.*,
	org.apache.lucene.queryParser.QueryParser,
	org.apache.lucene.document.Document,
   nl.leocms.util.tools.SearchUtil" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<%@include file="includes/calendar.jsp" %>
<% 
   
   String sQuery = request.getParameter("search");
	if(sQuery==null) { sQuery = ""; }
   String sMeta = request.getParameter("trefwoord");
   String sCategory = request.getParameter("categorie");
	if(sCategory==null) { sCategory = ""; }
	String sPool = request.getParameter("pool");
	if (sPool==null) { sPool = ""; }
	String sArchive = request.getParameter("archive");
	if (sArchive==null) { sArchive = "nee"; }
	String sAdv = request.getParameter("adv");
	if (sAdv==null) { sAdv = ""; }
	
   SearchUtil su = new SearchUtil();
   
   long [] period = su.getPeriod(periodId);
   long fromTime = period[0];
   long toTime = period[1];
   int fromDay = (int) period[2]; int fromMonth = (int) period[3]; int fromYear = (int) period[4];
   int toDay = (int) period[5]; int toMonth = (int) period[6]; int toYear = (int) period[7];
   int thisYear = (int) period[10];
   int startYear = (int) period[11];
   
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
      %><%@include file="includes/search/hashsets.jsp" %><%
   }
   %>
      <%@include file="includes/header.jsp" %>
      <td><%@include file="includes/pagetitle.jsp" %></td>
      <td><% 
         String rightBarTitle = "Uitgebreid Zoeken";
         if(actionId.equals("adv_search")) {
            %><%@include file="includes/rightbartitle.jsp" %><%
         } 
         %>
      </td>
	</tr>
	<tr>
	<td class="transperant">
	<div class="<%= infopageClass %>">
   <table border="0" cellpadding="0" cellspacing="0">
        <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
        <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
        <td><a name="top"/>
		  		<% if(hsetCategories.size()==0) {
			      %>Er zijn geen zoekresultaten gevonden, die voldoen aan uw zoekcriteria.<%
				   } else { 
				      %> De volgene zoekresultaten zijn gevonden in de categorieën <br/><% 
				   }
				   boolean bFirst = true;
				   for (Iterator it = hsetCategories.iterator(); it.hasNext();)
				   {
				      String sRubriek = (String) it.next();
				      if(!bFirst) { %> | <% }
				      %><mm:node number="<%=sRubriek%>">
							  <mm:field name="naam" jspvar="name" vartype="String" write="false">
						         <a href="search.jsp?<%= request.getQueryString() %>#<mm:field name="number" />">
										<span class="colortitle" style="text-decoration:underline;"><%= name.toUpperCase() %></span></a>
							  </mm:field>	
			   	     </mm:node><%
				        bFirst = false;
				   }

				   // *** Show rubrieken
				   if (hsetCategories.size() > 0) { 
						Vector defaultSearchTerms = new Vector(); 
						defaultSearchTerms = su.createSearchTerms(searchId);%>
					   <br/><br/>
					   <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table>
				      <% 
                  for (Iterator it = hsetCategories.iterator(); it.hasNext(); ) {
				         String sRubriek = (String) it.next();

				         HashSet hsetPagesForThisCategory = new HashSet(); %>
				         <mm:node number="<%= sRubriek %>">
				            <mm:relatednodes type="pagina">
				               <mm:field name="number" jspvar="sID" vartype="String" write="false"><%
				                     hsetPagesForThisCategory.add(sID);
				               %></mm:field>
				            </mm:relatednodes>
				            <a name="<mm:field name="number" />" />
				            <span class="colortitle"><b>
									<mm:field name="naam" jspvar="name" vartype="String" write="false">
										<%= name.toUpperCase() %>
									</mm:field></b>
								</span>
				            <br/><%

				            bFirst = true;
				            for (Iterator itp = hsetPagesNodes.iterator(); itp.hasNext(); ) {
            				   String sPageID = (String) itp.next();

				               if(!hsetPagesForThisCategory.contains(sPageID)) {
            				      continue;
				               }

            				   String templateUrl = "index.jsp";
									String textStr = "";
									String titleStr = "";
								   String showExpireDate = "1";
									
				               %><mm:node number="<%=sPageID%>"><%
            				      if (!bFirst) { %><br/><% } %>
				                  <b><mm:field name="titel"/></b>
            				      <ul style="margin:0px;margin-left:16px;">
				                  <mm:related path="gebruikt,template">
            				         <mm:field name="template.url" jspvar="url" vartype="String" write="false">
                        				<% templateUrl = url; %>
				                     </mm:field>
            				      </mm:related>
				                  <mm:related path="contentrel,artikel" fields="artikel.number">
            				         <mm:field name="artikel.number" jspvar="sID" vartype="String" write="false"><%
				                     if(hsetArticlesNodes.contains(sID)){
            				            %>
												<mm:node element="artikel" id="this_article">
													<mm:field name="titel" jspvar="titel" vartype="String" write="false">
														<% titleStr = titel; %>
														<li><a href="<%= templateUrl %>?p=<%=sPageID%>&article=<%= sID %>">
															<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
													</mm:field>
													<mm:field name="intro" jspvar="dummy" vartype="String" write="false">
													  <% textStr = dummy; %>
													</mm:field>
													<mm:field name="tekst" jspvar="dummy" vartype="String" write="false">
													  <% if (!textStr.equals("")) { textStr += " "; }
														  textStr += dummy; %>
													</mm:field>
													<mm:related path="posrel,paragraaf">
														<mm:field name="paragraaf.titel_zichtbaar" jspvar="titel_zichtbaar" vartype="String" write="false">
															<% if ((titel_zichtbaar==null||!titel_zichtbaar.equals("0"))) { %>
																<mm:field name="paragraaf.titel" jspvar="dummy" vartype="String" write="false">
																<% if (!textStr.equals("")) { textStr += " "; }
																  textStr += dummy; %>
																</mm:field>
															<% }%>
														</mm:field>
														<mm:field name="paragraaf.tekst" jspvar="dummy" vartype="String" write="false">
	 													  <% if (!textStr.equals("")) { textStr += " "; }
															  textStr += dummy; %>
														</mm:field>
													</mm:related><br/>
													<%@include file="includes/poolanddate.jsp" %>
												</mm:node>
												<mm:remove referid="this_article" />
												<%= su.highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
                                    <% 
                                 }
            				         %></mm:field>
				                  </mm:related>
            				      <mm:related path="contentrel,teaser">
				                     <mm:field name="teaser.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetTeaserNodes.contains(sID)){
                        				%><mm:field name="teaser.titel" jspvar="titel" vartype="String" write="false">
														<li><a href="<%= templateUrl %>?p=<%=sPageID%>">
															<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>	
												<mm:field name="teaser.omschrijving" jspvar="dummy" vartype="String" write="false">
 												  <% textStr = dummy; %>
												</mm:field>
												<br/><%= su.highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
                                    <% 
                                 }
            				         %></mm:field>
				                  </mm:related>
										<mm:related path="posrel,producttypes" fields="producttypes.number">
				                     <mm:field name="producttypes.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetProducctypesNodes.contains(sID)){
				                        %><mm:field name="producttypes.titel" jspvar="titel" vartype="String" write="false">
													<li><a href="<%= templateUrl %>?p=<%=sPageID%>&pool=<mm:field name="producttypes.number"/>">
														<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>	
                                    <%	
                                 }
				                     %></mm:field>
            				      </mm:related>
										<mm:related path="posrel,producttypes,posrel,products">
            				         <mm:field name="products.number" jspvar="sID" vartype="String" write="false"><%
				                     if(hsetProductsNodes.contains(sID)){
            				            %><mm:field name="products.titel" jspvar="titel" vartype="String" write="false">
														<li><a href="<%= templateUrl %>?p=<%=sPageID%>&pool=<mm:field name="producttypes.number"/>&product=<mm:field name="products.number"/>">
															<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>	
												<mm:field name="products.omschrijving" jspvar="dummy" vartype="String" write="false">
 												  <% textStr = dummy; %>
												</mm:field>
												<br/><%= su.highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
                                    <% 
                                 }
            				         %></mm:field>
				                  </mm:related>
										<mm:related path="posrel,items">
				                     <mm:field name="items.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetItemsNodes.contains(sID)){
                        				%><mm:field name="items.titel" jspvar="titel" vartype="String" write="false">
													<li><a href="<%= templateUrl %>?p=<%=sPageID%>&u=<mm:field name="items.number"/>">
													<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>	
												<mm:field name="items.intro" jspvar="dummy" vartype="String" write="false">
 												  <% textStr = dummy; %>
												</mm:field>
												<mm:field name="items.body" jspvar="dummy" vartype="String" write="false">
 												  <% if (!textStr.equals("")) { textStr += " "; }
													  textStr += dummy; %>
												</mm:field>
												<br/><%= su.highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
                                    <% 
                                 }
            				         %></mm:field>
				                  </mm:related>
										<mm:related path="posrel,documents">
				                     <mm:field name="documents.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetDocumentsNodes.contains(sID)){
                                    %><mm:field name="documents.filename" jspvar="titel" vartype="String" write="false">
                                       <li><a href="<mm:field name="documents.url"/>">
                                          <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
                                    </mm:field>		
                                    <%
                                 }
            				         %></mm:field>
				                  </mm:related>
										<mm:related path="contentrel,vacature">
				                     <mm:field name="vacature.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetVacatureNodes.contains(sID)){
                        				%><mm:field name="vacature.titel" jspvar="titel" vartype="String" write="false">
													<li><a href="<%= templateUrl %>?p=<%=sPageID%>&project=<mm:field name="vacature.number"/>">
														<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>	
												<% 
                                    LinkedList ll = new LinkedList();
                                    ll.add("vacature.functienaam"); 
                                    ll.add("vacature.omschrijving");	
                                    ll.add("vacature.functieinhoud"); 
                                    ll.add("vacature.functieomvang"); 
                                    ll.add("vacature.duur"); 
                                    ll.add("vacature.afdeling"); 
                                    ll.add("vacature.functieeisen"); 
                                    ll.add("vacature.opleidingseisen"); 
                                    ll.add("vacature.competenties"); 
                                    ll.add("vacature.salarisschaal");
                                    ll.add("vacature.metatags");  
                                    Iterator itl = ll.iterator();
                                    textStr = "";
                                    while (itl.hasNext()){ %>
                                       <mm:field name="<%= (String)itl.next() %>" jspvar="dummy1" vartype="String" write="false">
                                         <% if (dummy1!=null) {
                                               if (!textStr.equals("")) { textStr += " "; }
                                               textStr += dummy1; 
                                            } %>
                                       </mm:field>
                                       <%	
                                    } %>
												<br/><%= su.highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
                                    <% 
                                 }
				                     %></mm:field>
            				      </mm:related>
				                  </ul>
            				   </mm:node><%
				               bFirst = false;
            				}
				         %></mm:node>
				         <div align="right"><a href="#top"><img src="media/<%= NMIntraConfig.style1[iRubriekStyle] %>_up.gif" border="0" /></a></div>
				         <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table><%
				      }
				   }
				%><br/>
				</td>
	        <td><img src="media/spacer.gif" width="10" height="1"></td>
   	   </tr>
      </table>
</div>
</td>
<td><% 

// *********************************** right bar *******************************
	if(actionId.equals("adv_search")) { 
		%><%@include file="includes/search/form.jsp"
		%><%@include file="includes/whiteline.jsp" 
   	%><% } 
	%></td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>