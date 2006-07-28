<%-- de volgende instellingen hebben te maken met de top van iedere pagina --%>
<%-- De titel zoals deze in alle pagina's verschijnt --%>
<mm:import id="bannertitle"><span>EGEM</span> Mail</mm:import>

<%-- de url zoals deze in de top van alle pagina's verschijnt--%>
<mm:import id="bannerurl">http://demo.mediacompetence.com</mm:import>

<%-- de tekst van de link zoals deze in de top van alle pagina's verschijnt--%>
<mm:import id="bannerurltext">demo.mediacompetence.com</mm:import>

<%-- de tekst van de over knop in de top van alle pagina's --%>
<mm:import id="bannerovertext">Over LifeLine Mail</mm:import>

<%-- de tekst van de logout knop in de top van alle pagina's --%>
<mm:import id="bannerlogouttext">Log uit</mm:import>

<%-- de tekst van de help knop in de top van alle pagina's --%>
<mm:import id="bannerhelptext">Help</mm:import>

<%-- de volgende instellingen hebben te maken met de configuratie van de applicatie --%>

<%-- als hieronder een gemeentenaam gebruikt wordt dan moet dat als volgt:
     de gemeente Leiderdorp.--%>
<%-- let op: de gemeeentenaam moet exact hetzelfde zijn als de organisatienaam die bij
     de installatie is ingevoerd --%>
<mm:import id="gemeentenaam">Live Publish</mm:import>

<%-- de naam zoals deze in alle pagina's gebruikt wordt --%>
<%-- vervang door bijv GemeentenaamMail--%>
<mm:import id="applicationname">LifeLineEmail</mm:import>

<%-- emailadres welke gebruikt wordt als afzender van alle email--%>
<mm:import id="applicationemail">demo@mediacompetence.com</mm:import>

<%-- de url waarop de applicatie te bereiken is --%>
<mm:import id="applicationurl">http://egemmail.nl/</mm:import>

<%-- de url waarop de applicatie voor de producenten te bereiken is --%>
<mm:import id="producenturl">http://demo.mediacompetence.com/producent/</mm:import>

<%-- de url waarop de applicatie voor de beheerder te bereiken is --%>
<mm:import id="adminurl">http://demo.mediacompetence.com/beheerder/</mm:import>

<%-- website, wordt gebruikt in de tekst van de disclaimer --%>
<mm:import id="website">egemmail.nl</mm:import>

<%-- wordt gebruikt als afzender onderaan de informatiemailtjes
     (mailtjes bij aanmelden, wachtwoord wijzigigen, etc), bijv Gemeente Leiderdorp --%>
<mm:import id="signaturename">LifeLine</mm:import>
<%-- de locatie waar de abonnee informatie naar toe weggeschreven kan worden --%>
<%-- hier moet de applicatie server schrijfrechten hebben --%>
<%-- als u de locatie van de webapp directory heeft gewijzigd, zult u dit hier moeten aanpassen --%>
<mm:import id="exportlocation" jspvar="exportlocation">/mail/beheerder/export/</mm:import>
<%-- not used at the moment --%>
<mm:import id="newslettername">default</mm:import>

