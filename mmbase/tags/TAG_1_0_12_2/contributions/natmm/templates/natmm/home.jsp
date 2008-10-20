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
  
<div id="skyscraper" style="visibility: hidden; position:absolute; left:-625px; top:-700px; width:2000; height:2000; color:#000000; z-index:101;">
   <div style="position: absolute; left: 1220px; top:722px; z-index:102; display: block;">
      <img src="flash/cross.gif" onclick="closeSkyscraper();"/>
   </div>
   <object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" width="2000" height="2000" id="overlayerbanner_pop_600x600" align="middle">
      <param name="allowScriptAccess" value="sameDomain" />
      <param name="movie" value="flash/080923_hart_v1.swf?clickTag=http://www.natuurmonumenten.nl&clickTarget=_blank" />
      <param name="menu" value="false" />
      <param name="quality" value="high" />
      <param name="scale" value="noscale" />
      <param name="wmode" value="transparent" />
      <param name="bgcolor" value="#ffffff" />
      <embed src="flash/080923_hart_v1.swf?clickTag=http://www.natuurmonumenten.nl&clickTarget=_blank" menu="false" quality="high" scale="noscale" wmode="transparent" bgcolor="#ffffff" width="2000" height="2000" name="overlayerbanner_pop_600x600" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
   </object>
</div>
  
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



