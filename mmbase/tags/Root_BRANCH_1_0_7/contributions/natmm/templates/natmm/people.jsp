<% // *** article + articles in middle and left column *** %>
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

  <% if (isNaardermeer.equals("true")) { %>		
   	<div style="position:absolute; left:681px; width:70px; height:216px; background-image: url(media/natmm_logo_rgb2.gif); background-repeat:no-repeat;"></div>
  <% } %>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
   <td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
   <br/>
   <%@include file="includes/navleft.jsp" %>
   <br/>
   <jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="0" />
    </jsp:include>
    </td>
    <td style="width:374;vertical-align:top;padding:10px;padding-top:0px;">
      <br/>
      <%@include file="includes/page_intro.jsp" %>
      <% if(personID.equals("-1")) { %>
            <jsp:include page="includes/people/summary.jsp">
               <jsp:param name="p" value="<%= paginaID %>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
            </jsp:include>
      <% } else { %>
            <jsp:include page="includes/people/detail1.jsp">
               <jsp:param name="pers" value="<%= personID %>" />
            </jsp:include>
            <jsp:include page="includes/shorty.jsp">
               <jsp:param name="s" value="<%= paginaID %>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
               <jsp:param name="sr" value="1" />
            </jsp:include>
            <div style="margin-top:8px;margin-bottom:8px;">
               <a href="javascript:history.go(-1);"><img src="media/buttonleft_<%= NatMMConfig.style1[iRubriekStyle] %>.gif" border="0"/></a>
               <a href="javascript:history.go(-1);">Terug naar Smoelenboek</a>
            </div>
      <% } %>
      <img src="media/trans.gif" height="1px" width="352px;" />
   </td>
   <td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
      <% if (isNaardermeer.equals("true")) { %>			
   		<img src="media/trans.gif" height="226" width="1">
	  <% } %>	
	<br/>
	<% if(!personID.equals("-1")) { %>
            <jsp:include page="includes/people/detail2.jsp">
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="pers" value="<%= personID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
            </jsp:include>
      <% } else { 
         %>
         <jsp:include page="includes/navright.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="lnr" value="<%= lnRubriekID %>" />
         </jsp:include>
         <%
      } %>
      <img src="media/trans.gif" height="1px" width="165px;" />
   </td>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
