<%  String cssClassName = "";
    String logoName = "nm";
%><mm:list nodes="<%= rootId %>" path="rubriek,related,style" fields="style.title" max="1"
    ><mm:field name="style.title" jspvar="style_title" vartype="String" write="false"><%
      cssClassName = style_title; 
    %></mm:field
    ><mm:field name="style.stylepar1" jspvar="style_par1" vartype="String" write="false"
      ><mm:isnotempty><% logoName= style_par1; %></mm:isnotempty
    ></mm:field
></mm:list><% 
if(!rubriekId.equals("")) { 
    %><mm:list nodes="<%= rubriekId %>" path="rubriek,related,style" fields="style.title" max="1"
        ><mm:field name="style.title" jspvar="style_title" vartype="String" write="false"><%
            cssClassName = style_title; 
        %></mm:field
        ><mm:field name="style.stylepar1" jspvar="style_par1" vartype="String" write="false"
            ><mm:isnotempty><% logoName= style_par1; %></mm:isnotempty
        ></mm:field
    ></mm:list
><% } %>