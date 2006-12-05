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
   <%@include file="includes/shop/header.jsp" %>
	<td colspan="3" width="70%">
		<jsp:include page="includes/shop/discount.jsp">
			<jsp:param name="p" value="<%= paginaID %>" />
			<jsp:param name="pu" value="<%= pageUrl %>" />
		</jsp:include>
		<%@include file="includes/shop/relatedpools.jsp" %>
	</td>
	<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<td width="180">
		<jsp:include page="includes/shop/relatedteasers.jsp">
			<jsp:param name="p" value="<%= paginaID %>" />
		</jsp:include>
	</td>
   <%@include file="includes/shop/footer.jsp" %>
<%@include file="includes/footer.jsp" %>
</mm:locale>
</cache:cache>
</mm:cloud>