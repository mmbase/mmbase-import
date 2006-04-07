<%@include file="includes/templateheader.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" %><%

// using the "project" request parameter for vacatures
// this template gives an overview of vacatures
// - the title of the page is P&O nieuws
String readmoreUrl = "vacature_info.jsp";
if(!articleId.equals("")) { 

   String articleTemplate = "article.jsp" + templateQueryString;
	response.sendRedirect(articleTemplate);
   %><%--jsp:include page="<%= articleTemplate %>" /--%><%

} else if(!projectId.equals("")) { 

   ArrayList al = new ArrayList();
   al.add("functienaam"); 
   al.add("embargo");
   al.add("verloopdatum"); 
   al.add("metatags"); 
   al.add("omschrijving");		
   al.add("functieinhoud"); 
   al.add("functieomvang"); 
   al.add("duur"); 
   al.add("afdeling"); 
   al.add("functieeisen"); 
   al.add("opleidingseisen"); 
   al.add("competenties"); 
   al.add("salarisschaal");
   
   %><mm:node number="<%= projectId %>">
         <td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
      </tr>
      <tr>
         <td class="transperant" colspan="2" style="padding:10px;padding-top:18px;">
         <div class="<%= infopageClass %>">
         <div class="pageheader"><mm:field name="titel"/></div>
         <% if(!postingStr.equals("|action=print")) { %>
            <div align="right" style="letter-spacing:1px;"><a href="javascript:history.go(-1);">terug</a>&nbsp/&nbsp;<a target="_blank" href="ipage.jsp<%= 
                    templateQueryString %>&article=<%=articleId %>&pst=|action=print">print</a></div>
         <% } %>
         <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <mm:related path="posrel,ctexts" constraints="posrel.pos='1'">
            <tr><td colspan="3" style="padding-top:7px;padding-bottom:7px;"><span class="black"><mm:field name="ctexts.body" /></span></td></tr>
         </mm:related>
         <% Iterator ial = al.iterator();
         	while(ial.hasNext()) {
         		String sElem = (String) ial.next();%>
         		<mm:field name="<%= sElem%>" jspvar="thisField" vartype="String" write="false">
         			<mm:isnotempty>
         				<tr>
         					<% if (!sElem.equals("omschrijving")) {%>
   									<mm:fieldlist fields="<%= sElem %>">
   										<td style="width:25%;">
   											<% if(sElem.equals("embargo")) { %>
                                       Gepubliceerd&nbsp;op
                                    <% } else if(sElem.equals("verloopdatum")) { %>
                                       Sluitingsdatum
                                    <% } else if(sElem.equals("metatags")) { %>
                                       Type
                                    <% } else { %>
                                       <mm:fieldinfo type="guiname" />
                                    <% } %>
   										</td>
   										<td>	
   											&nbsp;&nbsp;|&nbsp;&nbsp;
   										</td>
   										<td>	
												<span class="black">
                                       <% if(sElem.equals("embargo")||sElem.equals("verloopdatum")) { 
                                             long td = Integer.parseInt(thisField); td = 1000 * td; Date dd = new Date(td); cal.setTime(dd);
                                             String dateStr =  cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR); 
                                             %>
                                             <%= dateStr %>
	                                    <% } else if(sElem.equals("metatags")) { %>
                                          <%= thisField.substring(0,1).toUpperCase() + thisField.substring(1) %>
                                       <% } else { %>
                                          <mm:fieldinfo type="guivalue" />
                                       <% } %>
                                    </span>
											</td>
   									</mm:fieldlist>	
								<% } else { %>
   								<td colspan="3" style="padding-top:7px;padding-bottom:15px;"><span class="black"><mm:fieldinfo type="guivalue" /></span></td>
   							<% } %>		
         				</tr>		
         			</mm:isnotempty>
         		</mm:field>
         	<% }%>
            <mm:related path="posrel,ctexts" constraints="posrel.pos='99'">
               <tr><td colspan="3" style="padding-top:7px;padding-bottom:15px;"><span class="black"><mm:field name="ctexts.body" /></span></td></tr>
            </mm:related>
            <tr><td colspan="3"><%@include file="includes/attachment.jsp" %></td></tr>
           </table>
           </div>
           </td>
		</mm:node><%

} else {  

   %><td><%@include file="includes/pagetitle.jsp" %></td>
     <td><% String rightBarTitle = "P&O nieuws";
            %><%@include file="includes/rightbartitle.jsp" 
      %></td>
   </tr>
   <tr>
   <td class="transperant">
   <div class="<%= infopageClass %>">
   <table border="0" cellpadding="0" cellspacing="0">
       <tr><td style="padding:10px;padding-top:18px;">
       <%@include file="includes/relatedteaser.jsp" %>
       <% // delete the expired vacatures %>
       <mm:list nodes="<%= pageId %>" path="pagina,posrel,vacature" fields="vacature.number" orderby="vacature.embargo" directions="DOWN"
                constraints="<%= "vacature.verloopdatum < '" + nowSec + "'" %>"> 
            ><mm:deletenode element="posrel" />
       </mm:list>
       <% // show vacatures the vacatures that passed their embargo %>
       <mm:list nodes="<%= pageId %>" path="pagina,posrel,vacature" fields="vacature.number"
                orderby="vacature.embargo" directions="DOWN" constraints="<%= "vacature.embargo <= '" + nowSec + "'" %>"
            ><mm:node element="vacature"><%
               if(isIPage) { readmoreUrl = "ipage.jsp"; }
               %><mm:field name="number" jspvar="vacature_number" vartype="String" write="false"><%
                  readmoreUrl += "?p=" + pageId + "&project=" + vacature_number; 
               %></mm:field>
               <div class="pageheader"><a href="<%= readmoreUrl %>" style="text-decoration:underline"><mm:field name="titel" /></a></div>
               <div class="black" style="margin-bottom:10px;">
               <mm:field name="functieomvang" jspvar="articles_intro" vartype="String" write="false">
                  <mm:isnotempty><%@include file="includes/cleanarticlesintro.jsp" %><br/></mm:isnotempty>
               </mm:field>
               <mm:field name="metatags" jspvar="vacatureType" vartype="String" write="false"> 
                  <%= vacatureType.substring(0,1).toUpperCase() +  vacatureType.substring(1) %>
               </mm:field>
               | Sluitingsdatum <mm:field name="verloopdatum" jspvar="expire_date" vartype="String" write="false"><%
                  long td = Integer.parseInt(expire_date); td = 1000 * td; Date dd = new Date(td); cal.setTime(dd);
                  String dateStr =  cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR); 
                  %><%= dateStr 
               %></mm:field><br/>
               </div>
            </mm:node
       ></mm:list></td>
   </tr>
   </table>
   </div>
   </td><%
   
   // *********************************** right bar *******************************
   %><td><%@include file="includes/whiteline.jsp" 
   %><div class="smoelenboeklist" style="height:478px;">
      <table cellpadding="0" cellspacing="0">
         <tr>
            <td style="padding-bottom:10px;padding-left:19px;padding-right:9px;">
            <% // show the last three news articles related to the pools that are related to this page %>
            <mm:list nodes="<%= pageId %>" path="pagina,posrel,pools">
      		<mm:node element="pools">
                  <mm:related path="posrel,artikel" orderby="artikel.embargo" directions="DOWN" max="3"
                       ><mm:remove referid="this_article"
                       /><mm:node element="artikel" id="this_article"
                       /><%@include file="includes/relatedsummaries.jsp" 
                  %></mm:related>
      		</mm:node>
      		</mm:list>
            </td>
         </tr>
         <tr>
            <td style="text-align:right;padding-bottom:10px;padding-left:19px;padding-right:9px;">
               <span class="pageheader"><span style="color:#FFFFFF;">Tips</span></div>
            </td>
         </tr>
         <tr>
            <td style="text-align:right;padding-bottom:10px;padding-left:17px;padding-right:9px;">
               <table border="0" cellpadding="0" cellspacing="0" width="100%">
	               <tr><td class="white"><img src="media/spacer.gif" style="height:1px;"></td></tr>
               </table>
            </td>
         </tr>
         <tr>
            <td style="padding-bottom:10px;padding-left:19px;padding-right:9px;">
             <mm:list nodes="<%= pageId %>" path="pagina,contentrel,artikel" orderby="contentrel.pos"
                 ><mm:remove referid="this_article"
                 /><mm:node element="artikel" id="this_article"
                 /><%@include file="includes/relatedsummaries.jsp" 
             %></mm:list>
            </td>
         </tr>
      </table>
   </div>
   </td><%
} 
%>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
