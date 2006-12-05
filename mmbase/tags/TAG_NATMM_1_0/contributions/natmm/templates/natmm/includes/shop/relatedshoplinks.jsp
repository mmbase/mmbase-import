<mm:remove referid="isfirst"
/><mm:import id="isfirst"
/><img src="media/spacer.gif" height="1" width="180" border="0" alt=""><br>
<table width="100%" cellspacing="0" cellpadding="0">
<tr><td style="padding:4px;padding-top:14px;"><%
	if(products.size()>0) {
		%><bean:message bundle="LEOCMS" key="relatedshoplinks.submit_order" /><%
	} 
	%><bean:message bundle="LEOCMS" key="relatedshoplinks.continue_shopping" />
	<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel"
		constraints="contentrel.pos=3">
		<mm:node element="artikel"
	  ><%@include file="relatedlinks.jsp" 
	%></mm:node
	></mm:list>
</td></tr>
</table>