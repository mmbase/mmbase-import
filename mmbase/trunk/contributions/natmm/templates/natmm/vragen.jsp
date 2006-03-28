<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<br>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
	<td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
	<%@include file="includes/navleft.jsp" %>
	<br>
	<jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="0" />
   </jsp:include>
	</td>
	<td style="vertical-align:top;width:100%;padding:10px;padding-top:0px;">
		<table cellspacing="0" cellpadding="0">
			<tr>
			<td style="vertical-align:top;width:374px;padding-right:10px">
            <mm:import id="nodotline" />
            <%@include file="includes/page_intro.jsp" %>
			</td>
			<td style="vertical-align:top;padding-left:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
            <jsp:include page="includes/navright.jsp">
               <jsp:param name="s" value="<%= paginaID %>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="lnr" value="<%= lnRubriekID %>" />
            </jsp:include>
            <jsp:include page="includes/shorty.jsp">
      	      <jsp:param name="s" value="<%= paginaID %>" />
      	      <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
			      <jsp:param name="sr" value="2" />
			   </jsp:include>
			</td>
			</tr>
		</table>
      <img src="media/trans.gif" height="1px" width="539px;" />
	<strong>De meest gestelde vragen en antwoorden</strong><br>
	<table class="dotline"><tr><td height="3"></td></tr></table>
	<jsp:include page="includes/vgv.jsp"><jsp:param name="p" value="<%= paginaID %>" /></jsp:include>
	</td>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>



