<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
</cache:cache>
<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<%
if (iRubriekLayout!=NatMMConfig.DEMO_LAYOUT) {
   %>
   <%@include file="includes/top5a_breadcrumbs.jsp" %>
   <%
} else {
   %>
   <%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
   <%
} %>
</table>
<mm:import id="url">http://natmmww.asp4all.nl/</mm:import>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,link" max="1">
   <mm:field name="link.url" jspvar="link_url" vartype="String" write="false">
      <%
      link_url += (link_url.indexOf("?")==-1 ? "?" : "&" ) + "rnd=" + (Math.random()*1000)%1000;
      %>
      <mm:import id="url" reset="true"><%= link_url %></mm:import>
   </mm:field>
</mm:list>
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
   <tr>
      <td align="center">
      <iframe src="<mm:write referid="url" />"  width="744" height="100%" scrolling="yes" frameborder="0"></iframe>
      </td>
    </tr>
</table>
<table align="center" width="100%" height="100%">
</table>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
