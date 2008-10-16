<% // *** article + articles in middle and left column *** %>
<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<% if(artikelID.equals("-1")) { %>
   <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" 
      fields="artikel.number" orderby="contentrel.pos" directions="up" max="1">
   	<mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
   		<% artikelID = artikel_number;%>
   	</mm:field>
   </mm:list><%
} %>
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
	<td style="width:374;vertical-align:top;padding:10px;padding-top:0px;">
      <%@include file="includes/page_intro.jsp" %>
      <% if(!artikelID.equals("-1")) { 
         %><jsp:include page="includes/artikel_1_column.jsp">
            <jsp:param name="o" value="<%= artikelID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
         </jsp:include><%
      } %>
      <jsp:include page="includes/shorty.jsp">
	      <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
	      <jsp:param name="sr" value="1" />
	   </jsp:include>
   </td>
   <td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
      <jsp:include page="includes/navright.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="lnr" value="<%= lnRubriekID %>" />
      </jsp:include>
      <jsp:include page="includes/rightcolumn_image.jsp">
         <jsp:param name="a" value="<%= artikelID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
      </jsp:include>
		<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" 
		    fields="artikel.number" orderby="contentrel.pos" directions="up" offset="1">
		   <mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
      		 <jsp:include page="includes/artikel_1_column.jsp">
               <jsp:param name="o" value="<%= artikel_number %>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
               <jsp:param name="f" value="fittothirdcolumn" />
            </jsp:include>
         </mm:field>
   	</mm:list>
   	<jsp:include page="includes/shorty.jsp">
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
</mm:cloud>