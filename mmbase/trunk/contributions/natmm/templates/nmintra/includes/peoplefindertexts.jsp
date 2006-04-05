<mm:node number="<%= employeeId %>"><%
    thisPerson = firstnameId;
    if(!suffixId.equals("")) { thisPerson += " " + suffixId; }
    thisPerson += " " + lastnameId;
    %><mm:field name="externid" jspvar="dummy" vartype="String" write="false"><%
        thisPerson += " (Sofinummer: " + dummy + ")";
    %></mm:field><%
    // *** PZ ***
    %><mm:field name="firstname" jspvar="dummy" vartype="String" write="false"><%
        if(!firstnameId.equals(dummy)) {
            pzText += "<br><br>Voornaam '" + dummy + "' moet worden gewijzigd in: " + firstnameId;
        }
    %></mm:field
    ><mm:field name="initials" jspvar="dummy" vartype="String" write="false"><%
        if(!initialsId.equals(dummy)) {
            pzText += "<br><br>Initialen '" + dummy + "' moet worden gewijzigd in: " + initialsId;
        } 
    %></mm:field
    ><mm:field name="suffix" jspvar="dummy" vartype="String" write="false"><%
        if(!suffixId.equals(dummy)) {
            pzText += "<br><br>Tussenvoegsel '" + dummy + "' moet worden gewijzigd in: " + suffixId;
        }
    %></mm:field
    ><mm:field name="lastname" jspvar="dummy" vartype="String" write="false"><%
        if(!lastnameId.equals(dummy)) {
            pzText += "<br><br>Achternaam '" + dummy + "' moet worden gewijzigd in: " + lastnameId;
        }
    %></mm:field
    ><%

    // *** FZ ***
    %><mm:field name="companyphone" jspvar="dummy" vartype="String" write="false"><%
        if(!companyphoneId.equals(dummy)) {
            fzText += "<br><br>Telefoon '" + dummy + "' moet worden gewijzigd in: " + companyphoneId;
        }
    %></mm:field
    ><mm:field name="cellularphone" jspvar="dummy" vartype="String" write="false"><%
        if(!cellularphoneId.equals(dummy)) {
            fzText += "<br><br>Mobiel '" + dummy + "' moet worden gewijzigd in: " + cellularphoneId;
        }
    %></mm:field
    ><mm:field name="fax" jspvar="dummy" vartype="String" write="false"><%
        if(!faxId.equals(dummy)) {
            fzText += "<br><br>Fax '" + dummy + "' moet worden gewijzigd in: " + faxId;
        }
    %></mm:field
    ><mm:field name="email" jspvar="dummy" vartype="String" write="false"><% 
        if(!emailId.equals(dummy)) {
            fzText += "<br><br>Email '" + dummy + "' moet worden gewijzigd in: " + emailId + " (wordt afgestemd met automatisering en/of betrokken medewerker)";
        }
    %></mm:field><%

    // *** fields that can be changed by employees themselves ***
    // *** have to use a tailor made comparison because constructed and saved strings differ from those passed as parameters ***
    %><mm:field name="omschrijving" jspvar="dummy" vartype="String" write="false"><%
        if(!descrupdateId.equals(dummy)) {
           dcText += "<br><br>En verder moet worden gewijzigd in: " + descrupdateId;
        }
    %></mm:field
></mm:node>