<mm:node number="<%= educationId %>">
   <%
   String sProvidersName = "Intern"; 
   String sSubsidie = "";
   %>
   <mm:relatednodes type="providers">
   	<mm:field name="funding" jspvar="providers_funding" vartype="String" write="false">
   	 	<% 
         if (providers_funding.equals("1")) {
   	 	   sSubsidie = "<a href=\"educations.jsp?p=subsidie\">Ja</a>";
   	   } else {
            sSubsidie = "Geen subsidie";
         }
         %>
   	</mm:field>
   	<mm:field name="naam" jspvar="dummy" vartype="String" write="false">
   		<% sProvidersName = dummy; %>
   	</mm:field>
   </mm:relatednodes>
   <span class="black">
   <table cellpadding="5" cellspacing="0" style="border:solid #000000 1px;border-collapse:collapse;width:100%;">
   	<tr>
   		<td class="solid" style="width:150px;">opleiding</td>
   		<td class="solid"><mm:field name="titel"/></td>
   	</tr>
		<mm:related path="afdelingen">
			<tr>
				<td class="solid" style="width:150px;">afdeling</td>
				<td class="solid"><mm:field name="afdelingen.naam"/></td>
			</tr>
		</mm:related>	
   	<mm:field name="content" jspvar="sContent" vartype="String" write="false">
			<tr>
				<td class="solid" style="width:150px;">inhoud</td>
				<td class="solid"><%= sContent %></td>
			</tr>
   	</mm:field>	
		<mm:field name="targetgroup">
			<tr>
				<td class="solid" style="width:150px;">doelgroep</td>
				<td class="solid"><mm:write/></td>
			</tr>
		</mm:field>
		<mm:field name="goal">
			<tr>
				<td class="solid" style="width:150px;">leerdoel</td>
				<td class="solid"><mm:write/></td>
			</tr>
		</mm:field>
		<mm:relatednodes type="competencies">
			<mm:first>
				<tr><td class="solid" style="width:150px;">competenties</td><td class="solid">
			</mm:first>
			<a href="article.jsp?p=competenties#c<mm:field name="number"/>"
            title="<mm:field name="description" jspvar="sDescr" vartype="String" write="false"><%= HtmlCleaner.cleanText(sDescr.replaceAll("'",""),"<",">","") %></mm:field>"><mm:field name="name"/></a>
			<mm:last inverse="true">
				<br/>
			</mm:last>
			<mm:last>
				</td></tr>
			</mm:last>
		</mm:relatednodes>
		<mm:field name="duration">
			<tr>
				<td class="solid" style="width:150px;">duur</td>
				<td class="solid"><mm:write/></td>
			</tr>
		</mm:field>
		<mm:field name="weight">
			<tr>
				<td class="solid" style="width:150px;">studielast&nbsp;/&nbsp;huiswerk</td>
				<td class="solid"><mm:write/></td>
			</tr>
		</mm:field>
	   <mm:field name="dates" jspvar="sDates" vartype="String" write="false">
			<tr>
				<td class="solid" style="width:150px;">cursusdata en tijdstippen</td>
				<td class="solid"><%= sDates %></td>
			</tr>
   	</mm:field>	
		<mm:field name="location">
			<tr>
				<td class="solid" style="width:150px;">cursus locatie(s)</td>
				<td class="solid"><mm:write/></td>
			</tr>
		</mm:field>
	   <tr>
			<td class="solid" style="width:150px;">instituut</td>
			<td class="solid"><%= sProvidersName %></td>
		</tr>
	 	<tr>	
			<td class="solid" style="width:150px;">subsidie</td>
			<td class="solid"><%= sSubsidie %></td>
		</tr>
		<tr>				
	   	<td class="solid" style="width:150px;"><nobr>meer informatie</nobr></td>
   		<td class="solid">
            <mm:relatednodes type="medewerkers" max="1">
            	<mm:relatednodes type="images">
   				   <table border="0" cellpadding="0" cellspacing="0" align="right">
                  <tr><td>
   						<img src="<mm:image template="s(100)" />" border="0" >
   			      </td></tr>
                  </table>
				   </mm:relatednodes>
               <mm:field name="firstname"/> <mm:field name="suffix"/> <mm:field name="lastname"/><br/>
               <a href="mailto:<mm:field name="email"/>"><mm:field name="email"/></a><br/>
               <mm:field name="companyphone"/><br/>
				</mm:relatednodes>
            <mm:relatednodes type="providers">
               <mm:field name="website" jspvar="dummy" vartype="String" write="false">
                  <mm:isnotempty><a href="<%= ( dummy.indexOf("http://")==-1 ? "http://" : "") %><mm:write />" target="_blank"><mm:write /></a><br/></mm:isnotempty>
               </mm:field>
               <mm:field name="email"><mm:isnotempty><a href="mailto:<mm:write />"><mm:write /></a><br/></mm:isnotempty></mm:field>
               <mm:field name="telefoonnummer"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
            </mm:relatednodes>
            <mm:field name="text" jspvar="sText" vartype="String" write="false">
         		<% 
               if (sText!=null&&!HtmlCleaner.cleanText(sText,"<",">","").trim().equals("")) {
                  %><%= sText %><% 
               } 
               %>
         	</mm:field>	
            <%@include file="../attachment.jsp" %>
			</td>
   	</tr>
		<mm:field name="subscribe">
			<tr>
				<td class="solid" style="width:150px;">inschrijven</td>
				<td class="solid"><mm:write/></td>
			</tr>
		</mm:field>
   </table>
	<br/>
	<mm:list nodes="<%= paginaID %>" path="pagina,rolerel,users" orderby="rolerel.pos">
      <mm:first>Een link die niet klopt? email</mm:first>
      <mm:first inverse="true"> of </mm:first>
      <a href="mailto:<mm:field name="users.emailadres" />"><mm:field name="users.emailadres" /></a>
      <mm:last><br/><br/></mm:last>
	</mm:list>
   <a name="feedback"></a>
	Deze cursus gevolgd? <a href="educations.jsp?p=<%= paginaID %>&e=<%= educationId %>&t=feedback">geef uw mening</a><br/><br/>
	<mm:relatednodes type="feedback">
      <mm:first>
     		Feedback van collega's op deze cursus: <br/><br/>
	      <ul>
      </mm:first>
		<li><mm:field name="namesender"/>&nbsp;/&nbsp;
			<mm:field name="begindate" jspvar="begibdate" vartype="String" write="false">
				<% long td = Integer.parseInt(begibdate); td = 1000 * td; Date dd = new Date(td); cal.setTime(dd); %>
            <%= cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR) %>
			</mm:field>
			<br/>
			Opleiding: <mm:field name="topic" /><br/>
			Cursusleid(st)er: <mm:field name="namereceiver"/><br/>
			<mm:field name="text"/><br/>
			Beoordeling: <img src="media/icon_rating_<mm:field name="score"/>.gif">
		</li>
      <mm:last>
   		</ul>
      </mm:last>
	</mm:relatednodes>
   </span>
</mm:node>