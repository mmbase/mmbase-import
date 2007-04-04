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