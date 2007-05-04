<!-- FIX APPLIES div to contain - placeholder -->
<div style="position:relative;  width:744px;">
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<% 
if(iRubriekLayout==NatMMConfig.DEFAULT_LAYOUT) { 
   %>
   <%@include file="../includes/top5a_breadcrumbs_vertlogo.jsp" %>
   <%@include file="../includes/top5b_pano_vertlogo.jsp" %>
   <%
} else {
   %>
   <%@include file="../includes/top5b_pano.jsp" %>
   <%@include file="../includes/top5a_breadcrumbs.jsp" %>
   </table>
   <%
} %>