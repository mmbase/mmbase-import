<% //actual site
if(iRubriekLayout==NatMMConfig.DEFAULT_LAYOUT) { 
   %>
   	<div style="position:relative; width:100%;">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <%@include file="../includes/top5a_breadcrumbs_vertlogo.jsp" %>
   <%@include file="../includes/top5b_pano_vertlogo.jsp" %>
   <%
} else { //naardermeer
   %>
	<div style="position:relative;    width:744px;">
	<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <%@include file="../includes/top5b_pano.jsp" %>
   <%@include file="../includes/top5a_breadcrumbs.jsp" %>
   </table>
   <%
} %>