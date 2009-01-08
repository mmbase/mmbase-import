<mm:node number="<%= employeeId %>" jspvar="e"><%
   thisPerson = firstnameId;
   if(!suffixId.equals("")) { thisPerson += " " + suffixId; }
   thisPerson += " " + lastnameId;
   thisPerson += " (Sofinummer: " + e.getStringValue("externid") + ")";
   
   // *** PZ ***
   if(!firstnameId.equals(e.getStringValue("firstname"))) {
      pzText += "<br><br>Voornaam '" + e.getStringValue("firstname") + "' moet worden gewijzigd in: " + firstnameId;
   }
   if(!initialsId.equals(e.getStringValue("initials"))) {
      pzText += "<br><br>Initialen '" + e.getStringValue("initials") + "' moet worden gewijzigd in: " + initialsId;
   } 
   if(!suffixId.equals(e.getStringValue("suffix"))) {
      pzText += "<br><br>Tussenvoegsel '" + e.getStringValue("suffix") + "' moet worden gewijzigd in: " + suffixId;
   }
   if(!lastnameId.equals(e.getStringValue("lastname"))) {
      pzText += "<br><br>Achternaam '" + e.getStringValue("lastname") + "' moet worden gewijzigd in: " + lastnameId;
   }
   if(!omschrijving_engId.equals(e.getStringValue("omschrijving_eng"))) {
      pzText += "<br><br>Beschrijving lokatie, regio/afdeling en functie moet worden gewijzigd in:<br>" + omschrijving_engId;
   }
   if(!omschrijving_deId.equals(e.getStringValue("omschrijving_de"))) {
      pzText += "<br><br>Beschrijving werkzaamheden moet worden gewijzigd in:<br>" + omschrijving_deId;
   }
   
   // *** FZ ***
   if(!companyphoneId.equals(e.getStringValue("companyphone"))) {
      fzText += "<br><br>Telefoon '" + e.getStringValue("companyphone") + "' moet worden gewijzigd in: " + companyphoneId;
   }
   if(!cellularphoneId.equals(e.getStringValue("cellularphone"))) {
      fzText += "<br><br>Mobiel '" + e.getStringValue("cellularphone") + "' moet worden gewijzigd in: " + cellularphoneId;
   }
   if(!faxId.equals(e.getStringValue("fax"))) {
      fzText += "<br><br>Fax '" + e.getStringValue("fax") + "' moet worden gewijzigd in: " + faxId;
   }
   if(!emailId.equals(e.getStringValue("email"))) {
      fzText += "<br><br>Email '" + e.getStringValue("email") + "' moet worden gewijzigd in: " + emailId + " (wordt afgestemd met automatisering en/of betrokken medewerker)";
   }
   
   // *** fields that can be changed by employees themselves ***
   if(!omschrijving_fraId.equals(e.getStringValue("omschrijving_fra"))) {
     dcText += "<br><br>" + specialDays + ": " + omschrijving_fraId;
   }
   if(!omschrijvingId.equals(e.getStringValue("omschrijving"))) {
     dcText += "<br><br>En verder moet worden gewijzigd in: " + omschrijvingId;
   }
%></mm:node>