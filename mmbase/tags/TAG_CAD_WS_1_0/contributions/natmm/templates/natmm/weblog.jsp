<% // *** one article, with shorties and teasers  *** %>
<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<%
// In this page we need to override the Cache parameter expireTime that we had in standart include top2_cacheparams.jsp
// We are enforcing the fresh loading of the weblog pages. This enables back to webcam link display properly [NMCMS-128]
expireTime = 0;
%>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>

<%-- Any template calling others need to pass isNaardermeer as PaginaHelper/mm:import fails--%>
<%request.setAttribute("isNaardermeer", isNaardermeer);%>

<mm:node number="<%= paginaID %>">
<%@include file="actie/includes/navsettings.jsp" %>
  <% if (isNaardermeer.equals("true")) { %>		
   	<div style="position:absolute; left:681px; width:70px; height:216px; background-image: url(media/natmm_logo_rgb2.gif); background-repeat:no-repeat;"></div>
  <% } %>
<% if(artikelID.equals("-1")) {
   String articleConstraint = (new SearchUtil()).articleConstraint(nowSec, quarterOfAnHour);
%>
	<mm:relatednodes type="artikel" path="contentrel,artikel" orderby="begindatum" directions="down" constraints="<%= articleConstraint %>" max="1">
       <mm:field name="number" jspvar="artikel_number" vartype="String" write="false">
          <% artikelID = artikel_number;%>
       </mm:field>
    </mm:relatednodes><%
} %>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
	<td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
   	<br>
   	<%@include file="includes/navleft.jsp" %>
      <br />
   	<jsp:include page="includes/teaser.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="0" />
      </jsp:include>
	</td>
	<td style="vertical-align:top;width:100%;padding-left:10px;padding-right:10px;text-align:right;">
		<br/>
	   <jsp:include page="includes/artikel_12_column.jsp">
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="lnr" value="<%= lnRubriekID %>" />
         <jsp:param name="rnimageid" value="<%= rnImageID %>" />
         <jsp:param name="p" value="<%= paginaID %>" />
         <jsp:param name="a" value="<%= artikelID %>" />
         <jsp:param name="showpageintro" value="true" />
		<jsp:param name="shownav" value="true" />
         <jsp:param name="isNaardermeer" value="<%=isNaardermeer %>" />
      </jsp:include>
	</td>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</mm:node>
</cache:cache>
</mm:cloud>