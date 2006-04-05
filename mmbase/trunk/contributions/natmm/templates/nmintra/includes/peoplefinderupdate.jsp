<form method="POST" action="smoelenboek.jsp<%= templateQueryString %>" name="whoiswhoupdate" onSubmit="return updateIt(this);">
<mm:node number="<%= employeeId %>"
    ><mm:related path="readmore,afdelingen" 
        ><mm:field name="afdelingen.naam" jspvar="name" vartype="String" write="false"><% deptdescrId = name; %></mm:field
        ><mm:field name="readmore.readmore" jspvar="readmore" vartype="String" write="false"
            ><mm:isnotempty><% deptdescrId += " / functie omschrijving (formeel) is \"" + readmore + "\""; %></mm:isnotempty
        ></mm:field
    ></mm:related
    ><mm:related path="readmore,locations" 
        ><mm:field name="locations.naam" jspvar="name" vartype="String" write="false"><% progdescrId = name; %></mm:field
    ></mm:related
    >
    <table border="0" cellpadding="0" cellspacing="0">
    <tr><td colspan="2"><h4>Vul hier de correcte gegevens in (*)</h4></td></tr>
    <tr><td>Voornaam:&nbsp;</td>                    <td><input type="text" class="<%=  cssClassName %>" name="firstname" value="<mm:field name="firstname" />" style="width:300px;"></td></tr>
    <tr><td>Initialen:&nbsp;</td>                   <td><input type="text" class="<%=  cssClassName %>" name="initials" value="<mm:field name="initials" />" style="width:300px;"></td></tr>
    <tr><td>Tussenvoegsel:&nbsp;</td>               <td><input type="text" class="<%=  cssClassName %>" name="suffix" value="<mm:field name="suffix" />" style="width:300px;"></td></tr>
    <tr><td>Achternaam:&nbsp;</td>                  <td><input type="text" class="<%=  cssClassName %>" name="lastname" value="<mm:field name="lastname" />" style="width:300px;"></td></tr>
    <tr><td>Telefoon:&nbsp;</td>                    <td><input type="text" class="<%=  cssClassName %>" name="companyphone" value="<mm:field name="companyphone" />" style="width:300px;"></td></tr>
    <tr><td>Fax:&nbsp;</td>                         <td><input type="text" class="<%=  cssClassName %>" name="fax" value="<mm:field name="fax" />" style="width:300px;"></td></tr>
    <tr><td>Email (**):&nbsp;</td>                  <td><input type="text" class="<%=  cssClassName %>" name="email" value="<mm:field name="email" />" style="width:300px;"></td></tr>
    <tr><td>Regio/afdeling en functie:&nbsp;</td>   <td><textarea class="<%=  cssClassName %>" name="deptdescr" style="width:300px;height:50px;"><%= deptdescrId %></textarea></td></tr>
    <tr><td>Functie (visitekaartje):&nbsp;</td>     <td><input type="text" class="<%=  cssClassName %>" name="posdescr" value="<mm:field name="position" />" style="width:300px;"></td></tr>
    <tr><td>Lokatie:&nbsp;</td>                     <td><input type="text" class="<%=  cssClassName %>" name="progdescr" value="<%= progdescrId %>" style="width:300px;"></td></tr>
    <tr><td>En verder:&nbsp;</td>                   <td><textarea class="<%=  cssClassName %>" name="descrupdate" style="width:300px;height:50px;"><mm:field name="omschrijving" /></textarea></td></tr>
    <tr><td colspan="2"><div align="right"><input type="submit" name="Submit" value="Verstuur wijzigingen" class="<%=  cssClassName %>"  style="text-align:center;font-weight:bold;">&nbsp;</div></td></tr>
    <tr><td colspan="2">
     <i>(*) een nieuwe foto kunt u versturen naar <a href="mailto:<%= defaultFromAddress %>"><%= defaultFromAddress %><a></i><br>
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
    var deptdescr = escape(document.whoiswhoupdate.elements["deptdescr"].value);
    var progdescr = escape(document.whoiswhoupdate.elements["progdescr"].value);
    var posdescr = escape(document.whoiswhoupdate.elements["posdescr"].value);
    var descrupdate = escape(document.whoiswhoupdate.elements["descrupdate"].value);
    var introupdate = escape(document.whoiswhoupdate.elements["introupdate"].value);
    href += "&employee=<%= employeeId %>&firstname=" + firstname + "&initials=" + initials + "&suffix=" + suffix  + "&lastname=" + lastname 
         + "&companyphone=" + companyphone + "&cellularphone=" + cellularphone + "&fax=" + fax + "&email=" + email
         + "&deptdescr=" + deptdescr + "&progdescr=" + progdescr + "&posdescr=" + posdescr + "&descrupdate=" + descrupdate + "&introupdate=" + introupdate + "&pst=|action=commit"; 
    document.location = href; 
    return false; 
}
//-->
</script>