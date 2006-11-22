<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<% 
String templatesUrl = request.getParameter("tu");
extendedHref = pageUrl + "&p=bestel&t=view";
%>
  <table width="180" cellspacing="0" cellpadding="0">
			<tr>
				<td width="180">
				<table width="180" cellspacing="0" cellpadding="0">
					<tr>
					<td class="titlebar" width="0%"><img src="media/spacer.gif" width="3" height="1" border="0" alt=""><a href="<%@include file="../includes/extendedhref.jsp" 
						%>"><img src="media/w_wagentje.gif" border="0" alt=""></a></td>
					<td class="titlebar" width="100%" style="vertical-align:middle;padding-right:2px;padding-left:2px;"><nowrap><a href="<%@include
                 file="../includes/extendedhref.jsp"	%>" class="white">
                  <%= totalitemsId %>
                  <% 
                  if(totalitemsId.equals("1")) {
							%><bean:message bundle="LEOCMS" key="shoppingcartlink.item" /><%
						} else {
							%><bean:message bundle="LEOCMS" key="shoppingcartlink.items" /><%
						}
						%></a></nowrap></td>
					<td class="titlebar" style="padding:2px;" width="0%"><a href="<%@include file="../includes/extendedhref.jsp" 
						%>"><img src="media/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td width="180" colspan="2"><img src="media/spacer.gif" width="180" height="1" border="0" alt=""></td>
			</tr>
			<tr>
				<td class="subtitlebar" width="180" colspan="2"><bean:message bundle="LEOCMS" key="shoppingcartlink.paragraaf.titel" /></td>
			</tr>
		</table>
</mm:cloud>
