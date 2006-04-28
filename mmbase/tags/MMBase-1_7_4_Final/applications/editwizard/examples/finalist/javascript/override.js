var preloadimages = new Array();

// Preloading only works when the browser is not set to check for newer versions
// of stored pages for every visit to the page. In mozilla you don;t see
// any side-effect, but IE starts loading into eternity when the page after a
// wizard is closed, is loaded. Eg. listpages will have a loading bar all the time
// The issue is caused by the inactive button images. They are sometimes loaded
// after the wizard page is unloaded and the next page is loading.
function preLoadButtons() {
   a = 0;
   for (i = 0; i < document.images.length; i++) { 
      if (document.images[i].id.substr(0,"bottombutton-".length)=="bottombutton-") {
         preloadimages[a] = new Image();
         preloadimages[a].src = document.images[i].getAttribute('disabledsrc');
         a++;
      }
   }
}

function setButtonsInactive() {
   for (i = 0; i < document.images.length; i++) { 
      if (document.images[i].id.substr(0,"bottombutton-".length)=="bottombutton-") {
         var image = document.images[i];
         image.src = image.getAttribute('disabledsrc');
         image.className = "bottombutton-disabled";
         image.disabled = true;
      }
   }
}

function updateButtons(allvalid) {
   if (allvalid) {
      setSaveInactive("false");
      enableImgButton(document.getElementById("bottombutton-save"), "titlesave", "Stores all changes (and quit)");
      enableImgButton(document.getElementById("bottombutton-saveonly"), "titlesave", "Store all changes (but continue editing).");
   } else {
      setSaveInactive("true");
      disableImgButton(document.getElementById("bottombutton-save"),"titlenosave", "The changes cannot be saved, since some data is not filled in correctly.");
      disableImgButton(document.getElementById("bottombutton-saveonly"),"titlenosave", "The changes cannot be saved, since some data is not filled in correctly.");
   }
}

function enableImgButton(button, textAttr, textDefault) {
   if (button != null) {
      button.src = button.getAttribute("enabledsrc");
      button.className = "bottombutton";
      button.disabled = false;
      var usetext = getToolTipValue(button,textAttr, textDefault);
      button.title = usetext;
   }
}

function disableImgButton(button, textAttr, textDefault) {
   if (button != null) {
      button.src = button.getAttribute("disabledsrc");
      button.className = "bottombutton-disabled";
      button.disabled = true;
      var usetext = getToolTipValue(button,textAttr, textDefault);
      button.title = usetext;
   }
}

function setFocusOnFirstInput() {
    var form = document.forms["form"];
    for (var i=0; i < form.elements.length; i++) {
        var elem = form.elements[i];
        // find first editable field
        var hidden = elem.getAttribute("type"); //.toLowerCase();
        if (hidden != "hidden") {
            // It is very annoying when you want to scroll with a wheel mouse
            // when you open a wizard and the selectbox is the first field.
            if (elem.getAttribute('ftype') != 'enum') {
                elem.focus();
                break;
            }
        }
    }
}
