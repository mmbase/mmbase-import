/**
 * A lot of these methods are overrides of methods in the MMBase
 * editwizard javascript code. These methods should be adjusted to
 * the new implementation when this project is upgraded to a new
 * version of MMBase.
 */

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

      enableImgButton(document.getElementById("bottombutton-finish"), "titlefinish", "Store all changes");
      enableImgButton(document.getElementById("bottombutton-accept"), "titleaccept", "Store all changes");
      enableImgButton(document.getElementById("bottombutton-reject"), "titlereject", "Store all changes");
      enableImgButton(document.getElementById("bottombutton-publish"), "titlepublish", "Store all changes");
   } else {
      setSaveInactive("true");
      disableImgButton(document.getElementById("bottombutton-save"),"titlenosave", "The changes cannot be saved, since some data is not filled in correctly.");
      disableImgButton(document.getElementById("bottombutton-saveonly"),"titlenosave", "The changes cannot be saved, since some data is not filled in correctly.");

      disableImgButton(document.getElementById("bottombutton-finish"),"titlenofinish", "The changes cannot be saved, since some data is not filled in correctly.");
      disableImgButton(document.getElementById("bottombutton-accept"),"titlenoaccept", "The changes cannot be saved, since some data is not filled in correctly.");
      disableImgButton(document.getElementById("bottombutton-reject"),"titlenoreject", "The changes cannot be saved, since some data is not filled in correctly.");
      disableImgButton(document.getElementById("bottombutton-publish"),"titlenopublish", "The changes cannot be saved, since some data is not filled in correctly.");
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
