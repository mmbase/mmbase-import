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

function setWorkflowCommand(command) {
   var workflowCommand = document.getElementById("workflowcommand");
   if (workflowCommand) {
      workflowCommand.value = command;
   }
}

function setWorkflowComment() {
   var workflowComment = document.getElementById("workflowcomment");
   if (workflowComment) {
      var comment = prompt("Opmerking ?","");
      if (comment != null && comment != ""){ // OK pressed
         workflowComment.value = comment;
      }
   }
}

function doFinish() {
    if (!isSaveInactive()) {
        setWorkflowCommand("finish");
        clearScroll();
        setButtonsInactive();
        doSendDelayedCommand("cmd/commit////");
    }
}

function doAccept() {
    if (!isSaveInactive()) {
        setWorkflowCommand("accept");
        clearScroll();
        setButtonsInactive();
        doSendDelayedCommand("cmd/commit////");
    }
}

function doPublish() {
    if (!isSaveInactive()) {
        setWorkflowCommand("publish");
        clearScroll();
        setButtonsInactive();
        doSendDelayedCommand("cmd/commit////");
    }
}

function doReject() {
    setWorkflowComment();
    setWorkflowCommand("reject");
    clearScroll();
    setButtonsInactive();
    doSendDelayedCommand("cmd/cancel////");
}

function doCancel() {
    setWorkflowCommand("cancel");
    clearScroll();
    setButtonsInactive();
    doSendDelayedCommand("cmd/cancel////");
}

function updateHtml(el, err, silent) {
    updateErrormesg(el, err, silent)
    updatePrompt(el, err, silent);
}

function updateErrormesg(el, err, silent) {
    var prompt = document.getElementById("errormesg_" + el.name);
    if (prompt && !silent) {
        var orgprompt = prompt.getAttribute("prompt");
        var description = prompt.getAttribute("description");
        if (err.length > 0) {
            prompt.innerHTML = err;
            
        } else {
            prompt.innerHTML = "";
        }
    }
}

function updateButtons(allvalid) {
    var savebut = document.getElementById("bottombutton-save");
    var saveonlybut = document.getElementById("bottombutton-saveonly");
    if (allvalid) {
        setSaveInactive("false");
        enableButton(savebut,"titlesave", "Stores all changes.");
        if (saveonlybut != null) {
           enableButton(saveonlybut,"titlesave", "Stores all changes.");
        }
        enableButton(document.getElementById("bottombutton-finish"), "titlefinish", "Store all changes");
        enableButton(document.getElementById("bottombutton-accept"), "titleaccept", "Store all changes");
        enableButton(document.getElementById("bottombutton-reject"), "titlereject", "Store all changes");
        enableButton(document.getElementById("bottombutton-publish"), "titlepublish", "Store all changes");
    } else {
        setSaveInactive("true");
        disableButton(savebut,"titlenosave", "You cannot save because one or more forms are invalid.");
        if (saveonlybut != null) {
           disableButton(saveonlybut,"titlenosave", "You cannot save because one or more forms are invalid.");
        }
        disableButton(document.getElementById("bottombutton-finish"),"titlenofinish", "The changes cannot be saved, since some data is not filled in correctly.");
        disableButton(document.getElementById("bottombutton-accept"),"titlenoaccept", "The changes cannot be saved, since some data is not filled in correctly.");
        disableButton(document.getElementById("bottombutton-reject"),"titlenoreject", "The changes cannot be saved, since some data is not filled in correctly.");
        disableButton(document.getElementById("bottombutton-publish"),"titlenopublish", "The changes cannot be saved, since some data is not filled in correctly.");
    }
}

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
            //textareas[i].style.width = docWidth - 355;
            textareas[i].style.width = '99%';
        }
        else {
            //removetextareas[i].style.width = docWidth - 100;
            textareas[i].style.width = '99%';
        }
    }
}
