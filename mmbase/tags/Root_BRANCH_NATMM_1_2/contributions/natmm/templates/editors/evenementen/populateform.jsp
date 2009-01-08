javascript:populateInput('<%= dnumber
      %>','<mm:write referid="snumber"
      />','<%= HtmlCleaner.filterEntitiesEvents(thisParticipant.getStringValue("prefix"))
      %>','<%= HtmlCleaner.filterEntitiesEvents(thisParticipant.getStringValue("firstname"))
      %>','<mm:field name="initials"
      />','<mm:field name="suffix"
      />','<%= HtmlCleaner.filterEntitiesEvents(thisParticipant.getStringValue("lastname"))
      %>','<mm:field name="bron"
      />','<mm:relatednodes type="deelnemers_categorie"><mm:field name="number" /><mm:import id="dc_found" /></mm:relatednodes
            ><mm:notpresent referid="dc_found">-1</mm:notpresent><mm:remove referid="dc_found" 
      />','<mm:field name="privatephone"
      />','<mm:field name="email"
      />','<mm:field name="lidnummer"
      />','<%= HtmlCleaner.filterEntitiesEvents(thisParticipant.getStringValue("straatnaam"))
      %>','<mm:field name="huisnummer"
      />','<%= HtmlCleaner.filterEntitiesEvents(thisParticipant.getStringValue("plaatsnaam"))
      %>','<%= HtmlCleaner.filterEntitiesEvents(thisParticipant.getStringValue("land"))
      %>','<mm:list nodes="<%= snumber %>" path="inschrijvingen"><mm:field name="inschrijvingen.betaalwijze" /></mm:list
		 >','<mm:field name="gender"
		/>','<mm:field name="postcode" 
      />','<mm:write referid="ssource"
      />','<%= HtmlCleaner.filterEntitiesEvents(sdescription)
      %>','<mm:list nodes="<%= snumber %>" path="inschrijvingen,related,inschrijvings_status"><mm:field name="inschrijvings_status.number" /></mm:list
       >');