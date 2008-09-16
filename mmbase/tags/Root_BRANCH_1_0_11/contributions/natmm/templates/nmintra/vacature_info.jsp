<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%

// using the "project" request parameter for vacatures
// this template gives an overview of vacatures

if(!articleId.equals("-1")) { 

  String articleTemplate = "article.jsp" + templateQueryString;
  articleTemplate += (articleTemplate.indexOf("?")==-1 ? "?" : "&" ) + "showteaser=false";
	response.sendRedirect(articleTemplate);

} else {

  %>
  <%@include file="includes/cacheparams.jsp" %>
  <% expireTime = newsExpireTime; %>
  <cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
  <%@include file="includes/calendar.jsp"%>
  <%@include file="includes/header.jsp" %> 
  <%

  if(!projectId.equals("")) { 
  
     ArrayList al = new ArrayList();
     al.add("omschrijving_de"); 
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
           <td class="transperant" colspan="2">
           <div class="<%= infopageClass %>" id="infopage" style="padding:10px;padding-top:18px;">
           <div class="pageheader"><mm:field name="titel"/></div>
           <%@include file="includes/back_print.jsp" %>
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
                    <% if (sElem.indexOf("omschrijving")==-1) {%>
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
     
     String newsConstraint = (new SearchUtil()).articleConstraint(nowSec,quarterOfAnHour);
     String readmoreUrl = "vacature_info.jsp";
     %><td><%@include file="includes/pagetitle.jsp" %></td>
       <td>
           <mm:list nodes="<%= paginaID %>" path="pagina,readmore,artikel" max="1">
              <% String rightBarTitle = "Tips";
              %><%@include file="includes/rightbartitle.jsp" %>
           </mm:list>
       </td>
     </tr>
     <tr>
     <td class="transperant">
     <div class="<%= infopageClass %>" id="infopage">
     <table border="0" cellpadding="0" cellspacing="0">
         <tr><td style="padding:10px;padding-top:18px;">
         <%@include file="includes/relatedteaser.jsp" %>
         
         <% // delete the expired vacatures %>
         <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,vacature" fields="vacature.number"
              orderby="contentrel.pos,vacature.embargo" directions="UP,DOWN"
              constraints="<%= "vacature.verloopdatum < '" + nowSec + "'" %>"> 
              <mm:deletenode element="contentrel" />
         </mm:list>
         <% // show vacatures the vacatures that passed their embargo %>
         <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,vacature" fields="vacature.number"
                  orderby="contentrel.pos,vacature.embargo" directions="UP,DOWN"
                  constraints="<%= "vacature.embargo <= '" + nowSec + "'" %>"
              ><mm:node element="vacature"><%
                 %><mm:field name="number" jspvar="vacature_number" vartype="String" write="false"><%
                    readmoreUrl = "?p=" + paginaID + "&project=" + vacature_number; 
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
         ></mm:list>
        <mm:import id="hrefclass"></mm:import>
        <%@include file="includes/info/movetoarchive.jsp" 
        %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel"  searchdir="destination" 
           orderby="artikel.embargo" directions="DOWN" constraints="<%= newsConstraint %>"
           ><mm:remove referid="this_article"
           /><mm:node element="artikel" id="this_article"
           /><%@include file="includes/relatedsummaries.jsp" 
        %></mm:list>
         
         </td>
     </tr>
     </table>
     </div>
     </td><%
     
     // *********************************** right bar *******************************
     %><td>
        <mm:import id="nodates" />
        <mm:import id="hrefclass" reset="true">menuitem</mm:import>
        <mm:list nodes="<%= paginaID %>" path="pagina,readmore,artikel" orderby="readmore.pos" constraints="<%= newsConstraint %>">
           <mm:first>
              <%@include file="includes/whiteline.jsp" %>
              <div class="rightcolumn" id="rightcolumn" style="padding-left:20px;padding-right:10px;">
           </mm:first>
              <mm:remove referid="this_article"
              /><mm:node element="artikel" id="this_article"
              /><%@include file="includes/relatedsummaries.jsp" %>
           <mm:last>
              </div>
           </mm:last>
        </mm:list>
     </td><%
  }
  %>
  <%@include file="includes/footer.jsp" %>
  </cache:cache>
  <%
}
%>
</mm:cloud>
