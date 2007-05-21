var REPOSITORY = "CMSC-REPOSITORY";

  function cut(number) {
    writeCookie(REPOSITORY, 'action', 'move');
    writeCookie(REPOSITORY, 'sourceChannel', number);
    var cut = "cut("+ "'" + number + "'" +")"
    return cut;
  }

  function copy(number) {
    writeCookie(REPOSITORY, 'action', 'copy');
    writeCookie(REPOSITORY, 'sourceChannel', number);
    var copy = "copy("+ "'" + number + "'" +")"
    return copy;
  }

  function paste(number) {
    var action = readCookie(REPOSITORY, 'action', '');
    var source = readCookie(REPOSITORY, 'sourceChannel', '');

    if (action == 'move' ||action == 'copy') {
      document.forms['RepositoryPasteForm'].action.value = action;
      document.forms['RepositoryPasteForm'].sourcePasteChannel.value = source;
      document.forms['RepositoryPasteForm'].destPasteChannel.value = number;
      document.forms['RepositoryPasteForm'].submit();
    }
    var paste = "paste("+ "'" + number + "'" +")"
    return paste;
  }