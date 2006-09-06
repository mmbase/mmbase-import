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
<mm:log jspvar="log">
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
	
	RubriekHelper rh = new RubriekHelper(cloud);
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
   HashSet hsetRubrieken = new HashSet();

   HashSet hsetPageDescrNodes = new HashSet();
   HashSet hsetArticlesNodes = new HashSet();
   HashSet hsetTeaserNodes = new HashSet();
   HashSet hsetProducttypesNodes = new HashSet();
   HashSet hsetProductsNodes = new HashSet();
	HashSet hsetItemsNodes = new HashSet();
	HashSet hsetDocumentsNodes = new HashSet();
	HashSet hsetVacatureNodes = new HashSet();
	HashSet hsetAttachmentsParagraafNodes = new HashSet();
	HashSet hsetAttachmentsContentblocksNodes = new HashSet();
	HashSet hsetAttachmentsItemsNodes = new HashSet();
	HashSet hsetAttachmentsVacaturesNodes = new HashSet();

   LuceneModule mod = (LuceneModule) Module.getModule("lucenemodule");
   if(mod!= null) {
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
	<div class="<%= infopageClass %>" id="infopage">
   <table border="0" cellpadding="0" cellspacing="0">
        <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
        <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
        <td><a name="top"/>
		  		<% if (searchId.equals("")&&sCategory.equals("")&&sPool.equals("")&&(fromTime==0)&&(toTime==0)){ %>
					Vul een zoekterm in bij 'ik zoek op...' en klik op de 'Zoek'-knop om in het Intranet te zoeken."						
				<% } else {
						if(hsetRubrieken.size()==0) {
				         %>Er zijn geen zoekresultaten gevonden, die voldoen aan uw zoekcriteria.<%
					   } else { 
					      %>De volgene zoekresultaten zijn gevonden in de categorieën <br/><% 
					   }
					   boolean bFirst = true;
					   for (Iterator it = hsetRubrieken.iterator(); it.hasNext();)
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
				   	if (hsetRubrieken.size() > 0) { 
							Vector defaultSearchTerms = new Vector(); 
							defaultSearchTerms = su.createSearchTerms(searchId);%>
						   <br/><br/>
						   <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table>
					      <% 
   	               for (Iterator it = hsetRubrieken.iterator(); it.hasNext(); ) {
					         String sRubriek = (String) it.next();
                        log.info(sRubriek);
					         HashSet hsetPagesForThisRubriek = rh.getAllPages(sRubriek); 
					         log.info(" -> " + hsetPagesForThisRubriek);
					         %>
					         <mm:node number="<%= sRubriek %>">
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

				            	   if(!hsetPagesForThisRubriek.contains(sPageID)) {
            				   	   continue;
					               }

   	         				   String templateUrl = "index.jsp";
										String textStr = "";
										String titleStr = "";
									   String showExpireDate = "1";
									   boolean bHasAttachments = false;
				   	            %><mm:node number="<%=sPageID%>"><%
            					      if (!bFirst) { %><br/><% } %>
            					      <mm:related path="gebruikt,template">
            				      	   <mm:field name="template.url" jspvar="url" vartype="String" write="false">
                        					<% templateUrl = url; %>
					                     </mm:field>
   	         				      </mm:related>
   	         				      <%
         	   				      if(hsetPageDescrNodes.contains(sPageID)){
            	            		   %>
            	            		   <mm:field name="titel" jspvar="titel" vartype="String" write="false">
														<li><a href="<%= templateUrl %>?p=<%=sPageID%>">
													   <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>	
												<mm:field name="omschrijving" jspvar="dummy" vartype="String" write="false">
												  <% textStr = dummy; %>
												</mm:field>
												<br/><%= su.highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
                                 	<% 
	                              } else {
	                                 %>
				         	            <b><mm:field name="titel"/></b>
				         	            <%
				         	         } %>
            					      <ul style="margin:0px;margin-left:16px;">
				               	   <%
   	         				      String [] articlePathToPage = 
   	         				         { "contentrel,artikel", "readmore,artikel", "posrel,images,pos4rel,artikel" };      
   	         				      for(int i = 0; i<3; i++) {
      	         				      %>
   					                  <mm:related path="<%= articlePathToPage[i] %>" fields="artikel.number">
            	   				         <mm:field name="artikel.number" jspvar="sID" vartype="String" write="false">
   													<mm:list nodes="<%= sID %>" path="artikel,posrel,paragraaf,posrel,attachments" fields="attachments.number,attachments.filename,attachments.titel">
   														<mm:field name="attachments.number" jspvar="sAttID" vartype="String" write="false">
   															<% 
   															if (hsetAttachmentsParagraafNodes.contains(sAttID)) { 
   																bHasAttachments = true;
   																%>
   															   <%@include file="includes/search/show_attachments.jsp" %>															
   															   <%	
   															} %>
   														</mm:field>
   													</mm:list>
   												<%
   					                     if(hsetArticlesNodes.contains(sID)||bHasAttachments){
               					            %>
   													<mm:node element="artikel" id="this_article">
   														<mm:field name="titel" jspvar="titel" vartype="String" write="false">
   															<%@include file="includes/highlightsshow.jsp" %>
   														<% String highlightSearchTerms = su.highlightSearchTerms(textStr,defaultSearchTerms,"b");
   															if (!highlightSearchTerms.trim().equals("")) {
   																highlightSearchTerms += "<br/>";
   															}
   															titleStr = titel;
   															if (bHasAttachments) {%>
   																<%@include file="includes/poolanddate.jsp" %>
   																<%= highlightSearchTerms %>
   																<a href="<%= templateUrl %>?p=<%=sPageID%>&article=<%= sID %>">
   																<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a><br/>
   														<% } else {%>
   																<li><a href="<%= templateUrl %>?p=<%=sPageID%>&article=<%= sID %>">
   															   <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li><br/>
   																<%@include file="includes/poolanddate.jsp" %>
   																<%= highlightSearchTerms %>
   														<% } %><br/>	
   														</mm:field>
   													</mm:node>
   													<mm:remove referid="this_article" />
                           	            <% 
                              	      }
   												bHasAttachments = false;
               				      	   %>
   												</mm:field>
   					                  </mm:related>
   					                  <%
   					               } %>
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
            					         if(hsetProducttypesNodes.contains(sID)){
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
				   	                  <mm:field name="items.number" jspvar="sID" vartype="String" write="false">
													<mm:list nodes="<%= sID %>" path="items,posrel,attachments" fields="attachments.number,attachments.filename,attachments.titel">
														<mm:field name="attachments.number" jspvar="sAttID" vartype="String" write="false">
															<% if (hsetAttachmentsItemsNodes.contains(sAttID)) { 
																bHasAttachments = true; %>
															<%@include file="includes/search/show_attachments.jsp" %>
															<%	}%>
														</mm:field>
													</mm:list><%
            					         if(hsetItemsNodes.contains(sID)||bHasAttachments){
                     	   				%><mm:field name="items.intro" jspvar="dummy" vartype="String" write="false">
 													  <% textStr = dummy; %>
													</mm:field>
													<mm:field name="items.body" jspvar="dummy" vartype="String" write="false">
 													  <% if (!textStr.equals("")) { textStr += " "; }
														  textStr += dummy; %>
													</mm:field>
													<mm:field name="items.titel" jspvar="titel" vartype="String" write="false">
													<% String highlightSearchTerms = su.highlightSearchTerms(textStr,defaultSearchTerms,"b");
														if (!highlightSearchTerms.trim().equals("")){
															highlightSearchTerms += "<br/>";
														}
														if (bHasAttachments) {%>
															<%= highlightSearchTerms %>
															<a href="<%= templateUrl %>?p=<%=sPageID%>&u=<mm:field name="items.number"/>">
															<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a><br/>
													<% } else {%>	
															<li><a href="<%= templateUrl %>?p=<%=sPageID%>&u=<mm:field name="items.number"/>">
															<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li><br/>
															<%= highlightSearchTerms %>
													<% } %><br/>
													</mm:field>	
                  	                  <% 
                     	            }
												bHasAttachments = false;
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
					                     <mm:field name="vacature.number" jspvar="sID" vartype="String" write="false">
													<mm:list nodes="<%= sID %>" path="vacature,posrel,attachments" fields="attachments.number,attachments.filename,attachments.titel">
														<mm:field name="attachments.number" jspvar="sAttID" vartype="String" write="false">
															<% if (hsetAttachmentsVacaturesNodes.contains(sAttID)) { 
																bHasAttachments = true; %>
															<%@include file="includes/search/show_attachments.jsp" %>
															<%	}%>
														</mm:field>
													</mm:list>
   	         				        <%  if(hsetVacatureNodes.contains(sID)||bHasAttachments){
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
													<mm:field name="vacature.titel" jspvar="titel" vartype="String" write="false">
														<% String highlightSearchTerms = su.highlightSearchTerms(textStr,defaultSearchTerms,"b");
															if (!highlightSearchTerms.trim().equals("")){
																highlightSearchTerms += "<br/>";
															}
															if (bHasAttachments) {%>
																<%= highlightSearchTerms %>
																<a href="<%= templateUrl %>?p=<%=sPageID%>&project=<mm:field name="vacature.number"/>">
																<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a><br/>
														<% } else {%>
														<li><a href="<%= templateUrl %>?p=<%=sPageID%>&project=<mm:field name="vacature.number"/>">
															<span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li><br/>
															<%= highlightSearchTerms %>
														<% } %><br/>	
													</mm:field>	
            	                        <% 
               	                  }
												bHasAttachments = false;
				      	               %></mm:field>
            					      </mm:related>
											<mm:related path="readmore,contentblocks,readmore,attachments">
												<mm:field name="attachments.number" jspvar="sAttID" vartype="String" write="false">
													<% if (hsetAttachmentsContentblocksNodes.contains(sAttID)) { 
														bHasAttachments = true; %>
														<%@include file="includes/search/show_attachments.jsp" %>
													<%	}%>
												</mm:field>
											</mm:related>
											<% if (bHasAttachments) { %>
												<a href="<%= templateUrl %>?p=<%=sPageID%>">
												<span class="normal" style="text-decoration:underline;"><mm:field name="titel"/></span></a><br/>
											<% } 
												bHasAttachments = false; %>	
				            	      </ul>
            				   	</mm:node><%
					               bFirst = false;
   	         				}
					         %></mm:node>
					         <div align="right"><a href="#top"><img src="media/<%= NMIntraConfig.style1[iRubriekStyle] %>_up.gif" border="0" /></a></div>
					         <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table><%
				   	   }
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
</mm:log>
</mm:cloud>