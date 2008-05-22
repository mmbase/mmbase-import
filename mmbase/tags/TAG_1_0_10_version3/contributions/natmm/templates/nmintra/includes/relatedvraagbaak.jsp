<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">

<% 
String vraagId=request.getParameter("v");
String callingNode=request.getParameter("c");

String rb = request.getParameter("rb");
String rbid = request.getParameter("rbid");
String pgid = request.getParameter("pgid");
String ssid = request.getParameter("ssid");

String printAction=request.getParameter("pst");
boolean printView = ((printAction != null) && (printAction.indexOf("print") != -1));
// needed to support images in paragraph include
String imageTemplate = "";
PaginaHelper ph = new PaginaHelper(cloud);
%>
<% // print page proper css format
if (printView) { %>
	<html><head><link rel="stylesheet" type="text/css" href="../css/main.css"></head><body style="overflow: scroll !important;">
<% } %>


<mm:node number="<%=vraagId%>">

   <mm:field name="titel_zichtbaar">
   <mm:compare value="0" inverse="true">  
   
     <table bgcolor="#d9e4f4" width="100%" >
     <tr>
     <td>
        <div style="float:left;">
            <div class="pageheader"><mm:field name="titel" /></div>
        </div>
        <div style="float:right;">
           <% if (!printView) { %>
           <a href="javascript:history.go(-1);">terug</a>
           /
           <a target="_blank" href="includes/relatedvraagbaak.jsp?&pst=|action=print&v=<%=vraagId%>">print</a>
         	<% } %>  
         </div>
   
     </td>
     </tr>
     <tr>
     <td>
        <div style="float:left;">
         	<mm:relatednodes type="pools" max="1">
           Status:
             <mm:field name="name"/>
             </mm:relatednodes>
         </div>
        <div style="float:right;">
           	<mm:relatednodes type="persoon" max="1">
             Deskundige:
             <a href="smoelenboek_vraagbaak.jsp?employee=<mm:field name="number"/>&rb=<%=rb%>&rbid=<%=rbid%>&pgid=<%=pgid%>&ssid=<%=ssid%>"><mm:field name="titel"/></a>
             </mm:relatednodes>
         </div>
   
     </td>
     </tr>  
     </table>

   </mm:compare>
   </mm:field>

   <mm:field name="intro">

      <mm:related path="posrel,images"
         constraints="posrel.pos='9'" orderby="images.title"
         ><br/><div align="center"><img src="<mm:node element="images"><mm:image template="s(535)" /></mm:node
            >" alt="<mm:field name="images.title" />" border="0" ></div><br/>
      </mm:related>
      
      <mm:related path="posrel,images" orderby="images.title" constraints="posrel.pos!='9'" 
          ><mm:first
             ><mm:field name="posrel.pos" jspvar="dummy" vartype="Integer" write="false"><%
                int posrel_pos = dummy.intValue();  
                if((2<posrel_pos)&&(posrel_pos<5)) { imageTemplate = "+s(80)"; }
                if((4<posrel_pos)&&(posrel_pos<7)) { imageTemplate = "+s(180)"; }
                if(6<posrel_pos) { imageTemplate = "+s(400)"; }
              %></mm:field>
          </mm:first>    
               <div style="display: block; clear: both; padding-bottom: 5px;">                                        
                  <div style="float: <%@include file="imagesposition_vraagbaak.jsp" %>; display: block; padding-bottom: 5px;">
                     <img src="<mm:node element="images"><mm:image template="<%= imageTemplate %>" /></mm:node>" alt="<mm:field name="images.title" />" border="0">
                  </div>
               
               </div>
      </mm:related>

      <mm:isnotempty><span class="black"><mm:write /></span></mm:isnotempty>

   </mm:field>

   <mm:related path="posrel,paragraaf" orderby="posrel.pos" directions="UP">
      <mm:first><br/></mm:first>

         <mm:node element="paragraaf">

            <mm:related path="posrel,images"
               constraints="posrel.pos='9'" orderby="images.title"
               ><br/><div align="center"><img src="<mm:node element="images"><mm:image template="s(535)" /></mm:node
                  >" alt="<mm:field name="images.title" />" border="0" ></div><br/>
            </mm:related>

           <mm:field name="titel_zichtbaar">
               <mm:compare value="0" inverse="true">
                  <div class="pageheader" style="display: block; clear: both; padding-top: 5px;"><mm:field name="titel"/></div>
               </mm:compare>
           </mm:field>            
            
            <mm:related path="posrel,images" orderby="images.title" constraints="posrel.pos!='9'" 
                ><mm:first
                   ><mm:field name="posrel.pos" jspvar="dummy" vartype="Integer" write="false"><%
                      int posrel_pos = dummy.intValue();  
                      if((2<posrel_pos)&&(posrel_pos<5)) { imageTemplate = "+s(80)"; }
                      if((4<posrel_pos)&&(posrel_pos<7)) { imageTemplate = "+s(180)"; }
                      if(6<posrel_pos) { imageTemplate = "+s(400)"; }
                    %></mm:field>
                </mm:first>    
                  <div style="display: block; clear: both; padding-bottom: 5px;">                          
                     <div style="float: <%@include file="imagesposition_vraagbaak.jsp" %>; display: block; padding-bottom: 5px;">
                        <img src="<mm:node element="images"><mm:image template="<%= imageTemplate %>" /></mm:node>" alt="<mm:field name="images.title" />" border="0">
                     </div>
                  
                  </div>
            </mm:related>
           
           <mm:field name="tekst"><mm:isnotempty><span class="black"><mm:write /></span></mm:isnotempty></mm:field>
           <%@include file="../includes/attachment.jsp" %>
           <mm:related path="posrel,link" orderby="posrel.pos,link.titel" directions="UP,UP"
               ><br/>
               <a target="<mm:field name="link.target" />" href="<mm:field name="link.url" />" title="<mm:field name="link.alt_tekst" />" >
                 <mm:field name="link.titel" />
               </a>
           </mm:related>
           <mm:related path="readmore,pagina" orderby="readmore.pos,pagina.titel" directions="UP,UP"
               ><br/>
               <a href="<mm:node element="pagina" jspvar="p"><%= ph.createPaginaUrl(p.getStringValue("number"),request.getContextPath()) %></mm:node>"
                  title="<mm:field name="pagina.titel" />" >
                   <mm:field name="readmore.readmore" /> <mm:field name="pagina.titel" />
               </a>
           </mm:related>
           <br/>
           <br/>

         </mm:node>


    </mm:related>
  
  <% if (!printView && callingNode != null) { %>
  	<div style="display: block; clear: both; padding-bottom: 5px;">
      &nbsp;<br/><a href="<%= ph.createPaginaUrl(callingNode,request.getContextPath()) %>#top">naar boven</a>
   </div><br/>
  <% } %>	

</mm:node>

<% // print page proper css format
if (printView) { %>
   </body></html>
<% } %>

</mm:cloud>


