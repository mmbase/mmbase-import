<% response.setContentType("text/javascript"); %>
/**
 * editwizard.jsp
 * Routines for refreshing the edit wizard,
 * interaction between form elements, navigation,
 * and validation (in validator.js)
 *
 * @since    MMBase-1.6
 * @version  $Id: editwizard.jsp,v 1.21 2002-09-06 13:30:34 pierre Exp $
 * @author   Kars Veling
 * @author   Pierre van Rooden
 */

var form = null;
var validator = new Validator();

function doOnLoad_ew() {
    //signal the form hasn't been submitted yet
    document.forms[0].hasBeenSubmitted = false;

    //set variables
    form = document.forms["form"];

    var firstfield = null;
    var s="";

    //scan form fields
    for (var i=0; i<form.elements.length; i++) {
        var elem = form.elements[i];

        // find first editible field
        var hidden = elem.getAttribute("type"); //.toLowerCase();
        if (hidden!="hidden" && firstfield==null) firstfield=elem;

        //handle complex data types
        var dttype = elem.getAttribute("dttype");
        var ftype = elem.getAttribute("ftype");
        switch (dttype) {
            case "datetime":
                if ((elem.value == "") || (elem.value <= 0)) {
                    var d = new Date();
                    elem.value = Math.round(d.getTime()/1000);
                }

                if (elem.value && (elem.value != "")) {
                    var id = elem.name;
                    var d = new Date();
                    d.setTime(1000*elem.value);

                    if ((ftype == "datetime") || (ftype == "date")) {
                        form.elements["internal_" + id + "_day"].selectedIndex = d.getDate() - 1;
                        form.elements["internal_" + id + "_month"].selectedIndex = d.getMonth();
                        form.elements["internal_" + id + "_year"].value = d.getFullYear();
                    }
                    if ((ftype == "datetime") || (ftype == "time")) {
                        form.elements["internal_" + id + "_hours"].value = d.getHours();
                        form.elements["internal_" + id + "_minutes"].value = d.getMinutes();
                    }
                }
                break;
        }

        validator.attach(elem);
    }

    //restore the scroll position
    var st = readCookie_general("scrollTop", 0);
    var pf = readCookie_general("prevForm", "-");
    if (pf == document.forms[0].id) {
        document.body.scrollTop = st;
    } else {
        if (firstfield!=null) firstfield.focus();
    }

    doValidateAndUpdateButtons();
}

function doOnSubmit(form) {
    if (!form.hasBeenSubmitted) {
        form.hasBeenSubmitted = true;
        return true;
    } else {
        return false;
    }
}

function doOnUnLoad_ew() {
    writeCookie_general("scrollTop", document.body.scrollTop);
    writeCookie_general("prevForm", document.forms[0].id);

    // debug code - uncomment for tracking
    // alert(readCookie_general("scrollTop") + ", " + document.body.scrollTop);
}

document.writeln('<div id="searchframe" class="searchframe"><iframe onblur="removeModalIFrame();" src="searching.html" id="modaliframe" class="searchframe" scrolling="no"></iframe></div>');

function doSearch(el, cmd, sessionkey) {
    var searchfields = document.forms[0].elements["searchfields_" + cmd].value;
    var searchage = new Number(document.forms[0].elements["searchage_" + cmd].value);
    var searchterm = document.forms[0].elements["searchterm_" + cmd].value+"";

    searchterm = searchterm.toLowerCase();

    var filterrequired = el.getAttribute("filterrequired");
    if (filterrequired=="true" && searchterm=="") {
        var form = document.forms["form"];
        var errmsg=form.getAttribute("filter_required")
        if (errmsg==null || errmsg=="") {
            errmsg="Entering a search term is required";                        
        }
        alert(errmsg);
        return;
    }

    // recalculate age
    var oneday = 24 * 60 * 60;
    if (searchage == -1){
        searchage = 99999;
    }

    var startnodes = el.getAttribute("startnodes");
    var nodepath   = el.getAttribute("nodepath");
    var fields     = el.getAttribute("fields");
    var constraints= el.getAttribute("constraints");
    var orderby    = el.getAttribute("orderby");
    var directions = el.getAttribute("directions");
    var distinct   = el.getAttribute("distinct");

    var tmp=nodepath.split(",");
    var lastobject="";
    if (tmp.length>1) lastobject=tmp[tmp.length-1];

    // check constraints
    var cs = searchfields.split("|");
    if (constraints!="" && constraints) var constraints = "("+constraints+") AND (";
    else constraints = "(";
    for (var i=0; i<cs.length; i++) {
        if (i>0) constraints += " OR ";
        var fieldname=cs[i];
        if (fieldname.indexOf(".")==-1 && lastobject!="") fieldname = lastobject+"."+fieldname;
        constraints += "LOWER("+fieldname+") LIKE '%25"+searchterm+"%25'";

        // make sure these fields are added to the fields-param
        if (fields.indexOf(fieldname)==-1) fields += ","+fieldname;
    }
    constraints += ")";

    // build url
    var url="<%= response.encodeURL("list.jsp")%>?proceed=true&popupid=search&replace=true&referrer=<%=request.getParameter("referrer")%>&template=xsl/searchlist.xsl&nodepath="+nodepath+"&fields="+fields+"&len=10&language=<%=request.getParameter("language")%>";
    url += setParam("sessionkey", sessionkey);
    url += setParam("startnodes", startnodes);
    url += setParam("constraints", constraints);
    url += setParam("orderby", orderby);
    url += setParam("directions", directions);
    url += setParam("distinct", distinct);
    url += setParam("age", searchage+"");
    url += setParam("type", el.getAttribute("type"));
    url += "&cmd="+cmd;


    try {
        window.status = "...searching...";
    } catch(e) {}

    var mif = document.getElementById("modaliframe");
    if (!window.frames[0] || !window.frames[0].document || (window.frames[0].document.location.href.indexOf(url) == -1)) {
        if (window.frames[0] && window.frames[0].document) {
            window.frames[0].document.open();
            window.frames[0].document.writeln('<link rel="stylesheet" type="text/css" href="../style/base.css" /><span>...searching...</span>');
            window.frames[0].document.close();
        }

        if (window.frames[0] && window.frames[0].document) window.frames[0].document.location.replace(url);
        else mif.src = url;
    }

    var stel = document.forms[0].elements["searchterm_" + cmd];
    var sf = document.getElementById("searchframe");
    var windowinfo = getWindowInfo();

    var xcorr = 0;
    var ycorr = 0;

    if (navigator.appName.indexOf("etscape")>-1) {
        // netscape (6), any platform
        xcorr = 0;
        ycorr = 2;
    } else if (navigator.appVersion.indexOf("Win")>-1) {
        // windows platform, not netscape
        xcorr = 5;
        ycorr = 4;
    } else {
        // other platforms, IE
        xcorr = 14;
        ycorr = 16;
    }

    var obj = getRect(stel);
    stbcrTop = obj.top + ycorr;
    stbcrLeft = obj.left + xcorr - 400 + stel.offsetWidth;
    if (stbcrLeft < 0) stbcrLeft = 0;
    stbcrBottom = stbcrTop + stel.offsetHeight;
    if (stbcrTop + mif.offsetHeight > windowinfo.clientHeight + windowinfo.scrollTop) {
        var t = stbcrBottom - 2 - mif.offsetHeight;
        if (t < 20) t = 20;
        sf.style.top = t;
    } else {
        sf.style.top = stbcrTop - 2;
    }
    sf.style.left = stbcrLeft - 2;

    sf.style.visibility = "visible";
}

function removeModalIFrame() {
        try {
        window.status = "";
        }
        catch(e) {}

    var mif = document.getElementById("modaliframe");
    if (window.frames[0] && window.frames[0].document) window.frames[0].document.location.replace("searching.html");
    else mif.src = "searching.html";

    var sf = document.getElementById("searchframe");
    if (sf) {
        sf.style.top = -400;
        sf.style.left = -400;
        sf.style.visibility = "hidden";
    }
}

function doAdd(s, cmd) {
    removeModalIFrame();
    if (!s || (s == "")) return;
    doSendCommand(cmd, s);
}

function doStartWizard(fieldid,dataid,wizardname,objectnumber,origin) {
    doCheckWysiwyg();
    var fld = document.getElementById("hiddencmdfield");
    fld.name = "cmd/start-wizard/"+fieldid+"/"+dataid+"/"+objectnumber+"/"+origin+"/";
    fld.value = wizardname;
    document.forms[0].submit();
}

function doGotoForm(formid) {
    doCheckWysiwyg();
    var fld = document.getElementById("hiddencmdfield");
    fld.name = "cmd/goto-form//"+formid+"//";
    fld.value = "";
    document.forms[0].submit();
}

function doSendCommand(cmd, value) {
    doCheckWysiwyg();

    var fld = document.getElementById("hiddencmdfield");
    fld.name = cmd;
    fld.value = "";
    if (value) fld.value = value;
    document.forms[0].submit();
}

function getRect(el, obj) {
    if (!obj) {
        obj = new Object();
        obj.top=0; obj.left=0;
    }

    obj.top += el.offsetTop;
    obj.left += el.offsetLeft;
    if (el.offsetParent) {
        getRect(el.offsetParent, obj);
    }
    return obj;
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

function setButtonsInactive() {
   var cancelbut = document.getElementById("bottombutton-cancel");
   // cancelbut.className = "invalid";
   cancelbut.style.visibility = "hidden";
   var savebut = document.getElementById("bottombutton-save");
   // savebut.className = "invalid";
   savebut.style.visibility = "hidden";
}


function doCancel() {
    setButtonsInactive();
    doSendCommand("cmd/cancel////");
}

function doSave() {
    var allvalid = doValidateAndUpdateButtons();
    if (allvalid) {
        setButtonsInactive();
        doSendCommand("cmd/commit////");
    }
}

function setParam(name, value) {
    if (value!="" && value!=null) return "&"+name+"="+value;
    return "";
}

function doStartUpload(el) {
    var href = el.getAttribute("href");
    window.open(href,null,"width=300,height=300,status=yes,toolbar=no,titlebar=no,scrollbars=no,resizable=no,menubar=no,top=100,left=100");

    return false;
}

function doRefresh() {
    doSendCommand("","");
}

function doCheckWysiwyg() {
    try {
        if (wysiwyg) wysiwyg.blur();
    } catch (e) {}
}
