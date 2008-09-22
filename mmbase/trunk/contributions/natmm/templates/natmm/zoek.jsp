<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<!-- cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" -->
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>

<%-- Any template calling others need to pass isNaardermeer as PaginaHelper/mm:import fails--%>
<%request.setAttribute("isNaardermeer", isNaardermeer);%>

<mm:locale language="nl">
<!-- /cache:cache -->
<%
String sQuery = request.getParameter("query");
if(sQuery==null) { sQuery = ""; }
String sQueryForm = request.getParameter("query_frm");
if(sQueryForm==null) { sQueryForm = ""; }

if(!sQueryForm.equals("")&&!sQueryForm.equals(sQuery)) {
   sQuery = sQueryForm;
}
%>
  <% if (isNaardermeer.equals("true")) { %>		
   	<div style="position:absolute; left:681px; width:70px; height:216px; background-image: url(media/natmm_logo_rgb2.gif); background-repeat:no-repeat;"></div>
  <% } %>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
   <td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
   <br/>
   <%@include file="includes/navleft.jsp" %>
   <br>
	<jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="0" />
   </jsp:include>
   </td>
   <td style="vertical-align:top;width:374px;padding:10px;padding-top:0px">
	  <br/>
	  <mm:import id="nodotline" />
      <%@include file="includes/page_intro.jsp" %>
      <jsp:include page="includes/zoek/resultaten.jsp" flush="true">
         <jsp:param name="query" value="<%= sQuery %>"/>
         <jsp:param name="root" value="<%= subsiteID %>"/>
         <jsp:param name="maxresults" value="100"/>
         <jsp:param name="paginaID" value="<%= paginaID %>"/>
      </jsp:include>
   </td>
   <td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
 	 <% if (isNaardermeer.equals("true")) { %>			
   		<img src="media/trans.gif" height="226" width="1">
	  <% } %>	  
	  <br/>
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
      <img src="media/trans.gif" height="1px" width="165px;" />
   </td>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</mm:locale>
</mm:cloud>