<mm:node number="<%= eventId %>">
   <div class="pageheader"><mm:field name="titel" /></div>
   <br/>
   <mm:related path="posrel,images" orderby="images.title"
         ><div align="center" style="margin-top:8px;margin-bottom:15px;"><img src="<mm:node element="images"><mm:image template="+s(400)" /></mm:node
         >" alt="<mm:field name="images.title" />" border="0"></div>
   </mm:related>
	 <mm:field name="tekst" jspvar="sText" vartype="String" write="false">
     <% 
     if (sText!=null&&!HtmlCleaner.cleanText(sText,"<",">","").trim().equals("")) {
       %>
			 <span class="black"><%= sText %></span>
       <br/><br/>
       <%
     } %>
	 </mm:field>
	 <% boolean showNextDotLine = false; %>
   <mm:related path="posrel,paragraaf" orderby="posrel.pos" directions="UP">
      <%@include file="../relatedparagraph.jsp" %>
   </mm:related>
   <table cellpadding="5" cellspacing="0" style="border:solid #000000 1px;border-collapse:collapse;width:100%;">
    <%
    String [] type_paths = { "related,evenement_type", "posrel,deelnemers_categorie", "posrel,deelnemers_age", "related,natuurgebieden_type",
                               "related,evenement_duration", "related,evenement_distance", "readmore,afdelingen"}; 
    String [] type_fields = { "evenement_type.naam", "deelnemers_categorie.naam", "deelnemers_age.name", "natuurgebieden_type.name",
                               "evenement_duration.name", "evenement_distance.name", "afdelingen.naam" }; 
    String [] type_titles = { "Type activiteit", "Doelgroep", "Leeftijd", "Type terrein", 
                               "Tijdsduur", "Afstand", "Bezoekerscentrum" }; 
    for(int i=0; i<type_paths.length; i++) {
      %>
      <mm:related path="<%= type_paths[i] %>"
          ><mm:first><tr><td class="solid" style="width:150px;"><%= type_titles[i] %></td><td class="solid"></mm:first
          ><mm:first inverse="true">,</mm:first>
          <mm:field name="<%= type_fields[i] %>"/>
          <mm:last></td></tr></mm:last
      ></mm:related>	
      <%
    }
    
    String [] int_fields = { "min_aantal_deelnemers", "max_aantal_deelnemers" };
    String [] int_titles = { "Minimum aantal deelnemers", "Maximum aantal deelnemers" };
    for(int i=0; i<int_fields.length; i++) {
      %>
      <mm:field name="<%= int_fields[i] %>" jspvar="number" vartype="Integer" write="false">
      <% 
      int iNumber = number.intValue();
      if (iNumber>0) {
        %>
        <tr><td class="solid" style="width:150px;"><%= int_titles[i] %></td><td class="solid"><%= iNumber %></td></tr>
        <% 
      } %>
      </mm:field>	
    <% 
   } 
    
   %>
   </table>
   <br/>
   <%

   String [] text_fields = { "omschrijving", "omschrijving_eng", "omschrijving_de", "omschrijving_fra"};
   String [] text_titles = { "Bijzondere aandachtspunten", "Hulpmiddelen", "Inzet van de beheereenheid", "Eisen die de activiteit aan het terrein stelt"};
   for(int i=0; i<text_fields.length; i++) {
      %>
      <mm:field name="<%= text_fields[i] %>" jspvar="sText" vartype="String" write="false">
   		<% if (sText!=null&&!HtmlCleaner.cleanText(sText,"<",">","").trim().equals("")) {%>
            <br/>
            <div class="pageheader"><%= text_titles[i] %></div>
   			<span class="black"><%= sText %></span>
   		<% } %>
   	  </mm:field>	
      <% 
   } 
   
   %>
   <br/>
   <span class="black">
   <mm:list nodes="<%= paginaID %>" path="pagina,rolerel,users" orderby="rolerel.pos">
      <mm:first>Een link die niet klopt? email</mm:first>
      <mm:first inverse="true"> of </mm:first>
      <a href="mailto:<mm:field name="users.emailadres" />"><mm:field name="users.emailadres" /></a>
      <mm:last><br/><br/></mm:last>
	</mm:list>
   <br/>
   <a name="feedback"></a>
	Ervaring met deze activiteit?
   <a href="event_blueprints.jsp?&p=<%= paginaID %>&ev=<%= eventId %>&t=feedback">geef uw feedback</a><br/><br/>
	<mm:relatednodes type="feedback">
      <mm:first>
     		Feedback van collega's op deze activiteit: <br/><br/>
	      <ul>
      </mm:first>
		<li><mm:field name="namesender"/>&nbsp;/&nbsp;
			<mm:field name="begindate" jspvar="begindate" vartype="String" write="false">
				<% long td = Integer.parseInt(begindate); td = 1000 * td; Date dd = new Date(td); cal.setTime(dd); %>
            <%= cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR) %>
			</mm:field>
			<br/>
			Activiteit: <mm:field name="topic" /><br/>
			Uitgevoerd door: <mm:field name="namereceiver"/><br/>
			<mm:field name="text"/><br/>
			Ervaring: <img src="media/icon_rating_<mm:field name="score"/>.gif">
		</li>
      <mm:last>
   		</ul>
      </mm:last>
	</mm:relatednodes>
   </span>
</mm:node>