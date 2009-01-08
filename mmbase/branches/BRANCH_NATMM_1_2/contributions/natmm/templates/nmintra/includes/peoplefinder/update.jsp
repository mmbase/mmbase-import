<form method="POST" action="<%= javax.servlet.http.HttpUtils.getRequestURL(request) + templateQueryString 
  %>" name="whoiswhoupdate" onSubmit="return updateIt(this);">
<mm:node number="<%= employeeId %>">
    <table border="0" cellpadding="0" cellspacing="0">
    <tr><td colspan="2"><h4>Vul hier de correcte gegevens in (*)</h4></td></tr>
    <tr><td>Voornaam:&nbsp;</td>                    <td><input type="text" name="firstname" value="<mm:field name="firstname" />" style="width:400px;"></td></tr>
    <tr><td>Initialen:&nbsp;</td>                   <td><input type="text" name="initials" value="<mm:field name="initials" />" style="width:400px;"></td></tr>
    <tr><td>Tussenvoegsel:&nbsp;</td>               <td><input type="text" name="suffix" value="<mm:field name="suffix" />" style="width:400px;"></td></tr>
    <tr><td>Achternaam:&nbsp;</td>                  <td><input type="text" name="lastname" value="<mm:field name="lastname" />" style="width:400px;"></td></tr>
    <tr><td>Telefoon:&nbsp;</td>                    <td><input type="text" name="companyphone" value="<mm:field name="companyphone" />" style="width:400px;"></td></tr>
    <tr><td>Mobiel:&nbsp;</td>                      <td><input type="text" name="cellularphone" value="<mm:field name="cellularphone" />" style="width:400px;"></td></tr>
    <tr><td>Fax:&nbsp;</td>                         <td><input type="text" name="fax" value="<mm:field name="fax" />" style="width:400px;"></td></tr>
    <tr><td>Email (**):&nbsp;</td>                  <td><input type="text" name="email" value="<mm:field name="email" />" style="width:400px;"></td></tr>
    <tr><td>Lokatie, regio/afdeling en functie:&nbsp;</td><td><textarea name="omschrijving_eng" style="width:400px;height:50px;"><%= omschrijving_engId %></textarea></td></tr>
    <tr><td>Functie (visitekaartje):&nbsp;</td>     <td><input type="text" name="job" value="<mm:field name="job" />" style="width:400px;"></td></tr>
    <tr><td><%= specialDays %>:&nbsp;</td>          <td><textarea name="omschrijving_fra" rows="2" style="width:400px;"><mm:field name="omschrijving_fra" /></textarea></td></tr>
    <tr><td>Werkzaamheden: &nbsp;</td>              <td><textarea name="omschrijving_de" rows="4" style="width:400px;"><mm:field name="omschrijving_de" jspvar="omschrijving_de" vartype="String" write="false"><%= HtmlCleaner.cleanHtml(omschrijving_de) %></mm:field></textarea></td></tr>
    <% if(iRubriekLayout!=NMIntraConfig.SUBSITE1_LAYOUT) { 
      %>
      <tr><td>En verder:&nbsp;</td>                <td><textarea name="omschrijving" rows="5" style="width:400px;"><mm:field name="omschrijving" /></textarea></td></tr>
      <%
    } else {
      %>
      <input type="hidden" name="omschrijving" value="" /> 
      <%
      HashSet myKeywords = new HashSet();
      %>
      <mm:related path="related,keywords">
          <mm:field name="keywords.number" jspvar="keywords_number" vartype="String" write="false">
            <% myKeywords.add(keywords_number); %>
          </mm:field>
      </mm:related>
      <mm:listnodes type="keywords" orderby="word" directions="UP">
        <tr><td colspan="2">
          <mm:field name="number" jspvar="keywords_number" vartype="String" write="false">
            <input type="checkbox" name="keyword<%= keywords_number %>" value="<%= keywords_number %>" style="background-color:#FFFFFF;" 
              <%= (myKeywords.contains(keywords_number)? "CHECKED" : "")  %>>
          </mm:field>
          <mm:field name="word" />
        </td></tr>
      </mm:listnodes>
      <%
    } %>
    <tr><td colspan="2"><div align="right"><input type="submit" name="Submit" value="Verstuur wijzigingen" style="text-align:center;font-weight:bold;">&nbsp;</div></td></tr>
    <tr><td colspan="2">
     <% String emailAddress = ap.getFromEmailAddress(); %>
     <i>(*) een nieuwe foto kunt u versturen naar <a href="mailto:<%= emailAddress %>"><%= emailAddress %><a></i><br>
     <i>(**) alleen een intern "...<%= emailAddress.substring(emailAddress.indexOf("@")) %>" emailadres is toegestaan.</i></td></tr>
    </table>
</mm:node>
</form>
<script type="text/javascript">
function updateIt(el) {
    var href = document.whoiswhoupdate.action;
    var firstname = escape(document.whoiswhoupdate.elements["firstname"].value);
    var initials = escape(document.whoiswhoupdate.elements["initials"].value);
    var suffix = escape(document.whoiswhoupdate.elements["suffix"].value);
    var lastname = escape(document.whoiswhoupdate.elements["lastname"].value);
    var companyphone = escape(document.whoiswhoupdate.elements["companyphone"].value);
    var cellularphone = escape(document.whoiswhoupdate.elements["cellularphone"].value);
    var fax = escape(document.whoiswhoupdate.elements["fax"].value);
    var email = escape(document.whoiswhoupdate.elements["email"].value);
    var job = escape(document.whoiswhoupdate.elements["job"].value);
    var omschrijving_eng = escape(document.whoiswhoupdate.elements["omschrijving_eng"].value);
    var omschrijving_de  = escape(document.whoiswhoupdate.elements["omschrijving_de"].value);
    var omschrijving = escape(document.whoiswhoupdate.elements["omschrijving"].value);
    var omschrijving_fra = escape(document.whoiswhoupdate.elements["omschrijving_fra"].value);
    href += "&employee=<%= employeeId %>&firstname=" + firstname + "&initials=" + initials + "&suffix=" + suffix  + "&lastname=" + lastname 
         + "&companyphone=" + companyphone + "&cellularphone=" + cellularphone + "&fax=" + fax + "&email=" + email
         + "&job=" + job + "&omschrijving_eng=" + omschrijving_eng + "&omschrijving_de=" + omschrijving_de + "&omschrijving_fra=" + omschrijving_fra
         + "&omschrijving=" + omschrijving;
     <% 
    if(iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) {
      %><mm:listnodes type="keywords" orderby="word" directions="UP"
         ><mm:field name="number" jspvar="keywords_number" vartype="String" write="false">
            var answer = document.whoiswhoupdate.keyword<%= keywords_number %>;
            if(answer.checked) {
              href += "&keyword<%= keywords_number %>=checked";
            }
         </mm:field
      ></mm:listnodes><%
    } %>
    href += "&pst=|action=commit";
    document.location = href; 
    return false; 
}
</script>