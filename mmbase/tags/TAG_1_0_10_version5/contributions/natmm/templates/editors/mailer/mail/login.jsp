<%-- get header --%>
<%@include file="includes/abonnee/top.jsp"%>
<%-- this is a page where the user isn't logged in yet--%>
<mm:import id="active">login</mm:import>
<mm:import id="login"/>

<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase">
<mm:import externid="username" from="parameters" />
<mm:import externid="reason">please</mm:import>
<mm:import externid="referrer">index.jsp</mm:import>
<mm:compare referid="referrer" value=".">
  <mm:import id="referrer" reset="true">index.jsp</mm:import>
</mm:compare>



<div id="container">

   <%-- get header --%>
   <%@include file="includes/abonnee/banner.jsp"%>
   <mm:include referids="active" page="includes/abonnee/sidebar.jsp"/>


   <%-- get some text, this must be changed in the next release, use something like multilanguage option --%>
   <%@include file="includes/abonnee/defaulttext.jsp"%>

   <div id="content">
      <h2><mm:write referid="welcometitle"/></h2>
      <p><mm:write referid="welcometext" escape="none"/></p>

<%--
      <h2><mm:write referid="signuptitle"/></h2>
      <p><mm:write referid="signuptext" escape="none"/></p>


      <form action="feedback_aanmeld.jsp" method="post">
         <fieldset>
            <legend>Aanmelden</legend>
            <p>* is verplicht</p>
            <div class="formcontainer">
               <div><label>Gebruikersnaam*:</label><input name="username" type="text" size="10" maxlength="100" /></div>
               <div><label>E-mailadres*:</label><input name="email" type="text" size="10" maxlength="100" /></div>
            </div>
            <div><label>&nbsp;</label><input class="button" type="submit" value="Verstuur"></div>
         </fieldset>
      </form>
--%>

      <p><mm:write referid="logintext"/></p>
      <form method="post" action="index.jsp" >
         <fieldset>
            <legend>Inloggen</legend>
            <div class="formcontainer">
               <mm:compare referid="reason" value="failed">
                  <p class="errormessage">Login mislukt, probeer het opnieuw.</p>
               </mm:compare>
               <div><label>Gebruikersnaam:</label><input type="text" name="username" size="10" maxlength="100" /></div>
               <div><label>Wachtwoord:</label><input type="password" name="password" size="10" maxlength="100" />&nbsp;<a href="wachtwoord.jsp">wachtwoord vergeten?</a></div>
            </div>
            <input type="hidden" name="command" value="login"/>
            <div><label>&nbsp;</label><input class="button" type="submit" value="Login"></div>
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