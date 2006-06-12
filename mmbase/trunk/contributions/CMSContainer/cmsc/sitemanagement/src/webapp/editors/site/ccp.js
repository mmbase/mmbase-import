var NAVIGATION = "CMSC-NAVIGATION";

  function cut(number) {
    writeCookie(NAVIGATION, 'action', 'move');
    writeCookie(NAVIGATION, 'sourceChannel', number);
    return false;
  }

  function copy(number) {
    writeCookie(NAVIGATION, 'action', 'copy');
    writeCookie(NAVIGATION, 'sourceChannel', number);
    return false;
  }

  function paste(number) {
    var action = readCookie(NAVIGATION, 'action', '');
    var source = readCookie(NAVIGATION, 'sourceChannel', '');

    if (action == 'move' ||action == 'copy') {
      document.forms['NavigationPasteForm'].action.value = action;
      document.forms['NavigationPasteForm'].sourcePasteChannel.value = source;
      document.forms['NavigationPasteForm'].destPasteChannel.value = number;
      document.forms['NavigationPasteForm'].submit();
    }
    return false;
  }