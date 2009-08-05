function checkContact(formulier) {
  var msg = "";
  if (!hasInput(formulier.naam)) {
      msg += "You did not provide a name.\n";
  }

  if (hasInput(formulier.email)) {
    if (!emailCheck(formulier.email.value)) {
      msg += "'" + formulier.email.value + "' is not a valid e-mail address. Please provide a valid one.\n";
    }
  } else {
      msg += "You did not provide an e-mail address.\n";
  }

  if (!hasInput(formulier.bericht)) {
      msg += "You did not write a message.\n";
  }

  if(msg) {
      alert(msg);
      return false;
  }
}

