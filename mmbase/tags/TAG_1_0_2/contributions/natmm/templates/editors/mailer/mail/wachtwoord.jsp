<%@include file="includes/abonnee/top.jsp"%>
<%-- this is a page where the user isn't logged in yet--%>
<mm:import id="login"/>
<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase">

<div id="container">
<%-- get header --%>
<%@include file="includes/abonnee/banner.jsp"%>

<div id="content">
<h2>Nieuw wachtwoord aanvragen</h2>
<p>Bent u uw wachtwoord vergeten? Om een nieuw wachtwoord aan te vragen, vult u hieronder uw gebruikersnaam of e-mailadres in, en klikt u op 'Verstuur'. Het nieuwe wachtwoord wordt dan naar uw e-mailadres gestuurd.</p>
<form action="feedback_wachtwoord.jsp" method="post">
<fieldset>
<legend>Wachtwoord</legend>
<div class="formcontainer">
<div><label>gebruikersnaam of e-mail:</label><input name="wantedaccount" type="text" size="10" maxlength="100" /></div>
</div>
<div><label>&nbsp;</label><input class="button" type="submit" value="Verstuur"></div>
</fieldset>
</form>

</div>

<%-- get footer --%>
<%@include file="includes/abonnee/footer.jsp"%>

</div>
</body>
</html>
</mm:cloud>
</mm:content>