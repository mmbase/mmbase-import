<%@page import="org.mmbase.util.logging.*" %>
<%@include file="/taglibs.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/templateheader.jsp" 
%><%@include file="includes/calendar.jsp" 
%><%@include file="includes/searchfunctions.jsp" 
%><mm:import jspvar="paginaID" externid="p">-1</mm:import>
   <% 

   //String rootID = "home";
   String sQuery = request.getParameter("search");
	if(sQuery==null) { sQuery = ""; }
   String sMeta = request.getParameter("trefwoord");
   String sCategory = request.getParameter("categorie");
	if(sCategory==null) { sCategory = ""; }
	String sPool = request.getParameter("pool");
	if (sPool==null) {sPool = ""; }
	String sArchieve = request.getParameter("archive");
	String sAdv = request.getParameter("adv");
	if (sAdv==null) {sAdv = ""; }
	if (sArchieve==null) {sArchieve = "nee";}
	int fromDay = 0; int fromMonth = 0; int fromYear = 0;
   int toDay = 0; int toMonth = 0; int toYear = 0;
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);
   int thisMonth = cal.get(Calendar.MONTH)+1;
   int thisYear = cal.get(Calendar.YEAR);
   int startYear = 2004;
   long fromTime = 0;
   long toTime = 0;
	boolean checkOnPeriod = false;
	boolean periodExceedsMonth = true;
	
	if(!periodId.equals("")) {
          try{
              fromDay = new Integer(periodId.substring(0,2)).intValue(); 
              fromMonth = new Integer(periodId.substring(2,4)).intValue();
              fromYear = new Integer(periodId.substring(4,8)).intValue();
      
              toDay = new Integer(periodId.substring(8,10)).intValue();
              toMonth = new Integer(periodId.substring(10,12)).intValue();
              toYear = new Integer(periodId.substring(12)).intValue();
              if((fromDay+fromMonth+fromYear+toDay+toMonth+toYear)>0)
              {   // if not set use defaults for day, month and year
                  if(fromDay==0) fromDay = 1;
                  if(fromMonth==0) fromMonth = 1;
                  if(fromYear==0) fromYear = startYear; 
                  if(toDay==0) toDay = thisDay;
                  if(toMonth==0) toMonth = thisMonth;
                  if(toYear==0) toYear = thisYear;
      
                  cal.set(fromYear,fromMonth-1,fromDay,0,0,0);
                  fromTime = (cal.getTime().getTime()/1000);
      
                  cal.set(toYear,toMonth-1,toDay,23,60,0);
                  toTime = (cal.getTime().getTime()/1000);    
                  checkOnPeriod = (fromTime<=toTime);
                  periodExceedsMonth = toTime > (fromTime + 31*24*3600);
              }
          } catch (Exception e) { }
      }
   
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
   <%@include file="includes/header.jsp" %>
	<td><%@include file="includes/pagetitle.jsp" %></td>
	<td><% String rightBarTitle = "Uitgebreid Zoeken";
	    if(actionId.equals("adv_search")) { %><%@include file="includes/rightbartitle.jsp" 
	%><% } %></td>
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
						defaultSearchTerms = createSearchTerms(searchId);%>
					   <br/><br/>
					   <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table>
				   <% for (Iterator it = hsetCategories.iterator(); it.hasNext(); ) {
				         String sRubriek = (String) it.next();

				         HashSet hsetPagesForThisCategory = new HashSet(); %>
				         <mm:node number="<%=sRubriek%>">
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
            				            %><mm:field name="artikel.titel" jspvar="titel" vartype="String" write="false">
													<% titleStr = titel; %>
													<li><a href="<%= templateUrl %>?p=<%=sPageID%>&article=<mm:field name="artikel.number"/>">
														<span class="normal" style="text-decoration:underline;"><%= highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>
												<mm:field name="artikel.intro" jspvar="dummy" vartype="String" write="false">
 												  <% textStr = dummy; %>
												</mm:field>
												<mm:field name="artikel.tekst" jspvar="dummy" vartype="String" write="false">
 												  <% if (!textStr.equals("")) { textStr += " "; }
													  textStr += dummy; %>
												</mm:field>
												<mm:node element="artikel">
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
													</mm:related>
												</mm:node>
												<br/><%= highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
										<% }
            				         %></mm:field>
				                  </mm:related>
            				      <mm:related path="contentrel,teaser">
				                     <mm:field name="teaser.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetTeaserNodes.contains(sID)){
                        				%><mm:field name="teaser.titel" jspvar="titel" vartype="String" write="false">
														<li><a href="<%= templateUrl %>?p=<%=sPageID%>">
															<span class="normal" style="text-decoration:underline;"><%= highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>	
												<mm:field name="teaser.omschrijving" jspvar="dummy" vartype="String" write="false">
 												  <% textStr = dummy; %>
												</mm:field>
												<br/><%= highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
										<% }
            				         %></mm:field>
				                  </mm:related>
										<mm:related path="posrel,producttypes" fields="producttypes.number">
				                     <mm:field name="producttypes.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetProducctypesNodes.contains(sID)){
				                        %><mm:field name="producttypes.titel" jspvar="titel" vartype="String" write="false">
													<li><a href="<%= templateUrl %>?p=<%=sPageID%>&pool=<mm:field name="producttypes.number"/>">
														<span class="normal" style="text-decoration:underline;"><%= highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
													</mm:field>	
										<%	}
				                     %></mm:field>
            				      </mm:related>
										<mm:related path="posrel,producttypes,posrel,products">
            				         <mm:field name="products.number" jspvar="sID" vartype="String" write="false"><%
				                     if(hsetProductsNodes.contains(sID)){
            				            %><mm:field name="products.titel" jspvar="titel" vartype="String" write="false">
														<li><a href="<%= templateUrl %>?p=<%=sPageID%>&pool=<mm:field name="producttypes.number"/>&product=<mm:field name="products.number"/>">
															<span class="normal" style="text-decoration:underline;"><%= highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>	
												<mm:field name="products.omschrijving" jspvar="dummy" vartype="String" write="false">
 												  <% textStr = dummy; %>
												</mm:field>
												<br/><%= highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
										<% }
            				         %></mm:field>
				                  </mm:related>
										<mm:related path="posrel,items">
				                     <mm:field name="items.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetItemsNodes.contains(sID)){
                        				%><mm:field name="items.titel" jspvar="titel" vartype="String" write="false">
													<li><a href="<%= templateUrl %>?p=<%=sPageID%>&u=<mm:field name="items.number"/>">
													<span class="normal" style="text-decoration:underline;"><%= highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
												</mm:field>	
												<mm:field name="items.intro" jspvar="dummy" vartype="String" write="false">
 												  <% textStr = dummy; %>
												</mm:field>
												<mm:field name="items.body" jspvar="dummy" vartype="String" write="false">
 												  <% if (!textStr.equals("")) { textStr += " "; }
													  textStr += dummy; %>
												</mm:field>
												<br/><%= highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
										<% }
            				         %></mm:field>
				                  </mm:related>
										<mm:related path="posrel,documents">
				                     <mm:field name="documents.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetDocumentsNodes.contains(sID)){
                        				%><mm:field name="documents.filename" jspvar="titel" vartype="String" write="false">
														<li><a href="<mm:field name="documents.url"/>">
															<span class="normal" style="text-decoration:underline;"><%= highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
													</mm:field>		
										<% }
            				         %></mm:field>
				                  </mm:related>
										<mm:related path="contentrel,vacature">
				                     <mm:field name="vacature.number" jspvar="sID" vartype="String" write="false"><%
            				         if(hsetVacatureNodes.contains(sID)){
                        				%><mm:field name="vacature.titel" jspvar="titel" vartype="String" write="false">
													<li><a href="<%= templateUrl %>?p=<%=sPageID%>&project=<mm:field name="vacature.number"/>">
														<span class="normal" style="text-decoration:underline;"><%= highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
													</mm:field>	
												<% LinkedList ll = new LinkedList();
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
												<%	} %>
												<br/><%= highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
										<% }
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
				</td>
	        <td><img src="media/spacer.gif" width="10" height="1"></td>
   	   </tr>
      </table>
</div>
</td>
<td><% 

// *********************************** right bar *******************************
	if(actionId.equals("adv_search")) { 
		%><%@include file="includes/searchform.jsp"
		%><%@include file="includes/whiteline.jsp" 
   	%><% } 
	%></td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>