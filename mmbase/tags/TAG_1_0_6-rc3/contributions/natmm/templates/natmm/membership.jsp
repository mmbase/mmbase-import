<%@page import="nl.leocms.forms.MembershipForm" %>
<%@include file="includes/top0.jsp" %>
<%
// *** use paginaID from session to return to the last visited membership page (in case of redirect from Struts form) ***
if(paginaID.equals("-1") && session.getAttribute("pagina")!=null) { 
   paginaID = (String) session.getAttribute("pagina");
} %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<% session.setAttribute("pagina",paginaID); %><%
String referer =  request.getHeader("referer");
if(referer!=null) { session.setAttribute("form_referer",referer); }
%>
<%@include file="includes/top2_cacheparams.jsp" %>
<%--cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application"--%>
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp"%>
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
      <jsp:include page="includes/membership/form.jsp">
         <jsp:param name="p" value="<%= paginaID %>" />
      </jsp:include>
	</td>
	<td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
		<bean:define id="actionId" property="action" name="MembershipForm" scope="session" type="java.lang.String"/> 
      <% 
      if ((actionId.equals(MembershipForm.initAction)) || (actionId.equals(MembershipForm.correctAction))) { 
         %>
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
         <jsp:include page="includes/shorty.jsp">
   	      <jsp:param name="s" value="<%= paginaID %>" />
   	      <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
   	      <jsp:param name="sr" value="2" />
   	   </jsp:include>
         <% 
      } %>
      <img src="media/trans.gif" height="1px" width="165px;" />
	</td>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
<%--</cache:cache>--%>
</mm:cloud>