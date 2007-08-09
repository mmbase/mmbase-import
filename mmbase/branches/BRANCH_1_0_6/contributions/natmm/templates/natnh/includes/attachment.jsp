<mm:relatednodes type="attachments">
	<table>
	<tr><td>
	<mm:field name="filename" jspvar="attachments_filename" vartype="String" write="false">
		<a href="<mm:attachment />">
			<% if(attachments_filename.indexOf(".pdf")>-1){ %> <img src="media/pdf.gif" alt="" border="0">
			<% } else if(attachments_filename.indexOf(".doc")>-1){ %> <img src="media/word.gif" alt="" border="0">
			<% } else if(attachments_filename.indexOf(".xls")>-1){ %> <img src="media/xls.gif" alt="" border="0">
			<% } else if(attachments_filename.indexOf(".ppt")>-1){ %> <img src="media/ppt.gif" alt="" border="0">
			<% } else { %> download <% } %>
		</a>
	</mm:field>
	</td></tr>
	<tr><td><mm:notpresent referid="noattachmenttitel"><mm:field name="titel" /></mm:notpresent
	></td></tr>
	</table>
</mm:relatednodes>
