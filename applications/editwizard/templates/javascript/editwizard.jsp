<% response.setContentType("text/javascript"); %>
/**
 * editwizard.jsp
 * Routines for refreshing the edit wizard,
 * interaction between form elements, navigation,
 * and validation (in validator.js)
 *
 * @since    MMBase-1.6
 * @version  $Id: editwizard.jsp,v 1.35 2003-12-19 11:09:08 nico Exp $
 * @author   Kars Veling
 * @author   Pierre van Rooden
 * @author   Nico Klasens
 */

var form = null;

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

        //handle complex data types
        var dttype = elem.getAttribute("dttype");
        var ftype = elem.getAttribute("ftype");
        switch (dttype) {
            case "datetime":
                if (elem.value == "" || (elem.value == -1)) {
                    var d = new Date();
                    elem.value = Math.round(d.getTime()/1000);
                }

                if (elem.value && (elem.value != "")) {
                    var d = getDate(elem.value);
                    var id = elem.name;

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
    }

    resizeEditTable();
    restoreScroll();
}

function doOnUnLoad_ew() {
    saveScroll();
}

//********************************
// COMMAND STUFF
//********************************

function doHelp() {
	var w=window.open("","Help", "width=350 height=400 scrollbars=yes toolbar=no statusbar=no resizable=yes");
	try {
		var str=document.getElementById("help_text").innerHTML;

		w.document.writeln('<html><head>');
		w.document.writeln('<link rel="stylesheet" href="../style/layout/help.css">');
		w.document.writeln('<link rel="stylesheet" href="../style/color/help.css">');
		w.document.writeln('</head><body>');
		w.document.writeln(str);
		w.document.writeln('</body></html>');
	} catch (e) {
		w.close();
	}
}

function doSearch(el, cmd, sessionkey) {
    // most of this is probably better to just pass to list.jsp...
    var searchfields = document.forms[0].elements["searchfields_" + cmd].value;
    var searchtype = document.forms[0].elements["searchtype_" + cmd].value;
    if (searchtype=="") searchtype="like";
    var searchage = -1;
    if (document.forms[0].elements["searchage_" + cmd]) {
       searchage = new Number(document.forms[0].elements["searchage_" + cmd].value);
    }
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
    url += setParam("orderby", orderby);
    url += setParam("directions", directions);
    url += setParam("distinct", distinct);
    url += setParam("age", searchage+"");
    url += setParam("type", el.getAttribute("type"));
    url += "&cmd=" + cmd;

	showSearchScreen(cmd, url);
}

function showSearchScreen(cmd, url) {
	alert("Add the searchwindow.js or searchiframe.js to the wizard.xsl");
}

function doStartWizard(fieldid,dataid,wizardname,objectnumber,origin) {
    doCheckHtml();
    
    var fld = document.getElementById("hiddencmdfield");
    fld.name = "cmd/start-wizard/"+fieldid+"/"+dataid+"/"+objectnumber+"/"+origin+"/";
    fld.value = wizardname;
    document.forms[0].submit();
}

function doGotoForm(formid) {
    doCheckHtml();
        
    var fld = document.getElementById("hiddencmdfield");
    fld.name = "cmd/goto-form//"+formid+"//";
    fld.value = "";
    document.forms[0].submit();
    document.body.scrollTop = 0;
}

function doSendCommand(cmd, value) {
    doCheckHtml();
    
    var fld = document.getElementById("hiddencmdfield");
    fld.name = cmd;
    fld.value = "";
    if (value) fld.value = value;
    document.forms[0].submit();
}

function doAdd(s, cmd) {
    if (!s || (s == "")) return;
    doSendCommand(cmd, s);
}

function doCancel() {
    setButtonsInactive();
    doSendCommand("cmd/cancel////");
    cleanScroll();
}

function doSave() {
    doCheckHtml();
    
    var savebut = document.getElementById("bottombutton-save");
    if (!savebut.disabled) {
        setButtonsInactive();
        doSendCommand("cmd/commit////");
        cleanScroll();
    }
}

function doSaveOnly() {
    doCheckHtml();
    
    var savebut = document.getElementById("bottombutton-save");
    if (!savebut.disabled) {
        setButtonsInactive();
        doSendCommand("cmd/save////");
    }
}

function doRefresh() {
    doSendCommand("","");
}

function doStartUpload(el) {
    var href = el.getAttribute("href");
    window.open(href,null,"width=300,height=300,status=yes,toolbar=no,titlebar=no,scrollbars=no,resizable=no,menubar=no,top=100,left=100");

    return false;
}

//********************************
// MISC STUFF
//********************************

function resizeEditTable() {
    var docHeight = getDimensions().windowHeight;
    var divButtonsHeight = document.getElementById("commandbuttonbar").offsetHeight;
    var divTop = findPosY(document.getElementById("edit_table"));

    document.getElementById("edit_table").style.height = docHeight - (divTop + divButtonsHeight);
}

function setParam(name, value) {
    if (value!="" && value!=null) return "&"+name+"="+value;
    return "";
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

function doCheckHtml() {
}

//********************************
// ITEMROW STUFF
//********************************
function objMouseOver(el) {
   el.className="itemrow-hover";
}

function objMouseOut(el) {
   el.className="itemrow";
}

function objClick(el) {
   var href = el.getAttribute("href")+"";
   var target = el.getAttribute("target")+"";
   
   if (href.length<10) return;
   if (target == "_blank") {
      window.open(href,"");
   }
   else {
      document.location=href;
   }
}

//********************************
// SCROLLBAR STUFF
//********************************

var cleanupScroll = false;

function restoreScroll() {
    var st = readCookie_general("scrollTop", 0);
    var pf = readCookie_general("prevForm", "-");
    if (pf == document.forms[0].id) {
        document.getElementById("edit_table").scrollTop = st;
    } else {
        form = document.forms["form"];
        for (var i=0; i<form.elements.length; i++) {
            var elem = form.elements[i];
            // find first editible field
            var hidden = elem.getAttribute("type"); //.toLowerCase();
            if (hidden != "hidden") {
                elem.focus();
                break;
            }
        }
    }
}

function saveScroll() {
	if (!cleanupScroll) {
	    writeCookie_general("scrollTop", document.getElementById("edit_table").scrollTop);
	    writeCookie_general("prevForm", document.forms[0].id);
	} else {
		writeCookie_general("scrollTop", 0);
		writeCookie_general("prevForm", 0);
	}
}

function cleanScroll() {
    cleanupScroll = true;
}

//********************************
// UNUSED STUFF AT THE MOMENT
//********************************
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