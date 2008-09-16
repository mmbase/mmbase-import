<% String formTitle = request.getParameter("ft"); 
	String formMessage = request.getParameter("fm"); 
	String formMessageHref = request.getParameter("fmh"); 
	String formMessageLinktext = request.getParameter("fmlt"); 
%>
<center>
<table cellspacing="10" cellpadding="0" style="width:400px;">
	<tr>
		<td>
			<b><%= formTitle %></b><br>
			<%= formMessage %><br><br><br>
		</td>
	</tr>
	<tr>
		<td>
		   <table width="180" cellspacing="0" cellpadding="0">
            <tr>
            <td class="titlebar" style="vertical-align:middle;padding-left:4px;padding-right:2px;" width="100%">
            	<nowrap><a href="<%= formMessageHref %>" class="white"><%= formMessageLinktext %></a></td>
            <td class="titlebar" style="padding:2px;" width="100%">
            	<a href="<%= formMessageHref %>"><img src="media/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
            </tr>
      	</table>	
		</td>
	</tr>
</table>
</center>