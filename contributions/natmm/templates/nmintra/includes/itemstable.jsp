<table cellpadding="0" cellspacing="0" border="0">
	<mm:list nodes="<%= paginaID %>" path="pagina,posrel,linklijst" fields="linklijst.number"
		orderby="lijstcontentrel.pos,contentrel.number" directions="UP,DOWN" offset="<%= "" + thisOffset*10 %>" max="10"
	><mm:field name="lijstcontentrel.pos" jspvar="contentrel_pos" vartype="String" write="false"><%	
         if(contentrel_pos.equals("")) contentrel_pos = "1";
			if(((Integer.parseInt(contentrel_pos)-1) % numberOfColumns) == (colNumber-1)){ 
			%><mm:field name="linklijst.number" jspvar="items_number" vartype="String" write="false"
			><mm:node number="<%= items_number %>"
			><tr>
				<td rowspan="8" ><div align="center">
					<mm:relatednodes type="images"
						><a target="_blank" href="<mm:image />">
							<% imageTemplate = "+s(100)"; 
							%><img src=<%@include file="../includes/imagessource.jsp" 
								%> alt="Klik op het plaatje om het artikel te lezen" border="0"><br>
								<% imageTemplate = ""; 
						%></a>
					</mm:relatednodes
					><mm:import id="noattachmenttitle" 
					/><%@include file="../includes/attachment.jsp" 
					%><mm:remove referid="noattachmenttitle"
				/></div>
				</td>
				<td rowspan="8" ><img src="media/spacer.gif" width="10" height="1"></td>
				<td ><img src="media/spacer.gif" width="1" height="1"></td>
			</tr>
			<tr>
				<td>
					<div class="bold">
					<mm:field name="titel" jspvar="items_name" vartype="String" write="false"
						><% int max_length = items_name.indexOf(" ",20); 
						%><% if(max_length==-1) max_length = items_name.length(); 
						%><%= items_name.substring(0,max_length) %>...
					</mm:field
					></div>
					<mm:field name="omschrijving" />
				</td>
			</tr>
			</tr>
			<tr>
				<td>
					<mm:relatednodes type="programs"
						>programma: <mm:field name="title" 
					/></mm:relatednodes>
				</td>
			</tr>
			<tr>
				<td>
					<mm:relatednodes type="companies"
						>omroep: <mm:field name="name" 
					/></mm:relatednodes>
				</td>
			 </tr>
			<tr>
				<td>
					<mm:relatednodes type="mmevents"
						><mm:field name="start" id="startdate" write="false"
							/>publicatiedatum: <mm:time format="d-M-yyyy" referid="startdate" 
						/><mm:remove referid="startdate" 
					/></mm:relatednodes>
				</td>
		 	</tr>
			<tr>
			 	<td><img src="media/spacer.gif" width="1" height="25"></td>
			</tr>
			</mm:node
			></mm:field
		><% } 
	%></mm:field
	></mm:list
></table>
