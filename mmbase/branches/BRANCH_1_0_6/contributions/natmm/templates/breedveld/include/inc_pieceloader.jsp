<%	int imageCounter = 0;
	String [] listLiggend =  new String[100];
	String imageConstraint = "(items.titel_zichtbaar='1') AND (images.screensize='Liggend')";
	imageCounter = 0;	
%>
	<mm:list path="items,posrel,images" fields="items.number,images.number"
		orderby="items.year" directions="DOWN"
		constraints="<%= imageConstraint %>">
		<mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
			<% listLiggend[imageCounter] = images_number; %>
		</mm:field>	
		<% imageCounter++; %>
	</mm:list>
<%	listLiggend[imageCounter] = "-1"; %>

<%	String [] listStaand =  new String[100];
	imageConstraint = "(items.titel_zichtbaar='1') AND (images.screensize='Staand')";
	imageCounter = 0;	
%>
	<mm:list path="items,posrel,images" fields="items.number,images.number"
		orderby="items.year" directions="DOWN"
		constraints="<%= imageConstraint %>">
		<mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
			<% listStaand[imageCounter] = images_number; %>
		</mm:field>	
		<% imageCounter++; %>
	</mm:list>
<%	listStaand[imageCounter] = "-1"; %>
