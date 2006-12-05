<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/shoppingcart/update.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<mm:locale language="nl">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/shop/header.jsp" %>
<td colspan="3" width="70%">
  <img src="media/spacer.gif" width="1" height="11" border="0" alt=""><br><% 
  if(shop_itemId.equals("-1")) {
    %><jsp:include page="includes/items/items.jsp">
        <jsp:param name="p" value="<%= paginaID %>" />
        <jsp:param name="pu" value="<%= pageUrl %>" />
      </jsp:include><% 
  } else {
    %><jsp:include page="includes/items/item.jsp">
        <jsp:param name="p" value="<%= paginaID %>" />
        <jsp:param name="u" value="<%= shop_itemId %>" />
      </jsp:include><% 
  } %>
</td>
<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
<td width="180">
<%--
if(shop_itemId.equals("-1")) {
  %>
  <jsp:include page="includes/shop/relatedteasers.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
  </jsp:include>
  <jsp:include page="includes/items/linkset.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
      <jsp:param name="u" value="<%= shop_itemId %>" />
    </jsp:include>
  <jsp:include page="includes/items/poolnav.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
    <jsp:param name="pu" value="<%= pageUrl %>" />
  </jsp:include>
  <% 
} else {
  boolean hasExtraInfo = false;
  %><mm:list nodes="<%= shop_itemId %>" path="products,posrel,images" 
      constraints="posrel.pos > 1" max="1"><%
      hasExtraInfo = true;
  %></mm:list><%
  if(!hasExtraInfo) { 
    %><mm:list nodes="<%= shop_itemId %>"	path="products,posrel,attachments" max="1"><%
      hasExtraInfo = true; 
    %></mm:list><% 
  } 
  if(!hasExtraInfo) { 
    %><mm:list nodes="<%= shop_itemId %>" path="products,posrel,artikel" max="1"><%
      hasExtraInfo = true; 
    %></mm:list><%
  }
  if(hasExtraInfo) { 
    %><jsp:include page="includes/items/nav.jsp">
      <jsp:param name="u" value="<%= shop_itemId %>" />
    </jsp:include><%
  } else { 
    %><jsp:include page="includes/shop/relatedteasers.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
    </jsp:include><%
  } 
  %><jsp:include page="includes/shop/relatedlinkset.jsp">
      <jsp:param name="p" value="<%= paginaID %>" />
      <jsp:param name="u" value="<%= shop_itemId %>" />
      <jsp:param name="pu" value="<%= pageUrl %>" />
    </jsp:include><% 
} 
--%></td>
<%@include file="includes/shop/footer.jsp" %>
<%@include file="includes/footer.jsp" %>
</mm:locale>
</cache:cache>
</mm:cloud>