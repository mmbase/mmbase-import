<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<mm:locale language="nl">
<%@include file="../../includes/getstyle.jsp" %>
<%@include file="../../includes/time.jsp" %>
<%
PaginaHelper ph = new PaginaHelper(cloud);

int thisOffset = 1;
try{
   if(!offsetID.equals("0")){
      thisOffset = Integer.parseInt(offsetID);
      offsetID ="";
   }
} catch(Exception e) {} 

%>
<mm:node number="<%= paginaID %>">
 <%@include file="navsettings.jsp" %>
 <table cellSpacing="0" cellPadding="0" style="vertical-align:top;width:170px;border-color:828282;border-width:1px;border-style:solid">
 <tr>
   <td style="padding:7px 10px 10px 0px">
     <% 
     int artikelNum = 0; 
     int pagesNum = 0;
     %>
     <mm:relatednodes type="artikel" path="contentrel,artikel" constraints="<%= artikelConstraint %>">
       <mm:first><mm:size jspvar="dummy" vartype="String" write="false">
         <% artikelNum = Integer.parseInt(dummy); 
            pagesNum = artikelNum/articlePerPage;
            if (pagesNum*articlePerPage < artikelNum) { pagesNum++; }
         %>
       </mm:size></mm:first>
     </mm:relatednodes>
     <% if (thisOffset > pagesNum) { thisOffset = pagesNum; }
        if (thisOffset < 1) { thisOffset = 1; }
     %>
     <table cellSpacing="0" cellPadding="0" border="0">
       <mm:field name="titel_eng">
       <mm:isnotempty>
          <tr style="padding-bottom:10px;">
            <td colspan="2" style="padding-left:7px;font-weight:bold;">
              <span style="font-size:110%;"><mm:write /></span><br/>
              <mm:field name="titel_de"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
            </td>
          </tr>
       </mm:isnotempty>
       </mm:field>
       <mm:relatednodes type="artikel" path="contentrel,artikel" max="<%= ""+ articlePerPage %>" offset="<%= "" + (thisOffset-1)*articlePerPage %>" 
          constraints="<%= artikelConstraint %>" orderby="<%= artikelOrderby %>" directions="<%= artikelDirections %>">
         <tr style="padding-bottom:10px;">
           <td valign="top" style="width:5px; padding-left:7px; padding-right:3px"><span style="font:bold 110%;color:red">></span></td>
           <td>
             <mm:field name="number" jspvar="aNumber" vartype="String" write="false">
               <a href="<%= ph.createItemUrl(aNumber, paginaID,"offset="+thisOffset,request.getRequestURI()) %>" class="maincolor_link"><b><mm:field name="titel"/></b></a>
             </mm:field>
             <% 
             if(menuType!=TITLE) {
               %>             
               <span class="colortxt"><mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false"
               ><mm:time time="<%=artikel_begindatum%>" format="d MMM yyyy"/></mm:field></span>
               <% 
             }
             if(menuType==QUOTE) {
               %>
               <mm:field name="intro" jspvar="text" vartype="String" write="false">
                  <% 
                  if(text!=null) { 
                     text = HtmlCleaner.cleanText(text,"<",">","");
                     if(!text.trim().equals("")) { %>"<%= text %>"<% }
                  }
                  %>
               </mm:field>
               <%
             } %>
           </td>
         </tr>
       </mm:relatednodes>
       <% if(pagesNum > 1) { %>
         <tr style="padding-bottom:10px">
           <td colspan="2" style="padding-left:10px">
             <table class="dotline"><tr><td height="3"></td></tr></table></span>
           </td>
         </tr>
         <tr>
           <td colspan="2" style="padding-left:15px">
              In archief: <%= artikelNum %> <mm:field name="titel.fr"/> [<%= pagesNum %> pgn]
             <div style="padding-top:10px;">
               <% if (thisOffset == 1) { %>
                    <img src="../media/arrowleft_<%= style1[iRubriekStyle] %>.gif" border="0">
               <% } else { %>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+(thisOffset-1),request.getRequestURI()) %>"
                      ><img src="../media/arrowleft_<%= style1[iRubriekStyle] %>.gif" border="0"></a>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset=1",request.getRequestURI()) %>">1</a>
               <% } 
                  if (thisOffset > 3) { %>
                    &hellip;                     
               <% } 
                  if (thisOffset > 2) { %>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+(thisOffset-1),request.getRequestURI()) %>"><%= thisOffset-1 %></a>
               <% } %>
                  [<%= thisOffset %>]
               <% if (thisOffset+1 < pagesNum) { %>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+(thisOffset+1),request.getRequestURI()) %>"><%= thisOffset+1 %></a>
               <% } 
                  if (pagesNum - thisOffset > 2) { %>
                    &hellip;                     
               <% } 
                  if (thisOffset == pagesNum) { %>
                    <img src="../media/arrowright_<%= style1[iRubriekStyle] %>.gif" border="0">
               <% } else { %>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+pagesNum,request.getRequestURI()) %>"><%= pagesNum %></a>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+(thisOffset+1),request.getRequestURI()) %>"
                      ><img src="../media/arrowright_<%= style1[iRubriekStyle] %>.gif" border="0"></a>
               <% } %>
             </div>
             <% if (pagesNum > 5) { %>
               <form name="myform" action="<%= ph.createItemUrl(artikelID, paginaID,null,request.getRequestURI()) %>" method="post">
                 Ga naar pgn: <input name="offset" style="width:23px;height:17px;font-size:12px;">
                 <a href="#bottom" onclick="myform.submit(); return false;" class="colortxt">Zoek</a>
                 <span class="colortxt" style="font:bold 110%;">></span>
               </form>
             <% } %>
           </td>
         </tr>
       <% } %>
     </table>
   </td>
 </tr>
</table>
</mm:node>
</mm:locale>
</mm:cloud>
