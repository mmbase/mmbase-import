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

<br/>
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
	<td style="width:559px;vertical-align:top;padding:10px;padding-top:0px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
		<table width="539px;" cellspacing="0" cellpadding="0">
			<tr>
			<td style="vertical-align:top;width:364px;padding-right:10px">
   		   <mm:import id="nodotline" />
            <%@include file="includes/page_intro.jsp" %>
			</td>
			<td style="vertical-align:top;padding-left:10px;width:175px;">
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
		<table class="dotline" style="width:539px;"><tr><td height="3"></td></tr></table>
		<mm:list  nodes="<%=paginaID%>"  path="pagina,posrel,linklijst,lijstcontentrel,link" orderby="lijstcontentrel.pos,link.titel"
		    fields="link.titel,link.url,link.alt_tekst">
   		<table width="539px;" cellspacing="2" cellpadding="2">
   			<tr>
   				<td width="150" valign="top">
   				<a class="maincolor_link" href="<mm:field name="link.url" />" title="<mm:field name="link.alt_tekst" 
   				   />" target="<mm:field name="link.target" />"><mm:field name="link.titel" /></a>
   				</td><td valign="top"><mm:field name="link.omschrijving" /></td>
   			</tr>
   		</table>
   		<table class="dotline" style="width:539px;"><tr><td height="3"></td></tr></table>
		</mm:list>
	</td>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>



