<%

// *** translate alias back to numbers ***
%><mm:node number="<%= websiteId %>" notfound="skipbody"
    ><mm:field name="number" jspvar="website_number" vartype="String" write="false"><%
        websiteId = website_number; 
    %></mm:field
></mm:node

><mm:node number="<%= rubriekId %>" notfound="skipbody"
    ><mm:field name="number" jspvar="rubriek_number" vartype="String" write="false"><%
        rubriekId = rubriek_number; 
    %></mm:field
></mm:node

><mm:node number="<%= pageId %>" notfound="skipbody"
    ><mm:field name="number" jspvar="page_number" vartype="String" write="false"><%
        pageId = page_number; 
    %></mm:field
></mm:node><%

// *** deep linking for articles from the shop ***
if(pageId.equals("")&&!shop_itemId.equals("-1")) { // try the shop_item detail page
    %><mm:list nodes="<%= shop_itemId %>" path="items,posrel,pagina" max="1"
        ><mm:field name="pagina.number" jspvar="page_number" vartype="String" write="false"
            ><% pageId = page_number; 
        %></mm:field
    ></mm:list><% 
}

// *** if no websiteId is defined try to find one ***
if(websiteId.equals("")&&!pageId.equals("")){ 

    boolean websiteExists = false;

    // normal page 
    %><mm:list nodes="<%= pageId %>" path="pagina,posrel,rubriek1,parent,rubriek2" fields="rubriek2.number" ><%--constraints="posrel.pos='1'"
        --%><mm:field name="rubriek2.number" jspvar="dummy" vartype="String" write="false"><%
            websiteId = dummy; 
        %></mm:field
        ><mm:field name="rubriek1.number" jspvar="dummy" vartype="String" write="false"><%
            rubriekId = dummy; 
        %></mm:field
        ><% websiteExists = true; 
    %></mm:list><% 

    // homepage
    if(!websiteExists) { 
    %><mm:list nodes="<%= pageId %>" path="pagina,posrel,rubriek" fields="rubriek.number"
        ><mm:field name="rubriek.number" jspvar="dummy" vartype="String" write="false"><%
            websiteId = dummy; 
        %></mm:field><%
            websiteExists = true; 
    %></mm:list><%
    } 

} 

// *** still no website found? last resort take the intranet ***
if(websiteId.equals("")) { websiteId = "home"; } 

if(pageId.equals("")) {
    if(!rubriekId.equals("")){ // *** if page is not defined, take rubriekpage if possible ***
       %><mm:node number="<%= rubriekId %>" notfound="skipbody"
           ><mm:related path="posrel,pagina" fields="pagina.number" orderby="posrel.pos" max="1"
               ><mm:field name="pagina.number" jspvar="dummy" vartype="String" write="false"><%
                    pageId = dummy; 
               %></mm:field
           ></mm:related
       ></mm:node><% 
    } 
    if(pageId.equals("")){ // *** still no page, take the first page under the first rubriek ***
      %><mm:node number="<%= websiteId %>" notfound="skipbody"
         ><mm:related path="parent,rubriek,posrel,pagina" fields="pagina.number"
               orderby="parent.pos,posrel.pos" directions="UP" max="1"
             ><mm:field name="pagina.number" jspvar="dummy" vartype="String" write="false"><%
                 pageId = dummy; 
             %></mm:field
         ></mm:related
      ></mm:node><% 
   }
}

// *** if rubriek is not defined, try to find it ***

if(rubriekId.equals("")&&!pageId.equals("")){ 

    %><mm:list nodes="<%= websiteId %>" path="rubriek,posrel,pagina" 
        constraints="<%= "pagina.number='" + pageId + "'"%>"
        ><mm:field name="rubriek.number" jspvar="dummy" vartype="String" write="false"><%
            rubriekId = dummy; 
        %></mm:field
    ></mm:list><% 
    
    if(rubriekId.equals("")) { 
      %><mm:list nodes="<%= websiteId %>" path="rubriek1,parent,rubriek2,posrel,pagina" 
           constraints="<%= "pagina.number='" + pageId + "'"%>"
           ><mm:field name="rubriek2.number" jspvar="dummy" vartype="String" write="false"><%
               rubriekId = dummy; 
           %></mm:field
       ></mm:list><% 
    }
} 

if(categoryId.equals("")&&!rubriekId.equals("")) {
    categoryId = rubriekId;
}

%>