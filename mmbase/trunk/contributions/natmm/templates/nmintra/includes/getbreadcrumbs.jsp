<%  
if(debug) { %><br>Trying to find breadcrums for page <%= pageId %> and category <%= allCategoryIds %>: <% } 
String nodeHref = "";
String nodeUrl = "";
// *** normal page ***
%><mm:field name="number" jspvar="node_number" vartype="String" write="false"
><mm:list nodes="<%= allCategoryIds %>" path="rubriek1,parent,rubriek2,posrel,pagina" constraints="<%= "pagina.number='" + pageId + "'" %>"
   ><%@include file="../includes/relatednodehref.jsp" %><%
   if(debug) { %>breadcrums for regular page<% } 
%></mm:list><%

// *** subpage *** 
if(nodeHref.equals("")) {
%><mm:list nodes="<%= allCategoryIds %>" path="rubriek1,parent1,rubriek2,parent2,rubriek3,posrel,pagina"
    constraints="<%= "pagina.number='" + pageId + "'" %>"
       ><%@include file="../includes/relatednodehref.jsp" %><% 
       if(debug) { %>breadcrums for subpage<% } 
    %></mm:list><% 
}

// *** homepage (nothing there) ***
if(false&&!nodeHref.equals("")) {
    %><mm:list nodes="<%= allCategoryIds %>" path="rubriek,posrel,pagina" constraints="<%= "pagina.number='" + pageId + "'" %>"><% 
       if(debug) { %>breadcrums for homepage (nothing there)<% } 
    %></mm:list><%
}

%></mm:field>