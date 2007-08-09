<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<mm:locale language="nl">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/shoppingcart/update.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
</cache:cache>
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/shop/header.jsp" %>
<td colspan="3" width="70%" style="vertical-align:top;">
  <img src="media/trans.gif" width="1" height="11" border="0" alt=""><br><% 
  if(shop_itemID.equals("-1")) {
    %>
    <jsp:include page="includes/items/items.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
    </jsp:include>
    <% 
  } else {
    %>
    <jsp:include page="includes/items/item.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
      <jsp:param name="u" value="<%= shop_itemID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
    </jsp:include>
    <% 
  } %>
</td>
<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
<td width="180" style="vertical-align:top;">
<%
if(shop_itemID.equals("-1")) {
  %>
  <jsp:include page="includes/shop/relatedteasers.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
  </jsp:include>
  <%@include file="includes/items/linkset.jsp" %>
  <%@include file="includes/items/poolnav.jsp" %>
  <% 
} else {
  boolean hasExtraInfo = false;
  %><mm:list nodes="<%= shop_itemID %>" path="items,posrel,images" 
      constraints="posrel.pos > 1" max="1"><%
      hasExtraInfo = true;
  %></mm:list><%
  if(!hasExtraInfo) { 
    %><mm:list nodes="<%= shop_itemID %>"	path="items,posrel,attachments" max="1"><%
      hasExtraInfo = true; 
    %></mm:list><% 
  } 
  if(!hasExtraInfo) { 
    %><mm:list nodes="<%= shop_itemID %>" path="items,posrel,artikel" max="1"><%
      hasExtraInfo = true; 
    %></mm:list><%
  }
  if(hasExtraInfo) { 
    %><jsp:include page="includes/items/nav.jsp">
      <jsp:param name="u" value="<%= shop_itemID %>" />
    </jsp:include><%
  } else { 
    %><jsp:include page="includes/shop/relatedteasers.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
    </jsp:include><%
  } 
  %><jsp:include page="includes/shop/relatedlinkset.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
      <jsp:param name="u" value="<%= shop_itemID %>" />
    </jsp:include><% 
} 
%></td>
<%@include file="includes/shop/footer.jsp" %>
<%@include file="includes/footer.jsp" %>
</mm:locale>
</mm:cloud>