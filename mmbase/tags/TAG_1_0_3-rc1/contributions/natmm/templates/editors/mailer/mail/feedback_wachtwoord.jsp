<%@include file="includes/abonnee/top.jsp"%>
<%-- this is a page where the user isn't logged in yet--%>
<mm:import id="login"/>
<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase">

<div id="container">
<%-- get header --%>
<%@include file="includes/abonnee/banner.jsp"%>

<div id="content">
<% // TODO: add functionality %>
<h2>Wachtwoord verzonden</h2>
<p>
De aanvraag voor een nieuw wachtwoord is met succes verzonden.
U krijgt per email een nieuw wachtwoord toegestuurd.
</p>
<p>
<a href="index.jsp">terug naar inlogpagina</a>
</p>

</div>

<%-- get footer --%>
<%@include file="includes/abonnee/footer.jsp"%>

</div>
</body>
</html>
</mm:cloud>
</mm:content>