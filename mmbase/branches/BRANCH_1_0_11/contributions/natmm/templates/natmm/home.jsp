<% // *** one article, with shorties and teasers  *** %>
<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>

<%-- Any template calling others need to pass isNaardermeer as PaginaHelper/mm:import fails--%>
<%request.setAttribute("isNaardermeer", isNaardermeer);%>

<% if(artikelID.equals("-1")) { %>
   <mm:list nodes="<%=paginaID%>" path="pagina,contentrel,artikel" fields="artikel.number" orderby="contentrel.pos" directions="up" max="1">
   	<mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
   		<% artikelID = artikel_number;%>
   	</mm:field>
   </mm:list><%
} %>
  <% if (isNaardermeer.equals("true")) { %>		
   	<div style="position:absolute; left:681px; width:70px; height:216px; background-image: url(media/natmm_logo_rgb2.gif); background-repeat:no-repeat;"></div>
  <% } %>
  
<div id="bigdiv" style="position:relative;">
  
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
	<td style="vertical-align:top;width:185px;padding:10px;padding-top:0px;">
	<br/>
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
	<br/>
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
   <td style="vertical-align:top;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
      <% if (isNaardermeer.equals("true")) { %>			
   		<img src="media/trans.gif" height="226" width="1">
	  <% } %>
	  <br/>	
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

</div>

<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>



