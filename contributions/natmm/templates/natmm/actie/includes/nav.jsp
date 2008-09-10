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
   <% 
   int objectCount = 0; 
   int pagesCount = 0;
   %>
   
    <%
    //Please check the navsettings.jsp for details. 2 constraints should be seperated by e.g. ' AND '.
    if (objectConstraint!= null && objectConstraint.length()>0) { 
      objectConstraint += " AND ";
    }  
    objectConstraint += (new SearchUtil()).articleConstraint(nowSec, quarterOfAnHour);
    %>
   
   <mm:relatednodes type="<%= objecttype %>" path="<%= "contentrel," + objecttype %>" constraints="<%= objectConstraint %>">
    <mm:first><mm:size jspvar="dummy" vartype="String" write="false">
      <% objectCount = Integer.parseInt(dummy); 
         pagesCount = objectCount/objectPerPage;
         if (pagesCount*objectPerPage < objectCount) { pagesCount++; }
      %>
    </mm:size></mm:first>
   </mm:relatednodes>
   <%
   boolean isFirst = true;
   if(thisOffset > pagesCount) { thisOffset = pagesCount; }
   if(thisOffset < 1) { thisOffset = 1; }
   if(objectCount>1) {
   %>
   <table cellSpacing="0" cellPadding="0" style="vertical-align:top;width:170px;border-color:828282;border-width:1px;border-style:solid">
   <tr>
   <td style="padding:7px 10px 10px 0px">
     <table cellSpacing="0" cellPadding="0" border="0" style="width:100%;">
       <mm:field name="titel_eng">
       <mm:isnotempty>
          <tr style="padding-bottom:10px;">
            <td colspan="2" style="padding-left:7px;font-weight:bold;">
              <span style="font-size:110%;"><mm:write /></span><br/>
              <mm:field name="titel_de"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
            </td>
          </tr>
          <% isFirst = false; %>
       </mm:isnotempty>
       </mm:field>
       <mm:compare referid="show_links" value="true">

          <mm:relatednodes type="<%= objecttype %>" path="<%= "contentrel," + objecttype %>"
             offset="<%= "" + (thisOffset-1)*objectPerPage %>" max="<%= ""+ objectPerPage %>" 
             constraints="<%= objectConstraint %>" orderby="<%= objecttype + "." + objectdate %>" directions="DOWN">
            <tr style="padding-bottom:0px;">
              <td valign="top" style="width:5px; padding-left:7px; padding-right:3px"><span style="font:bold 110%;color:red">></span></td>
              <td>
                <mm:field name="number" jspvar="oNumber" vartype="String" write="false">
                  <a href="<%= ph.createItemUrl(oNumber, paginaID,"offset="+thisOffset,request.getContextPath())+"&cp="+ callingPageID %>" class="maincolor_link"><b><mm:field name="<%= objecttitle %>"/></b></a>
                   <% if (menuType==TITLE) { %>
                  <mm:field name="<%= objectdate %>" jspvar="object_begindatum" vartype="String" write="false"
                  ><span class="colortxt"> <br/> <mm:time time="<%=object_begindatum%>" format="d MMM yyyy"/></span></mm:field>
                  <% } %>
                </mm:field>
                <% 
                if(menuType!=TITLE) {
                  %>             
                  <span class="colortxt" style="font-size:90%"><mm:field name="<%= objectdate %>" jspvar="object_begindatum" vartype="String" write="false"
                  ><mm:time time="<%=object_begindatum%>" format="d MMM yyyy"/></mm:field></span>
                  <% 
                }
                if(menuType==QUOTE) {
                  %>
                  <mm:field name="<%= objectintro %>" jspvar="text" vartype="String" write="false">
                     <% 
                     if(text!=null) { 
                        text = HtmlCleaner.cleanText(text,"<",">","");
                        if(!text.trim().equals("")) { %><%= text %><% }
                     }
                     %>
                  </mm:field>
                  <%
                } %>
              </td>
            </tr>
            <% isFirst = false; %>
          </mm:relatednodes>
       </mm:compare>
       <%
       if(pagesCount > 1) { 
         if(!isFirst) {
            %>
            <tr style="padding-bottom:10px">
              <td colspan="2" style="padding-left:10px">
                <table class="dotline"><tr><td height="3"></td></tr></table></span>
              </td>
            </tr>
            <%
         } %>
         <tr>
           <td colspan="2" style="padding-left:15px">
              In archief: <%= objectCount %> [<%= pagesCount %> pgn]
             <div style="padding-top:10px;">
               <% if (thisOffset == 1) { %>
                    <img src="<%=request.getContextPath()%>/natmm/media/arrowleft_links.gif" border="0">
               <% } else { %>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+(thisOffset-1),request.getContextPath()) %>"
                      ><img src="<%=request.getContextPath()%>/natmm/media/arrowleft_links.gif" border="0"></a>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset=1",request.getContextPath()) %>">1</a>
               <% } 
                  if (thisOffset > 3) { %>
                    &hellip;                     
               <% } 
                  if (thisOffset > 2) { %>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+(thisOffset-1),request.getContextPath()) %>"><%= thisOffset-1 %></a>
               <% } %>
                  [<%= thisOffset %>]
               <% if (thisOffset+1 < pagesCount) { %>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+(thisOffset+1),request.getContextPath()) %>"><%= thisOffset+1 %></a>
               <% } 
                  if (pagesCount - thisOffset > 2) { %>
                    &hellip;                     
               <% } 
                  if (thisOffset == pagesCount) { %>
                    <img src="<%=request.getContextPath()%>/natmm/media/arrowright_links.gif" border="0">
               <% } else { %>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+pagesCount,request.getContextPath()) %>"><%= pagesCount %></a>
                    <a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+(thisOffset+1),request.getContextPath()) %>"
                      ><img src="<%=request.getContextPath()%>/natmm/media/arrowright_links.gif" border="0"></a>
               <% } %>
             </div>
             <% if (pagesCount > 5) { %>
               <form name="myform" action="<%= ph.createItemUrl(artikelID, paginaID,null,request.getContextPath()) %>" method="post">
                 Ga naar pgn: <input name="offset" style="width:23px;height:17px;font-size:12px;">
                 <a onclick="myform.submit(); return false;" class="colortxt">Zoek</a>
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
   <a name="bottom"></a>
   <%
} %>
</mm:node>
</mm:locale>
</mm:cloud>
