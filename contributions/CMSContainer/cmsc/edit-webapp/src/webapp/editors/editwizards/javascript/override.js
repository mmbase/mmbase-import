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

var select_fid = '';
var select_did = '';

function selectPage(param, path, pos) {
   if (select_fid != null && select_did != null) {
       doAdd(param, 'cmd/add-item/' + select_fid + '/' + select_did + '//');
   }
}

function selectChannel(param, path, pos) {
   if (select_fid != null && select_did != null) {
       doAdd(param, 'cmd/add-item/' + select_fid + '/' + select_did + '//');
   }
}

function selectContent(param, path, pos) {
   if (select_fid != null && select_did != null) {
       doAdd(param, 'cmd/add-item/' + select_fid + '/' + select_did + '//');
   }
}

function doFinish() {
    if (!isSaveInactive()) {
        document.getElementById("workflowcommand").value="finish";
        clearScroll();
        setButtonsInactive();
        doSendDelayedCommand("cmd/commit////");
    }
}

function doAccept() {
    if (!isSaveInactive()) {
        document.getElementById("workflowcommand").value="accept";
        clearScroll();
        setButtonsInactive();
        doSendDelayedCommand("cmd/commit////");
    }
}

function doPublish() {
    if (!isSaveInactive()) {
        document.getElementById("workflowcommand").value="publish";
        clearScroll();
        setButtonsInactive();
        doSendDelayedCommand("cmd/commit////");
    }
}

function doReject() {
    var comment = prompt("Opmerking ?","");
    if (comment != null && comment != ""){ // OK pressed
        document.getElementById("workflowcomment").value = comment;
    }
    document.getElementById("workflowcommand").value="reject";
    clearScroll();
    setButtonsInactive();
    doSendDelayedCommand("cmd/cancel////");
}

function doCancel() {
    document.getElementById("workflowcommand").value="cancel";
    clearScroll();
    setButtonsInactive();
    doSendDelayedCommand("cmd/cancel////");
}


function doSave() {
    doCheckHtml();
    if (!isSaveInactive()) {
        clearScroll();
        setButtonsInactive();
        doSendDelayedCommand("cmd/commit////");
    }
}

function doSaveOnly() {
    doCheckHtml();
    if (!isSaveInactive()) {
        saveScroll();
        setButtonsInactive();
        doSendDelayedCommand("cmd/save////");
    }
}


/**
 * The delaying of commands is here, because without the delay the browser wont have the cycles to hide the buttons
 */
function doSendDelayedCommand(command) {
	setTimeout("doSendCommand(\""+command+"\")", 10);
}


/*
 * Maybe we should fix the two method below back to mmbase, this because it is clearly a bug in mmbase
*/
function resizeEditTable() {
    var divButtonsHeight = document.getElementById("commandbuttonbar").offsetHeight;
    var divTop = findPosY(document.getElementById("editform"));
    
    var isIE = (navigator.appVersion.indexOf('MSIE')!=-1);

    if (isIE && (navigator.appVersion.indexOf('Mac')!=-1)) {

      // IE on the Mac has some overflow problems.
      // These statements will move the button div to the right position and
      // resizes the editform div.
      var docHeight = getDimensions().documentHeight;
      document.getElementById("editform").style.height = docHeight - (divTop + divButtonsHeight);
      // The div is relative positioned to the surrounding table.
      // +10, because we have a padding of 10 in the css.
      document.getElementById("commandbuttonbar").style.top = docHeight - (2*divButtonsHeight + 10);
    }
    else {
       var docHeight = getDimensions().windowHeight;
       document.getElementById("editform").style.height = docHeight - (divTop + divButtonsHeight);
    }
    var docWidth = getDimensions().windowWidth;
    document.getElementById("editform").style.width = docWidth-((isIE)?5:0);

    var textareas = document.getElementsByTagName("textarea");
    for (var i = 0 ; i < textareas.length ; i++) {
        if(isSubEditElement(textareas[i])) {
	        textareas[i].style.width = docWidth - 355;
	     }
	     else {
	        textareas[i].style.width = docWidth - 100;
	     }
    }
}

function isSubEditElement(element) {
	if(element.className == "itemcanvas") {
		return true;
	}
	else if(element.parentNode == undefined) {
		return false;
	}
	else {
		return isSubEditElement(element.parentNode);
	}
}



// this is for when the page is disable because of no rights or in publish queue
function isSaveInactive() {
    var savebut = document.getElementById("bottombutton-save");
    if(savebut != null) {
	    return (savebut.getAttribute("inactive") == 'true');
	}
	else {
		return false;
	}
}

function setSaveInactive(inactive) {
    var savebut = document.getElementById("bottombutton-save");
    if(savebut != null) {
	    savebut.setAttribute("inactive", inactive);
	}
}


function setButtonsInactive() {
   var cancelbut = document.getElementById("bottombutton-cancel");
   cancelbut.style.visibility = "hidden";
   
   var savebut = document.getElementById("bottombutton-save");
   if(savebut != null) {
	   savebut.style.visibility = "hidden";
   }
   
   var saveonlybut = document.getElementById("bottombutton-saveonly");
   if (saveonlybut != null) {
      saveonlybut.style.visibility = "hidden";
   }
}



function hideCalendar()	{
	if(crossobj != null) {
		crossobj.visibility="hidden"
		if (crossMonthObj != null){crossMonthObj.visibility="hidden"}
		if (crossYearObj !=	null){crossYearObj.visibility="hidden"}

	    showElement( 'SELECT' );
		showElement( 'APPLET' );
	}
}

function requiresValidation(element) {
if(element == null) {
	return false;
}
    var form = document.forms[0];
    var superId = element.getAttribute("super");
    if (superId != null) {
        element = form[superId];
    }

    dtpattern = element.getAttribute("dtpattern");
    if (!isEmpty(dtpattern)) {
        return true;
    }

    required = element.getAttribute("dtrequired");
    if (!isEmpty(required) && (required == "true")) {
        return true;
    }

    var validationRequired = false;

    // determine datatype
    var dttype = element.getAttribute("dttype");
    switch (dttype) {
        case "string":
            validationRequired = !isEmpty(element.getAttribute("dtminlength")) ||
                       !isEmpty(element.getAttribute("dtmaxlength"));
            break;
        case "int":;
        case "long":;
        case "float":;
        case "double":
            validationRequired = !isEmpty(element.getAttribute("dtmin")) ||
                       !isEmpty(element.getAttribute("dtmax"));
            break;
        case "datetime":
        // Validation should always happen because the hidden date field
        // will be updated when the input boxes are valid.
                validationRequired = true;
            break;
        case "enum":
            break;
        default:
            validationRequired = requiresUnknown(element, form);
            break;
    }

    return validationRequired;
}
