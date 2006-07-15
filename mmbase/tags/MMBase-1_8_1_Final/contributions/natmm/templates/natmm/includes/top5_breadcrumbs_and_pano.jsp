<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<% 
if(iRubriekLayout==NatMMConfig.DEFAULT_LAYOUT) { 
   %>
   <%@include file="../includes/top5a_breadcrumbs.jsp" %>
   <%@include file="../includes/top5b_pano.jsp" %>
   <%
} else {
   %>
   <%@include file="../includes/top5b_pano.jsp" %>
   <%@include file="../includes/top5a_breadcrumbs.jsp" %>
   <%
} %>
</table>