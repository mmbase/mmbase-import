<%-- warning on using two times the same question should be added --%>
<% boolean timeFieldIsOpen = false;
%><form name="formulier" method="post"> 
<table cellpadding="0" cellspacing="0" align="left">
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3"><img src="media/spacer.gif" width="400" height="1">
      <div align="right" style="letter-spacing:1px;"><a href="javascript:history.go(-1);">terug</a></div>
		<%@include file="../relatedteaser.jsp" %></td>
	</tr>		
	<mm:list nodes="<%= pageId %>" path="pagina,posrel,formulier" 
		fields="formulier.number,formulier.titel,formulier.titel_fra,formulier.omschrijving" 
		orderby="posrel.pos" directions="UP">
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan=3>
			<div class="pageheader"><mm:field name="formulier.titel" 
			   /></div><br/>
			<mm:field name="formulier.titel_fra"/><br>
			<mm:field name="formulier.omschrijving"/><br><br><br>
		</td>
	</tr>

	<% String formulier_number = ""; %>
	<mm:field name="formulier.number" jspvar="dummy" vartype="String" write="false">
			<% formulier_number= dummy; %>
	</mm:field>

	<mm:list nodes="<%= formulier_number %>" path="formulier,posrel,formulierveld"
		fields="formulierveld.number,formulierveld.label,formulierveld.type"
		orderby="posrel.pos" directions="UP">

	<% String questions_type = ""; %>
	<mm:field name="formulierveld.type" jspvar="dummy" vartype="String" write="false">
		<% questions_type = dummy; %>
	</mm:field>
	<% boolean isRequired = false; %>
	<mm:field name="formulierveld.verplicht" jspvar="dummy" vartype="String" write="false">
			<% isRequired = dummy.equals("1"); %>
	</mm:field>
	<% String questions_number = ""; %>
	<mm:field name="formulierveld.number" jspvar="dummy" vartype="String" write="false">
			<% questions_number= dummy; %>
	</mm:field>

<%-- radio buttons or checkboxes --%>

	<% if(questions_type.equals("4")||questions_type.equals("5")) { %>
		<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan=3>
			<mm:field name="formulierveld.label" />
			<% if(isRequired) { %> (*) <% } %>
		</td></tr>
		<mm:list nodes="<%= questions_number %>" path="formulierveld,posrel,formulierveldantwoord"
			fields="formulierveldantwoord.waarde,formulierveldantwoord.number"
			orderby="posrel.pos" directions="UP">
			<tr>
				<td><img src="media/spacer.gif" width="10" height="1"></td>
				<td>
					<% if(questions_type.equals("4")) { %>
						<input type="radio" name="q<%= questions_number %>" value="<mm:field name="formulierveldantwoord.waarde" />">
					<% } else if(questions_type.equals("5")) { %>
						<input type="checkbox" name="q<%= questions_number %>_<mm:field name="formulierveldantwoord.number" />" value="<mm:field name="formulierveldantwoord.waarde" />">
					<% } %>	
				</td>
				<td colspan="2">
					<mm:field name="formulierveldantwoord.waarde" />
				</td>
			</tr>
		</mm:list>
	<% } %>

<%-- dropdown --%>

	<% if(questions_type.equals("3")) { %>
		<mm:field name="formulierveld.label" jspvar="questions_title" vartype="String" write="false">

		<%-- do something special for question sequences with day, month, year in their title --%>
		<% if(questions_title.equals("Dag")||questions_title.equals("Maand")||questions_title.equals("Jaar")||questions_title.equals("U")||questions_title.equals("M")) { 
			if(!timeFieldIsOpen) {%>
				<tr>	
					<td><img src="media/spacer.gif" width="10" height="1"></td>
					<td colspan=3>
						<table><tr>
					<% timeFieldIsOpen = true;
			} %>
							<td><%= questions_title %><% if(isRequired) { %> (*) <% } %><br>
								<select name="q<%= questions_number %>">
								<option>...
								<mm:list nodes="<%= questions_number %>" path="formulierveld,posrel,formulierveldantwoord"
									fields="formulierveldantwoord.waarde"
									orderby="posrel.pos" directions="UP">
									<option value="<mm:field name="formulierveldantwoord.waarde" />"><mm:field name="formulierveldantwoord.waarde" />
								</mm:list>
							</select>
							</td>
		<% } else { 
			if(timeFieldIsOpen) {%>
							</tr></table>
					</td></tr>
					<% 	timeFieldIsOpen = false;
		} %>
				<tr>	
					<td><img src="media/spacer.gif" width="10" height="1"></td>
					<td colspan=3>
					<mm:field name="formulierveld.label" />
					<% if(isRequired) { %> (*) <% } %>
				</td></tr>
				<tr>	
					<td><img src="media/spacer.gif" width="10" height="1"></td>
					<td colspan="3">
					<select name="q<%= questions_number %>">
					<option>...
					<mm:list nodes="<%= questions_number %>" path="formulierveld,posrel,formulierveldantwoord"
						fields="formulierveldantwoord.waarde"
						orderby="posrel.pos" directions="UP">
						<option value="<mm:field name="formulierveldantwoord.waarde" />"><mm:field name="formulierveldantwoord.waarde" />
					</mm:list>
					</select>
				</td></tr>
		<% } %>
		</mm:field>
	<% } %>

<%-- textarea and textline --%>

	<% if(questions_type.equals("1")||questions_type.equals("2")) { %>
		<tr>
			<td><img src="media/spacer.gif" width="10" height="1"></td>
			<td colspan=3>
			<mm:field name="formulierveld.label" />
			<% if(isRequired) { %> (*) <% } %>
		</td></tr>
		<tr>
			<td><img src="media/spacer.gif" width="10" height="1"></td>
			<td colspan="3">
			<% if(questions_type.equals("1")) { %>
				<textarea rows="3" cols="52" name="q<%= questions_number %>" wrap="physical"><%= sDefaultText %></textarea>
			<% } else { %>
				<input type="text" name="q<%= questions_number %>" size="50" 
				   value="<mm:first><%= sDefaultName %></mm:first><mm:first inverse="true"><%= sDefaultEmail %></mm:first>">
			<% } %>
		</td></tr>
	<% } %>

	</mm:list>	<%-- article,questions --%>


	<%if(timeFieldIsOpen) {%>
					</tr></table>
			</td></tr>
			<% 	timeFieldIsOpen = false;
	} %>
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3"><img src="media/spacer.gif" width="1" height="10"></td>
	</tr>
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3"><div align="right">
		<a href="formulier.jsp?<%= request.getQueryString() %>"
			onClick="return createPosting(this);"
			>verstuur je bericht</a><img src="media/spacer.gif" width="10" height="1"></div>
		</td>
	</tr>
	</mm:list>	<%-- pagina, formulier --%>
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3"><img src="media/spacer.gif" width="1" height="10"></td>
	</tr>
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3">
		(*) vul minimaal deze velden in i.v.m. een correcte afhandeling.
		</td>
	</tr>
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3"><img src="media/spacer.gif" width="1" height="10"></td>
	</tr>
</table>
</form>