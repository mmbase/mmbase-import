 <%
 if(iRubriekLayout==NatMMConfig.SUBSITE2_LAYOUT) { %>
   <style>
      span.navbutton {
         background-color:#10086B;
         color:#FFFFFF;
      }
      A.navbutton {
         background-color: #F37021;
      }
      A.navbutton:visited {
         color: #FFFFFF;
      }
      A.navbutton:hover {
         color:#FFFFFF;
      }
      A.subnavbutton {
         color:#FFFFFF;
      }
      A.subnavbutton:hover {
         background-color: #F9B790;
      	color: #050080;
      }
      A.subnavbutton:visited {
         color:#FFFFFF;
      }
      A.subnavbutton-high {
      	background-color: #F9B790;
      	color: #050080;
      }
      A.subnavbutton-high:visited {
      	color: #050080;
      }
   </style>
   <% 
} %>  
<table cellspacing="0" cellpadding="0" border="0" style="width:165px;">
<% if(iRubriekLayout!=NatMMConfig.DEFAULT_LAYOUT) { 
   %><tr>
	   <td height="1" class="leftnavline" style="<%= 
	         (iRubriekLayout==NatMMConfig.SUBSITE1_LAYOUT ? "background-color:#050080;" : "background-color:#F06E23;" ) 
	      %>"><img src="media/trans.gif" width="165px" height="1" vspace="0" border="0" alt=""></td>
   </tr><% 
}
int navCnt = 0;
TreeMap subObjects = new TreeMap();

// ** if this page is directly under the root of the website, don't show the rubrieken
%><mm:node number="<%= lnRubriekID %>">
   <mm:field name="level">
     <mm:compare value="1" inverse="true">
         <% subObjects =(TreeMap) rubriekHelper.getSubObjects(lnRubriekID); %>
     </mm:compare>
   </mm:field>
</mm:node><%
 
subObjects.put(new Integer(-1),lnRubriekID);
while(!subObjects.isEmpty()) {
   Integer nextKey = (Integer) subObjects.firstKey();
   String nextObject =  (String) subObjects.get(nextKey);

   String nextPage =  rubriekHelper.getFirstPage(nextObject);
   
   String pageClass = "subnavbutton";
   if(paginaID.equals(nextPage) || rubriekID.equals(nextObject)){
      pageClass = "subnavbutton subnavbutton-high"; 
   }
   if(navCnt==0) {
      pageClass = "navbutton"; 
   }
      
   if(!nextPage.equals("-1")) {
      %>
      <tr>
      	<td style="text-align:right;width:165px;line-height:85%;padding:0px;<%= 
      	      (iRubriekLayout==NatMMConfig.SUBSITE2_LAYOUT ? "background-color:#10086B;" : "" ) %>"><%
      	   if(navCnt==0) {
      	      %><span class="<%= pageClass %>">
      	            <mm:node number="<%= lnRubriekID %>"><mm:field name="naam" /></mm:node></span><% 
      	   } else { 
      	      %><a href="<%= ph.createPaginaUrl(nextPage,request.getContextPath()) %>" class="<%= pageClass %>">
                     <mm:node number="<%= nextObject %>"
            	         ><mm:field name="naam"
            	            ><mm:isnotempty><mm:write /></mm:isnotempty
            	            ><mm:isempty><mm:field name="titel" /></mm:isempty
            	         ></mm:field
            	      ></mm:node>
                  </a><% 
      	   } %></td>
      </tr>
      <tr>
      	<td height="1" class="leftnavline" <%
      	   if(iRubriekLayout==NatMMConfig.SUBSITE1_LAYOUT) { 
      	      %>style="background-color:#050080;"<% 
      	   } else if(iRubriekLayout==NatMMConfig.SUBSITE2_LAYOUT) {
      	      %>style="background-color:#F06E23;"<%  
      	   } %>><img src="media/trans.gif" width="165px" height="1" vspace="0" border="0" alt=""></td>
      </tr>
      <%
   } 
   subObjects.remove(nextKey);
   navCnt++;
} %>
<mm:node number="<%= lnLogoID %>" notfound="skipbody">
   <tr>
   	<td style="width:165px;padding:0px;"><img src="<mm:image />" border="0" alt=""></td>
   </tr>
</mm:node>
</table>
