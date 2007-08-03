<%
if(!hsetPagesForThisRubriek.contains(sPageID)) {
 continue;
}

String templateUrl = "index.jsp";
String textStr = "";
String titleStr = "";
String showDate = "1";
boolean bHasAttachments = false;
%>
<mm:node number="<%= sPageID %>">
  <%= (!bFirst ? "<br/>": "" ) %>
  <mm:related path="gebruikt,template">
    <mm:field name="template.url" jspvar="url" vartype="String" write="false">
      <% templateUrl = url; %>
    </mm:field>
  </mm:related>
  <%
  if(hsetPageDescrNodes.contains(sPageID)){
   %>
   <mm:field name="titel" jspvar="titel" vartype="String" write="false">
      <li><a href="<%= templateUrl %>?p=<%=sPageID%>">
        <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
    </mm:field>	
    <mm:field name="omschrijving" jspvar="dummy" vartype="String" write="false">
      <% textStr = dummy; %>
    </mm:field>
    <br/><%= su.highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
    <% 
  } else {
    %>
    <b><mm:field name="titel"/></b>
    <%
  } %>
  <ul style="margin:0px;margin-left:16px;">
    <%@include file="artikel.jsp" %>
    <%@include file="teaser.jsp" %>
    <%@include file="producttypes.jsp" %>
    <%@include file="products.jsp" %>
    <%@include file="items.jsp" %>
    <%@include file="documents.jsp" %>
    <%@include file="vacature.jsp" %>
    <%@include file="contentblocks.jsp" %>
    <%@include file="vraagbaak.jsp" %>    
    <% 
    if (bHasAttachments) {
      %>
      <a href="<%= templateUrl %>?p=<%=sPageID%>">
      <span class="normal" style="text-decoration:underline;"><mm:field name="titel"/></span></a><br/>
      <% 
    } 
    bHasAttachments = false; %>	
  </ul>
</mm:node>
