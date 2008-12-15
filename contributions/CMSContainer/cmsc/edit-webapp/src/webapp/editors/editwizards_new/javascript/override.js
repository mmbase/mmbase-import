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

function inits(){
   var inputs =document.getElementsByTagName("input");
   var calvalue = "";
   var message = "";
   for (var i = 0; i < inputs.length ;i++ ) {
      if(inputs[i].title == 'new-calendar') {
         calvalue = inputs[i].value;
         break;
      }
   }

  if(calvalue == null  || calvalue == "") {
    return;
  }

  var calendartype =document.getElementById("calendar-type");
  var calendarexpression =document.getElementById("calendar-expression");

   var expression = calvalue.split('|');
     var type;
     if(expression != null && expression.length >0) {
       type = expression[0];
     }
     calendartype.options[type].selected = true;
     if(type == '1') {
        message += "Once,start datetime:"+expression[1]+" "+expression[2]+":"+expression[3];
     }
     else if(type == '2') {
        message += "Per day,start datetime:"+expression[1]+" "+expression[2]+":"+expression[3];
        if(expression[4] == "0") {
            message += "<br/>  dayly"; 
         }
         else if(expression[4] == "1") {
            message += "<br/>  weekday"; 
         }
          else if(expression[4] == "2") {
            message += "<br/>   frequency: "+expression[5] +" day(s) "; 
         }
     }
     else if(type == '3') {
         message += "Per week,start time:"+expression[1]+":"+expression[2];
         message += "<br/> frequency: "+expression[3]+" week(s) ";

        var varWeek = "";
        for(var i = 0 ; i < expression[4].length;i++) {
            var month = expression[4].substr(i,1);
               if(month == "1") {
                  varWeek += "Monday,";
               }
               else if(month == "2") {
                  varWeek += "Tuesday,";
               }
               else if(month == "3") {
                  varWeek += "Wednesday,";
               }
               else if(month == "4") {
                  varWeek += "Thursday,";
               }
               else if(month == "5") {
                  varWeek += "Friday,";
               }
               else if(month == "6") {
                  varWeek += "Saturday,";
               }
               else if(month == "7") {
                  varWeek += "Sunday,";
               }
        }
        if(varWeek != null && varWeek != ""){
           if(varWeek.substr(varWeek.length-1,1) ==","){
              varWeek = varWeek.substr(0,varWeek.length-1) ;
           }
        }
        message += "<br/> week: "+varWeek;
     }
     else if(type == '4') {
         message += "Per month,start time:"+expression[1]+":"+expression[2];
         var months = "";
          if(expression[3] == "0") {
            message += "<br/> months: "+expression[4]+"";
             if(expression[4] == "1") {
               message += "st";
            }
            else if (expression[4] == "2") {
               message += "nd";
            }
            else if (expression[4] == "3") {
               message += "rd";
            }
            else {
               message += "th";  
            }
            months = expression[5];
         }
         else if(expression[3] == "1") {
            message += "<br/> Week: ";

            if(expression[4] == '1') {
               message += "the First Week,";
            }
            else if (expression[4] == '2') {
               message += "the Second Week,";
            }
            else if (expression[4] == '3') {
               message += "the Third Week,";
            }
            else if (expression[4] == '4') {
               message += "the Forth Week,";
            }
            else if (expression[4] == '5') {
               message += "the Last Week,";
            }

            if(expression[5] == '1') {
               message += "Monday.";
            }
            else if (expression[5] == '2') {
               message += "Tuesday.";
            }
            else if (expression[5] == '3') {
               message += "Wednesday.";
            }
            else if (expression[5] == '4') {
               message += "Thursday.";
            }
            else if (expression[5] == '5') {
               message += "Friday.";
            }
            else if (expression[5] == '6') {
               message += "Saturday.";
            }
            else if (expression[5] == '7') {
               message += "Sunday.";
            }
             months = expression[6];
         }
         var temp = "";
         for(var i = 0 ; i < months.length;i++) {
              var month = months.substr(i,1);
               if(month == "0") {
                  temp+="January,";
               }
               else if(month == "1") {
                  temp+="February,";
               }
               else if(month == "2") {
                  temp+="March,";
               }
               else if(month == "3") {
                  temp+="April,";
               }
               else if(month == "4") {
                  temp+="May,";
               }
               else if(month == "5") {
                  temp+="June,";
               }
               else if(month == "6") {
                  temp+="July,";
               }
               else if(month == "7") {
                  temp+="August,";
               }
               else if(month == "8") {
                  temp+="September,";
               }
               else if(month == "9") {
                  temp+="October,";
               }
               else if(month == "a") {
                  temp+="November,";
               }
               else if(month == "b") {
                  temp+="December,";
               }
        }
        if(temp != null && temp != ""){
           if(temp.substr(temp.length-1,1) ==","){
              temp = temp.substr(0,temp.length-1) ;
           }
        }
        message += "<br/> Month: "+temp;

     }
  calendarexpression.innerHTML  = message;
}

function resetCalendar(calendarType,fieldName) {
   if(calendarType == '0') {
      document.getElementById('calendar-expression').innerHTML='';
      document.getElementById(fieldName).value='';
   }
}