<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
  <%
  String actionId = request.getParameter("t"); if (actionId==null) {actionId=""; }
  
  PaginaHelper ph = new PaginaHelper(cloud);
  String extendedHref = ph.createPaginaUrl("bel",request.getContextPath());
	%>
  <table width="180" cellspacing="0" cellpadding="0">
	<tr>
		<td width="180">
		<table width="180" cellspacing="0" cellpadding="0">
			<tr>
			<td class="maincolor" width="0%"><a href="<%@include file="extendedhref.jsp" 
				%>"><img src="media/shop/telefoon.gif" border="0" alt=""></a></td>
			<td class="maincolor" style="vertical-align:middle;padding-right:2px;" width="100%"><nowrap><a href="<%@include file="extendedhref.jsp" 
				%>" class="klikpad"><b><bean:message bundle="LEOCMS" key="shop.phonelink.phonenumber" /></b></a></nowrap></td>
			<td class="maincolor" style="padding:2px;" width="100%"><a href="<%@include file="extendedhref.jsp" 
				%>"><img src="media/shop/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td width="180" colspan="2"><img src="media/trans.gif" height="1" width="180" border="0" alt=""></td>
	</tr>
	<tr>
		<td class="subtitlebar" width="180" colspan="2"><bean:message bundle="LEOCMS" key="shop.phonelink.order_by_phone" /></td>
	</tr>
</table>
</mm:cloud>