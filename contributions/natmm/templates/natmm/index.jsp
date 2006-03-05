<%@include file="includes/top0.jsp" %>
<%-- if(paginaID.equals("-1")&&ID.equals("-1")) {
   response.sendRedirect("/100jaarlater/index.html");
} --%>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<% if(rubriekExists&&pageExists) { %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
	<td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
	<jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="0" />
   </jsp:include>
   </td>
	<td style="vertical-align:top;width:374px;padding:10px;padding-top:0px">
      <br/>
	   <jsp:include page="includes/home/relateddossiers.jsp">
			<jsp:param name="o" value="<%= paginaID %>"/>
		</jsp:include>
	<br> 
	<jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="1" />
   </jsp:include>	
   </td>
	<td style="vertical-align:top;width:185px;padding:10px;padding-top:7px;">
      <jsp:include page="includes/home/shorty_home.jsp">
	      <jsp:param name="s" value="<%= paginaID %>" />
 	      <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
	      <jsp:param name="sr" value="2" />
	   </jsp:include>
      <img src="media/trans.gif" height="1px" width="165px;" /></td>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</cache:cache>
<% } %>
</mm:cloud>
