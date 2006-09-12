<form method="POST" action="<%= requestURL %>smoelenboek.jsp<%= templateQueryString %>" name="whoiswhoupdate" onSubmit="return updateIt(this);">
<mm:node number="<%= employeeId %>">
    <table border="0" cellpadding="0" cellspacing="0">
    <tr><td colspan="2"><h4>Vul hier de correcte gegevens in (*)</h4></td></tr>
    <tr><td>Voornaam:&nbsp;</td>                    <td><input type="text" name="firstname" value="<mm:field name="firstname" />" style="width:300px;"></td></tr>
    <tr><td>Initialen:&nbsp;</td>                   <td><input type="text" name="initials" value="<mm:field name="initials" />" style="width:300px;"></td></tr>
    <tr><td>Tussenvoegsel:&nbsp;</td>               <td><input type="text" name="suffix" value="<mm:field name="suffix" />" style="width:300px;"></td></tr>
    <tr><td>Achternaam:&nbsp;</td>                  <td><input type="text" name="lastname" value="<mm:field name="lastname" />" style="width:300px;"></td></tr>
    <tr><td>Telefoon:&nbsp;</td>                    <td><input type="text" name="companyphone" value="<mm:field name="companyphone" />" style="width:300px;"></td></tr>
    <tr><td>Mobiel:&nbsp;</td>                      <td><input type="text" name="cellularphone" value="<mm:field name="cellularphone" />" style="width:300px;"></td></tr>
    <tr><td>Fax:&nbsp;</td>                         <td><input type="text" name="fax" value="<mm:field name="fax" />" style="width:300px;"></td></tr>
    <tr><td>Email (**):&nbsp;</td>                  <td><input type="text" name="email" value="<mm:field name="email" />" style="width:300px;"></td></tr>
    <tr><td>Regio/afdeling en functie:&nbsp;</td>   <td><textarea name="omschrijving_eng" style="width:300px;height:50px;"><%= omschrijving_engId %></textarea></td></tr>
    <tr><td>Functie (visitekaartje):&nbsp;</td>     <td><input type="text" name="job" value="<mm:field name="job" />" style="width:300px;"></td></tr>
    <tr><td>Lokatie:&nbsp;</td>                     <td><input type="text" name="omschrijving_de" value="<%= omschrijving_deId %>" style="width:300px;"></td></tr>
    <tr><td>Vaste vrije dag(en):&nbsp;</td>         <td><textarea name="omschrijving_fra" style="width:300px;height:50px;"><mm:field name="omschrijving_fra" /></textarea></td></tr>
    <tr><td>En verder:&nbsp;</td>                   <td><textarea name="omschrijving" style="width:300px;height:50px;"><mm:field name="omschrijving" /></textarea></td></tr>
    <tr><td colspan="2"><div align="right"><input type="submit" name="Submit" value="Verstuur wijzigingen"  style="text-align:center;font-weight:bold;">&nbsp;</div></td></tr>
    <tr><td colspan="2">
     <i>(*) een nieuwe foto kunt u versturen naar <a href="mailto:<%= ap.getFromEmailAddress() %>"><%= ap.getFromEmailAddress() %><a></i><br>
     <i>(**) alleen een intern "...@natuurmonumenten.nl" emailadres is toegestaan.</i></td></tr>
    </table>
</mm:node>
</form>
<script>
<!--
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
    var omschrijving_de = escape(document.whoiswhoupdate.elements["omschrijving_de"].value);
    var omschrijving = escape(document.whoiswhoupdate.elements["omschrijving"].value);
    var omschrijving_fra = escape(document.whoiswhoupdate.elements["omschrijving_fra"].value);
    href += "&employee=<%= employeeId %>&firstname=" + firstname + "&initials=" + initials + "&suffix=" + suffix  + "&lastname=" + lastname 
         + "&companyphone=" + companyphone + "&cellularphone=" + cellularphone + "&fax=" + fax + "&email=" + email
         + "&job=" + job + "&omschrijving_eng=" + omschrijving_eng + "&omschrijving_de=" + omschrijving_de + "&omschrijving_fra=" + omschrijving_fra
         + "&omschrijving=" + omschrijving + "&pst=|action=commit"; 
    document.location = href; 
    return false; 
}
//-->
</script>