<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Community</title>
    <link rel="stylesheet" type="text/css" href="css/style.css" media="screen">
    <link rel="stylesheet" type="text/css" href="css/swapBox.css" media="screen">
    <script type="text/javascript" src="javascript/community.js"></script>
  </head>
  <body>

<html:errors />

<html:form styleId="userdata" action='/userAddAction.do' method='post'>
  <html:hidden property="action" />
  <fieldset>
    <legend></legend>
    <h2 id="naam"><span id="naam.voornaam">[Voornaam]</span> <span id="naam.tussenvoegsels"></span> <span id="naam.achternaam">[achternaam]</span></h2>
        <logic:equal name='userForm' property='action' value='update'>
          <p><label>Account</label><html:text property="account" readonly="true"/></p>
        </logic:equal>
        <logic:equal name='userForm' property='action' value='add'>
          <p><label>Account</label><html:text property="account" /></p>
        </logic:equal>
    <p><label>Voornaam</label><html:text property="voornaam" /></p>
    <p><label>Tussenvoegsels</label><html:text property="tussenVoegsels" /></p>
    <p><label>Achternaam</label><html:text property="achterNaam" /></p>
    <p><label>Email</label><html:text property="email" /></p>
    <p><label>Email Confirmation</label><html:text property="emailConfirmation" /></p>
    <p><label>Bedrijf</label><html:text property="bedrijf" /></p>
  </fieldset>
  <fieldset>
    <legend></legend>
    <p><button type="reset" onclick="this.form.reset()">Annuleren</button></p>
    <p><button type="submit">Opslaan</button></p>
  </fieldset>
</html:form>

  </body>
</html>