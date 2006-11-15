<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<% String templatesUrl = request.getParameter("tu");%>
<br />
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
	<td width="180"><img src="media/spacer.gif" width="180" height="1" border="0" alt=""></td>
	<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<td width="100%"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
	<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<td width="100%"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
	<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<td width="180"><img src="media/spacer.gif" width="180" height="1" border="0" alt=""></td>
</tr>
<tr>
	<td width="100%" height="100%" colspan="3">???</td>
	<td rowspan="2"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<td rowspan="2" align="right">
		<jsp:include page="../includes/phonelink.jsp">
			 <jsp:param name="tu" value="<%= templatesUrl %>" />
		 </jsp:include></td>
	<td rowspan="2"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<td rowspan="2">
			<jsp:include page="shoppingcart/link.jsp">
				<jsp:param name="p" value="<%= paginaID %>" />
				<jsp:param name="tu" value="<%= templatesUrl %>" />
			</jsp:include></td>
</tr>
<tr>
	<td class="bottom" colspan="3">
		<table width="100%" height="1" cellspacing="0" cellpadding="0">
			<tr><td width="100%" height="1" class="subtitlebar"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td></tr>
		</table>
	</td>
</tr>
<tr>
  <td>
    <table width="100%" cellspacing="0" cellpadding="0">
      <jsp:include page="includes/header.jsp">
        <jsp:param name="tu" value="<%= templatesUrl %>" />
        <jsp:param name="t" value="" />
        <jsp:param name="p" value="<%= paginaID %>" />
      </jsp:include>
      <tr>
         <td width="180">
              <jsp:include page="includes/welcome.jsp">
              <jsp:param name="p" value="<%= paginaID %>" />
            </jsp:include> 
             <jsp:include page="includes/searchlink.jsp">
                <jsp:param name="tu" value="<%= templatesUrl %>" />
            </jsp:include>
            <jsp:include page="includes/nav.jsp">
                  <jsp:param name="tu" value="<%= templatesUrl %>" />
            </jsp:include>
         </td>
         <td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
</mm:cloud>