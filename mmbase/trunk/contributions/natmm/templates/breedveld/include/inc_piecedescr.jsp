<% itemsDescription = ""; %>
<mm:list nodes="<%= imageId %>" path="images,posrel,items"
	fields="images.title,items.titel,items.number,items.year,items.piecesize,items.material">
	<mm:field name="images.title" jspvar="images_title" vartype="String" write="false">
		<%	itemsDescription += images_title; %>
	</mm:field>
	<mm:field name="items.titel" jspvar="items_title" vartype="String" write="false">
		<% if(items_title.indexOf("Zonder titel")>-1) { 
			itemsDescription +=  "\n" + lan(language,"Zonder titel") + items_title.substring(12);
		 } else {
			itemsDescription +=  "\n" + items_title;
		 } %>
	</mm:field>
	<mm:field name="items.piecesize" jspvar="items_piecesize" vartype="String" write="false">
		<%	itemsDescription += "-" + items_piecesize; %>
	</mm:field>				
	<mm:field name="items.material" jspvar="items_material" vartype="String" write="false">
		<% if(items_material.indexOf("olieverf op linnen")>-1) { 
				itemsDescription += "-" + lan(language,"olieverf op linnen");
			} else if(items_material.indexOf("tempera op papier")>-1) { 
				itemsDescription += "-" + lan(language,"tempera op papier");
			} else {
				itemsDescription += "-" + items_material;
			} %>
	</mm:field>
	<mm:node element="items">
		<mm:related path="stock,organisatie,posrel,organisatie_type"
			constraints="organisatie_type.naam='Collectie' OR organisatie_type.naam='Privecollectie'"
			fields="organisatie.naam">
			<mm:field name="organisatie.naam" jspvar="contact_companyname" vartype="String" write="false">
				<% itemsDescription += "\n" + lan(language,"Collectie") + " " + contact_companyname; %>
			</mm:field>
		</mm:related>
	</mm:node>
</mm:list>