<mm:field name="images.number" jspvar="images_number" vartype="String" write="false"
	><mm:node number="<%= images_number %>"><% 
	if(imageTemplate.equals("")) { 
		%><mm:image /><% 
	} else { 
		%><mm:image template="<%= imageTemplate %>" /><% 
	} %></mm:node
></mm:field><% imageTemplate = ""; %>