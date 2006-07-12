<%	int imageCounter = 0;
	String [] listLiggend =  new String[100];
	String imageConstraint = "images.layout='Liggend' AND images.quality='A-keuze'";
	imageCounter = 0;	
%>
	<mm:list path="items" fields="items.number"
		orderby="piece.year" directions="DOWN"
		constraints="items.titel_zichtbaar='1'">
		<mm:node element="items">
			<mm:related path="posrel,images"
				fields="images.number"
				constraints="<%= imageConstraint %>">
				<mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
					<% listLiggend[imageCounter] = images_number; %>
				</mm:field>	
				<% imageCounter++; %>
			</mm:related>
		</mm:node>
	</mm:list>
<%	listLiggend[imageCounter] = "-1"; %>

<%--	String [] listStaand =  new String[100];
	imageConstraint = "images.layout='Staand' AND images.quality='A-keuze'";
	imageCounter = 0;	
%>
	<mm:list path="items" fields="items.number"
		orderby="items.year" directions="DOWN"
		constraints="items.titel_zichtbaar='1'">
		<mm:node element="items">
			<mm:related path="posrel,images"
				fields="items.titel,images.number"
				orderby="items.year" directions="DOWN"
				constraints="<%= imageConstraint %>">
				<mm:field name="images.number" jspvar="images_number" vartype="String" write="false">
					<% listStaand[imageCounter] = images_number; %>
				</mm:field>	
				<% imageCounter++; %>
			</mm:related>
		</mm:node>
	</mm:list->
<%	listStaand[imageCounter] = "-1"; --%>
