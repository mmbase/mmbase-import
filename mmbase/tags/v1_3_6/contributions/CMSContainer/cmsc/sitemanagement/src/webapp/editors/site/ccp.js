var NAVIGATION = "CMSC-NAVIGATION";

  function cut(number) {
    writeCookie(NAVIGATION, 'action', 'move');
    writeCookie(NAVIGATION, 'sourceChannel', number);
	var cut = "cut("+ "'" + number + "'" +")"
    return cut;
  }

  function copy(number) {
    writeCookie(NAVIGATION, 'action', 'copy');
    writeCookie(NAVIGATION, 'sourceChannel', number);
	var copy = "copy("+ "'" + number + "'" +")"
    return copy;
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
	var paste = "paste("+ "'" + number + "'" +")"
    return paste;
  }