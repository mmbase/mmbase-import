var preloadimages = new Array();
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
   var savebut = document.getElementById("bottombutton-save");
    
   if(savebut!= null) {
      if (allvalid) {
         savebut.src = savebut.getAttribute("enabledsrc");
         savebut.className = "bottombutton";
         savebut.disabled = false;
         var usetext = getToolTipValue(savebut,"titlesave", "Stores all changes.");
         savebut.title = usetext;
      } else {
         savebut.src = savebut.getAttribute("disabledsrc");
         savebut.className = "bottombutton-disabled";
         savebut.disabled = true;
         var usetext = getToolTipValue(savebut,"titlenosave", "You cannot save because one or more forms are invalid.");
         savebut.title = usetext;
      }
   }
}