<% response.setContentType("text/javascript"); %>
/**
 * editwizard.jsp
 * Routines for refreshing the edit wizard,
 * interaction between form elements, navigation,
 * and validation (in validator.js)
 *
 * @since    MMBase-1.6
 * @version  $Id$
 * @author   Kars Veling
 * @author   Pierre van Rooden
 */

var form = null;
var validator = new Validator();


// Here some date-related code that we need top determine if we're living within Daylight Saving Time
//
function makeArray()    {
    this[0] = makeArray.arguments.length;
    for (i = 0; i<makeArray.arguments.length; i++)
        this[i+1] = makeArray.arguments[i];
}

var daysofmonth   = new makeArray( 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
var daysofmonthLY = new makeArray( 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);

function LeapYear(year) {
    return ((year  % 4 == 0) && !( (year % 100 == 0) && (year % 400 != 0)));
}

function NthDay(nth,weekday,month,year) {
    if (nth > 0) return (nth-1)*7 + 1 + (7 + weekday - DayOfWeek((nth-1)*7 + 1,month,year))%7;
    if (LeapYear(year)) var days = daysofmonthLY[month];
    else                var days = daysofmonth[month];
    return days - (DayOfWeek(days,month,year) - weekday + 7)%7;
}

function DayOfWeek(day,month,year) {
    var a = Math.floor((14 - month)/12);
    var y = year - a;
    var m = month + 12*a - 2;
    var d = (day + y + Math.floor(y/4) - Math.floor(y/100) + Math.floor(y/400) + Math.floor((31*m)/12)) % 7;
    return d+1;
}

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
        if (hidden != "hidden" && firstfield==null) firstfield=elem;

        //handle complex data types
        var dttype = elem.getAttribute("dttype");
        var ftype = elem.getAttribute("ftype");
        switch (dttype) {
            case "datetime":
                if (elem.value == "") {
                    var d = new Date();
                    elem.value = Math.round(d.getTime()/1000);
                }

                if (elem.value && (elem.value != "")) {
                    var id = elem.name;
                    var d = new Date();

                    var ms = 1000*elem.value;
                    d.setTime(ms);
                    var year = d.getFullYear();

                    // Here we'll  calculate the start and end of Daylight Saving Time
                    // We need that in order to display correct date and times in IE on Macintosh
                    var DSTstart = new Date(year,4-1,NthDay(1,1,4,year),2,0,0);
                    var DSTend   = new Date(year,10-1,NthDay(-1,1,10,year),2,0,0);

                    var DSTstartMS = Date.parse(DSTstart);
                    var DSTendMS = Date.parse(DSTend);

                    // If Daylight Saving Time is active and clientNavigator=MSIE/Mac, add 60 minutes
                    //
                    if ((navigator.appVersion.indexOf('MSIE')!=-1) && (navigator.appVersion.indexOf('Mac')!=-1) && (ms>DSTstartMS) && (ms<DSTendMS)) {
                        d.setTime((1000*elem.value)+(1000*60*60));
                    }

                    if ((ftype == "datetime") || (ftype == "date")) {
                        form.elements["internal_" + id + "_day"].selectedIndex = d.getDate() - 1;
                        form.elements["internal_" + id + "_month"].selectedIndex = d.getMonth();
                        var y = d.getFullYear();
                        if (y <= 0) y--;
                        form.elements["internal_" + id + "_year"].value = y;
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

    // Start the htmlarea's.
    for (var i = 0; i < htmlAreas.length; i++) {
      var editor = new HTMLArea(htmlAreas[i]);
      customize(editor, "../htmlarea/");
      editor.generate();
      htmlAreas[i] = editor;
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

function doOnUnLoad_ew() {
    writeCookie_general("scrollTop", document.body.scrollTop);
    writeCookie_general("prevForm", document.forms[0].id);

    // debug code - uncomment for tracking
    // alert(readCookie_general("scrollTop") + ", " + document.body.scrollTop);
}

document.writeln('<div id="searchframe" class="searchframe"><iframe onblur="removeModalIFrame();" src="searching.html" id="modaliframe" class="searchframe" scrolling="no"></iframe></div>');

function doSearch(el, cmd, sessionkey) {
    saveHtmlAreas();
    // most of this is probably better to just pass to list.jsp...

    var searchfields = document.forms[0].elements["searchfields_" + cmd].value;
    var searchtype = document.forms[0].elements["searchtype_" + cmd].value;
    if (searchtype=="") searchtype="like";
    var searchage = new Number(document.forms[0].elements["searchage_" + cmd].value);
    var searchterm = document.forms[0].elements["searchterm_" + cmd].value+"";

    if (searchtype=="like") searchterm = searchterm.toLowerCase();

    var filterrequired = el.getAttribute("filterrequired");
    if (filterrequired=="true" && searchterm=="") {
        var form = document.forms["form"];
        var errmsg=form.getAttribute("filter_required")
        if (errmsg==null || errmsg=="") {
            errmsg="Entering a search term is required";
        }
        alert(errmsg);
        return;
    } // 11948878

    // recalculate age
    if (searchage == -1){
        searchage = 99999;
    }

    var searchdir = el.getAttribute("searchdir");
    var startnodes = el.getAttribute("startnodes");
    var nodepath   = el.getAttribute("nodepath");
    var fields     = el.getAttribute("fields");
    var constraints= el.getAttribute("constraints");
    var orderby    = el.getAttribute("orderby");
    var directions = el.getAttribute("directions");
    var distinct   = el.getAttribute("distinct");

    // lastobject is generally the last builder in the nodepath.
    // however, if the first field is a "<buildername>.number" field, that buildername is used

    var tmp=nodepath.split(",");
    var lastobject="";
    if (tmp.length>1) {
        lastobject=tmp[tmp.length-1];
        tmp=fields.split(",");
        if (tmp.length>1 && tmp[0].indexOf(".number") != -1) {
            lastobject=tmp[0].split(".")[0];
        }
    }

    // check constraints
    var cs = searchfields.split("|");
    if (constraints!="" && constraints) var constraints = "("+constraints+") AND (";
    else constraints = "(";
    for (var i=0; i<cs.length; i++) {
        if (i>0) constraints += " OR ";
        var fieldname=cs[i];
        if (fieldname.indexOf(".")==-1 && lastobject!="") fieldname = lastobject+"."+fieldname;

        if (searchtype=="string") {
            constraints += fieldname+" = '%25"+searchterm+"%25'";
        } else if (searchtype=="like") {
            constraints += "LOWER("+fieldname+") LIKE '%25"+searchterm+"%25'";
        } else {
            if (searchterm=="") searchterm="0";
            if (searchtype=="greaterthan") {
                constraints += fieldname + " > " + searchterm;
            } else if (searchtype=="lessthan") {
                constraints += fieldname + " < " + searchterm;
            } else if (searchtype=="notgreaterthan") {
                constraints += fieldname + " <= "+searchterm;
            } else if (searchtype=="notlessthan") {
                constraints += fieldname + " >= "+searchterm;
            } else if (searchtype=="notequals") {
                constraints += fieldname+" != "+searchterm;
            } else { // equals
                constraints += fieldname+" = "+searchterm;
            }
        }
        // make sure these fields are added to the fields-param, but not if its the number field
        //
        //if (fields.indexOf(fieldname)==-1 && fieldname.indexOf("number")==-1) {
        //    fields += "," + fieldname;
        //}
    }
    constraints += ")";

    // build url
    var url="<%= response.encodeURL("list.jsp")%>?proceed=true&popupid=search&replace=true&referrer=<%=request.getParameter("referrer")%>&template=xsl/searchlist.xsl&nodepath="+nodepath+"&fields="+fields+"&len=10&language=<%=request.getParameter("language")%>";
    url += setParam("sessionkey", sessionkey);
    url += setParam("startnodes", startnodes);
    url += setParam("constraints", constraints);
    url += setParam("searchdir", searchdir);
    url += setParam("orderby", orderby);
    url += setParam("directions", directions);
    url += setParam("distinct", distinct);
    url += setParam("age", searchage+"");
    url += setParam("type", el.getAttribute("type"));
    url += "&cmd=" + cmd;


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
    } catch(e) {}

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
    saveHtmlAreas();
    var fld = document.getElementById("hiddencmdfield");
    fld.name = "cmd/start-wizard/"+fieldid+"/"+dataid+"/"+objectnumber+"/"+origin+"/";
    fld.value = wizardname;
    document.forms[0].submit();
}

function doGotoForm(formid) {
    saveHtmlAreas();
    var fld = document.getElementById("hiddencmdfield");
    fld.name = "cmd/goto-form//"+formid+"//";
    fld.value = "";
    document.forms[0].submit();
    document.body.scrollTop = 0;
}

function doSendCommand(cmd, value) {
    saveHtmlAreas();
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
   var saveonlybut = document.getElementById("bottombutton-saveonly");
   if (saveonlybut != null) {
      saveonlybut.style.visibility = "hidden";
   }
}


function doCancel() {
    setButtonsInactive();
    doSendCommand("cmd/cancel////");
    document.body.scrollTop = 0;
}

function saveHtmlAreas() {
    for (var i = 0; i < htmlAreas.length; i++) {
      var editor = htmlAreas[i];
      if(editor._textArea) editor._textArea.value = editor.getHTML();
    }
}

function doSave() {
    saveHtmlAreas();
    var allvalid = doValidateAndUpdateButtons();
    if (allvalid) {
        setButtonsInactive();
        doSendCommand("cmd/commit////");
    }
    document.body.scrollTop = 0;
}

function doSaveOnly() {
    saveHtmlAreas();
    var allvalid = doValidateAndUpdateButtons();
    if (allvalid) {
        setButtonsInactive();
        doSendCommand("cmd/save////");
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

// forward compatibility with 1.7

function saveScroll() {
}

function restoreScroll() {
}


