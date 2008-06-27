/*
  Example contact form validation should be used with form.js like:
  <script src="contact.js" />" type="text/javascript"><!-- MSIE needs this --></script>
  <script src="forms.js" />" type="text/javascript"><!-- MSIE needs this --></script>
  <form onsubmit="return checkContact(this)" action="contact.jsp" method="post">
*/
function checkContact(formulier) {
  var msg = "";
  if (!hasInput(formulier.naam)) {
      msg += "U heeft uw naam niet ingevuld.\n";
  }

  if (hasInput(formulier.email)) {
    if (!emailCheck(formulier.email.value)) {
      msg += "Vul s.v.p. een geldig email-adres in.\n";
    }
  } else {
      msg += "U heeft uw e-mail adres niet ingevuld.\n";
  }

  if (!hasInput(formulier.naam)) {
      msg += "U heeft geen bericht geschreven.\n";
  }

  if(msg) {
      alert(msg);
      return false;
  }
}

