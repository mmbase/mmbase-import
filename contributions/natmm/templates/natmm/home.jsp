<% // *** one article, with shorties and teasers  *** %>
<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<% if(rubriekExists&&pageExists) { %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<% if(artikelID.equals("-1")) { %>
   <mm:list nodes="<%=paginaID%>" path="pagina,contentrel,artikel" fields="artikel.number" orderby="contentrel.pos" directions="up" max="1">
   	<mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
   		<% artikelID = artikel_number;%>
   	</mm:field>
   </mm:list><%
} %>
<br>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
	<td style="vertical-align:top;width:185px;padding:10px;padding-top:0px;">
   	<%@include file="includes/navleft.jsp" %>
      <br />
   	<jsp:include page="includes/teaser.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="0" />
      </jsp:include>
      <img src="media/trans.gif" height="1px" width="165px;" />
	</td>
	<td style="vertical-align:top;width:374px;padding:10px;padding-top:0px">
      <mm:import id="nodotline" />
      <%@include file="includes/page_intro.jsp" %>
      <jsp:include page="includes/teaser.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="tl" value="button" />
	      <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="1" />
      </jsp:include>	
   </td>
   <td style="vertical-align:top;width:185px;padding-left:10px;padding-right:10px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
      <jsp:include page="includes/home/relateddossiers.jsp">
		   <jsp:param name="o" value="<%= paginaID %>"/>
	   </jsp:include>
      <br/>
		<jsp:include page="includes/home/shorty_home.jsp">
	      <jsp:param name="s" value="<%= paginaID %>" />
	      <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
	      <jsp:param name="sr" value="2" />
	   </jsp:include>
      <img src="media/trans.gif" height="1px" width="165px;" />
   </td>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</cache:cache>
<% } %>
</mm:cloud>



