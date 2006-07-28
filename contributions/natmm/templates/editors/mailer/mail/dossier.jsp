<%-- get header --%>
<%@include file="includes/abonnee/top.jsp"%>
<%-- this is a page where the user isn't logged in yet--%>
<mm:import id="login"/>

<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase">

<mm:import externid="username" from="parameters" />
<mm:import externid="logged_user">2748</mm:import>
<mm:import externid="reason">please</mm:import>

<div id="container">

<%-- get header --%>
<%@include file="includes/abonnee/banner.jsp"%>

<%-- get some text, this must be changed in the next release, use something like multilanguage option --%>
<%@include file="includes/abonnee/defaulttext.jsp"%>

<div id="content">
<h2>Profiel</h2>
<p>Op deze profielpagina kunt u uw eigen voorkeuren bekijken en wijzigen.
Bij 'Inhoud' maakt u de keuze over welke onderwerpen u regelmatig berichten wilt ontvangen.
In het linkermenu, via de link 'instellingen', kunt u onder andere uw ontvangst interval en uw 
persoonlijke gegevens wijzigen.
</p>
<form method="post" action="dossier_cmd.jsp">
<fieldset>
<legend>Inhoud</legend>
<h2>Onderwerpen selecteren</h2>
<div class="formcontainer">
<mm:node number="dossiers" notfound="skip">
  <mm:relatednodes type="dossier" path="posrel,dossier" orderby="posrel.pos">
<%
    String sSelectFullName = "dossier";
    boolean isConnected = false;
%>
    <mm:field name="number" jspvar="dummy" vartype="String" write="false">
      <% sSelectFullName += dummy; %>
    </mm:field>
    <mm:related path="posrel,persoon" constraints="persoon.number='$logged_user'">
      <% isConnected = true; %>
    </mm:related>
    <input type="checkbox" name="<%= sSelectFullName %>"<%= (isConnected ? " checked" : "" ) %>>
    <mm:field name="naam"/>
    <br/>
  </mm:relatednodes>
</mm:node>
<input type="hidden" name="logged_user" value="<mm:write referid="logged_user"/>"/>
<div><input class="button" type="submit" value="Opslaan"> and text</div>

</fieldset>
</form>
</div>

<div id="sidebar">
<h3>&nbsp;</h3>
</div>

<%-- get footer --%>
<%@include file="includes/abonnee/footer.jsp"%>

</div>
</body>
</html>
</mm:cloud>
</mm:content>