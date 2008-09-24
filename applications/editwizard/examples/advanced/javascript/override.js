function resizeEditTable() {
    var divTop = findPosY(document.getElementById("editform"));

    if ((navigator.appVersion.indexOf('MSIE')!=-1) 
        && (navigator.appVersion.indexOf('Mac')!=-1)) {
        
      // IE on the Mac has some overflow problems. 
      // These statements will move the button div to the right position and
      // resizes the editform div.
      var docHeight = getDimensions().documentHeight;
      document.getElementById("editform").style.height = docHeight - (divTop + divButtonsHeight);
      // The div is relative positioned to the surrounding table.
      // +10, because we have a padding of 10 in the css.
      document.getElementById("commandbuttonbar").style.top = docHeight - (divTop + 10);
    }
    else {
       var docHeight = getDimensions().windowHeight;
       var maxHeight = docHeight - divTop;
       if (maxHeight < document.getElementById("editform").offsetHeight) {
          document.getElementById("editform").style.height = maxHeight;
       }
    }
}