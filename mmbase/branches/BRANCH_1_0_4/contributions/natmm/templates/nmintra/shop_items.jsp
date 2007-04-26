<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/header.jsp" %>
<% String pageUrl =  ph.createPaginaUrl(paginaID,request.getContextPath()) + "?u=" + shop_itemId; %>
<td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
</tr>
<tr>
<td class="transperant" colspan="2">
<div class="<%= infopageClass %>" id="infopage">
  <div style="padding-left:10px;padding-right:10px;"><%@include file="includes/relatedteaser.jsp" %></div>
  <table cellspacing="10px" cellpadding="0">
    <td colspan="3" width="70%"><% 
    if(shop_itemId.equals("-1")) {
      %><%@include file="includes/shop_items/items.jsp" %><% 
    } else {
      %><%@include file="includes/shop_items/item.jsp" %><% 
    } %>
    </td>
    <td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
    <td width="180"><% 
    if(shop_itemId.equals("-1")) {
      %><%@include file="includes/shop_items/linkset.jsp" 
      %><%@include file="includes/shop_items/poolnav.jsp" %><% 
    } else {
      boolean hasExtraInfo = false;
      %><mm:list nodes="<%= shop_itemId %>" path="items,posrel,images" 
          constraints="posrel.pos > 1" max="1"
        ><% hasExtraInfo = true;
      %></mm:list><%
      if(!hasExtraInfo) { 
        %><mm:list nodes="<%= shop_itemId %>" path="items,posrel,attachments" max="1"
          ><% hasExtraInfo = true; 
        %></mm:list><% 
      } 
      if(!hasExtraInfo) { 
        %><mm:list nodes="<%= shop_itemId %>" path="items,posrel,artikel" max="1"
          ><% hasExtraInfo = true; 
        %></mm:list><%
      }
      if(hasExtraInfo) { 
        %><%@include file="includes/shop_items/nav.jsp" %><%
      } 
      %><%@include file="includes/shop_items/linkset.jsp" %><% 
    } %>
    </td>
  </table>
</div>
</td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>

