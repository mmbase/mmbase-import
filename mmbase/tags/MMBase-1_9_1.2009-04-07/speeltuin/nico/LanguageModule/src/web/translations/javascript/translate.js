function doCancel(close) {
    setButtonsInactive();
    doSendCommand("cmd","cancel");    
    if (close) {
        window.close();
    }
}

function doSave(close) {
    var allvalid = doUpdateButtons();    
    if (allvalid) {
        setButtonsInactive();
        doSendCommand("cmd", "commit");
        if (close) {
            window.close();
        }
    }
}

function doOnSubmit(form) {
    if (!form.hasBeenSubmitted) {
        form.hasBeenSubmitted = true;
        return true;
    } else {
        return false;
    }
}

function doSendCommand(cmd, value) {
    doCheckWysiwyg();

    var fld = document.getElementById("hiddencmdfield");
    fld.name = cmd;
    fld.value = "";
    if (value) fld.value = value;
    document.forms[0].submit();
}

function doOnLoad_ew() {
    //signal the form hasn't been submitted yet
    document.forms[0].hasBeenSubmitted = false;

    //set variables
    var form = document.forms["form"];

    var firstfield = null;
    var s="";

    //scan form fields
    for (var i=0; i<form.elements.length; i++) {
        var elem = form.elements[i];

        // find first editible field
        var hidden = elem.getAttribute("type"); //.toLowerCase();
        if (hidden != "hidden" && firstfield==null) firstfield=elem;
    }

    //restore the scroll position
//    var st = readCookie_general("scrollTop", 0);
//    var pf = readCookie_general("prevForm", "-");

//    if (pf == document.forms[0].id) {
//        document.body.scrollTop = st;
//    } else {
        if (firstfield!=null) firstfield.focus();
//    }

    doUpdateButtons();
}

function doOnUnLoad_ew() {
//    writeCookie_general("scrollTop", document.body.scrollTop);
//    writeCookie_general("prevForm", document.forms[0].id);

    // debug code - uncomment for tracking
    // alert(readCookie_general("scrollTop") + ", " + document.body.scrollTop);
}

function setButtonsInactive() {
   var cancelbut = document.getElementById("bottombutton-cancel");
   // cancelbut.className = "invalid";
   cancelbut.style.visibility = "hidden";
   var savebut = document.getElementById("bottombutton-save");
   // savebut.className = "invalid";
   savebut.style.visibility = "hidden";
}

function doCheckWysiwyg() {
    try {
        if (wysiwyg) wysiwyg.blur();
    } catch (e) {}
}

function doUpdateButtons() {
    // check if current form is valid.
    var savebut = document.getElementById("bottombutton-save");

    savebut.className = "bottombutton";
    var usetext = getToolTipValue(savebut,"titlesave", "Stores all changes.");
    savebut.title = usetext;

    return true;
}

function getToolTipValue(el,attribname,defaultvalue,param) {
    var value = el.getAttribute(attribname);
    if (value==null || value=="") value=defaultvalue;
    if (param) {
        return value.replace(/(\{0\})/g, param);
    }
    return value;
}

function getWindowInfo() {
    var obj = new Object();
    obj.scrollTop = document.body.scrollTop;
    obj.scrollLeft = document.body.scrollLeft;
    obj.clientHeight = document.body.clientHeight;
    obj.clientWidth = document.body.clientWidth;

    if (navigator.appName.indexOf("etscape")>-1) {
        obj.scrollTop = window.scrollY;
        obj.scrollLeft = window.scrollX;
        obj.clientWidth = window.innerWidth;
        obj.clientHeight = window.innerHeight;
    }
    return obj;
}

// debug method
function showAllProperties(el, values) {
    var s = "";
    for (e in el) {
        s += e;
        if (values) s += "["+el[e]+"]";
        s += ", ";
    }
    alert(s);
}