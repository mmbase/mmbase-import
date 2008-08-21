<%@include file="includes/abonnee/top.jsp"%>
<mm:import id="login"/>
<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase">

<%-- The name of this page.
     Used in the sidebar include make an menu-item look active --%>
<mm:import id="active">logout</mm:import>

<%
session.invalidate();
Cookie thisCookie = new Cookie("memberid", null);
thisCookie.setMaxAge(0);
response.addCookie(thisCookie);
%>

<div id="container">

<%@include file="includes/abonnee/banner.jsp"%>

<div id="content">
<h2>Uitgelogd</h2>
<p>
U bent uitgelogd.
</p>
<p><a title="terug naar de inlogpagina" href="index.jsp">Terug naar aanmelden/inloggen</a></p>
</div>

<%-- get footer --%>
<%@include file="includes/abonnee/footer.jsp"%>

</div>
</body>
</html>
</mm:cloud>
</mm:content>
