/**
 * tools.jsp
 * Routines for reading and writing cookies
 *
 * @since    MMBase-1.6
 * @version  $Id: tools.js,v 1.8 2007-04-23 17:34:32 michiel Exp $
 * @author   Kars Veling
 * @author   Pierre van Rooden
 */


var COOKIE_ENTRY = "MMBase-EditWizard";
var entryLen = COOKIE_ENTRY.length;
var PAIR_SEP = "|";
var SEP = ":";

//function for reading a cookie
function readCookie_general(theWizard, theForm, theDefault) {
    try {    		
        var theCookie = unescape(getValueForName(document.cookie, COOKIE_ENTRY, ";", "="));
        if (theCookie == "")
            return theDefault;
        theValue = getValueForName(theCookie, theWizard + "-" + theForm, PAIR_SEP, SEP);
        //alert(theValue);
        if (theValue == "")
            return theDefault;
        else
            return theValue;
    } catch (e) {
        //alert("Error in readCookie_general('" + theWizard + "', '" + theForm + "', '" + theDefault + "'): " + e.description);
        return theDefault;
    }
}

//function for writing a cookie
function writeCookie_general(theWizard, theForm, theValue) {
    try {
        var theContent = unescape(getValueForName(document.cookie, COOKIE_ENTRY, ";", "="));
        //split the content into name:value pairs
        var cArray = theContent.split(PAIR_SEP);
        //make an associative name value array of them
        var nvs = new Array();
        for (var i=0; i<cArray.length; i++) {
            var name = cArray[i].substring(0, cArray[i].indexOf(SEP));
            if (name != "")
                nvs[name] = cArray[i].substring(cArray[i].indexOf(SEP) + 1, cArray[i].length);
        }
        //add the new cookie name/value pair
        nvs[theWizard + "-" + theForm] = theValue;
        //serialize it again
        var s = "";
        for (var n in nvs) {
        	if (typeof nvs[n] != "function") {
            	s += "|" + n + ":" + nvs[n];
          	}
          }
        //write the cookie
        // we don't set the expiredate to let the cookie expire when the browser is closed
        var c = COOKIE_ENTRY + "=" + s + "; path=/;";
        document.cookie = c;
    } catch (e) {
        alert("Error in writeCookie_general('" + theWizard + "-" + theForm + "', '" + theValue + "'): " + e.description);
    }
}

function clearCookie_general(theWizard) {
    try {
        var theContent = unescape(getValueForName(document.cookie, COOKIE_ENTRY, ";", "="));
        //split the content into name:value pairs
        var cArray = theContent.split(PAIR_SEP);
        //make an associative name value array of them
        var nvs = new Array();
        for (var i=0; i<cArray.length; i++) {
            var name = cArray[i].substring(0, cArray[i].indexOf(SEP));
            if (name != "")
                nvs[name] = cArray[i].substring(cArray[i].indexOf(SEP) + 1, cArray[i].length);
        }
        //serialize it again
        var s = "";
        for (var n in nvs) {
            if (n.indexOf(theWizard) == -1)
               if (typeof nvs[n] != "function") {
                	s += "|" + n + ":" + nvs[n];
               }
        }
        //write the cookie
        // we don't set the expiredate to let the cookie expire when the browser is closed
        var c = COOKIE_ENTRY + "=" + s + "; path=/;";
        document.cookie = c;
    } catch (e) {
        alert("Error in clearCookie_general('" + theWizard + "'): " + e.description);
    }
}

//Get value for name from the cookie
function getValueForName(str, name, pairsep, sep) {
    var search = name + sep;
    var returnvalue = "";
    if (str.length > 0) {
        offset = str.indexOf(search);
        // if cookie exists
        if (offset != -1) { 
            offset += search.length;
            // set index of beginning of value
            end = str.indexOf(pairsep, offset);
            // set index of end of cookie value
            if (end == -1)
                end = str.length;
            returnvalue = str.substring(offset, end);
        }
    }
    return returnvalue;
}

function getDimensions() {
    var dims = { };
    if (typeof window.innerHeight != 'undefined') {
        dims.windowWidth = window.innerWidth;
        dims.windowHeight = window.innerHeight;
        dims.documentWidth = window.document.width;
        dims.documentHeight = window.document.height;
    }
    else {
        if (window.document.body && typeof window.document.body.offsetWidth != 'undefined') {
            var doc = window.document;
            if (doc.compatMode && doc.compatMode != 'BackCompat') {
                dims.windowWidth = doc.documentElement.offsetWidth;
                dims.windowHeight = doc.documentElement.offsetHeight;
                dims.documentWidth = doc.documentElement.scrollWidth;
                dims.documentHeight = doc.documentElement.scrollHeight;
            }
            else {
                dims.windowWidth = doc.body.offsetWidth;
                dims.windowHeight = doc.body.offsetHeight;
                dims.documentWidth = doc.body.scrollWidth;
                dims.documentHeight = doc.body.scrollHeight;
            }
        }
    }
    return dims;
}

function findPosX(obj) {
    var curleft = 0;
    if (obj.offsetParent) {
        while (obj.offsetParent) {
            curleft += obj.offsetLeft;
            obj = obj.offsetParent;
        }
    }
    else {
        if (obj.x) {
            curleft += obj.x;
        }
    }
    return curleft;
}

function findPosY(obj) {
    var curtop = 0;
    if (obj.offsetParent) {
        while (obj.offsetParent) {
            curtop += obj.offsetTop
            obj = obj.offsetParent;
        }
    }
    else {
    	if (obj.y) {
            curtop += obj.y;
        }
    }
    return curtop;
}


// debug method
function checkDimensions (windowOrFrame) {
   var dims = getDimensions();
   if (typeof dims.windowWidth != 'undefined') {
     var text = 'window width: ' + dims.windowWidth + '\n';
     text += 'window height: ' + dims.windowHeight + '\n';
     text += 'document width: ' + dims.documentWidth + '\n';
     text += 'document height: ' + dims.documentHeight;
     alert(text);
   }
   else {
     alert('Unable to determine window dimensions.');
   }
}


function loadXMLDoc(url, async) {
    var req = false;
    // branch for native XMLHttpRequest object
    if(window.XMLHttpRequest && !(window.ActiveXObject)) {
        try {
            req = new XMLHttpRequest();
        } catch(e) {
            req = false;
        }
    // branch for IE/Windows ActiveX version
    } else if(window.ActiveXObject) {
        try {
            req = new ActiveXObject("Msxml2.XMLHTTP");
        } catch(e) {
            try {
                req = new ActiveXObject("Microsoft.XMLHTTP");
            } catch(e) {
                req = false;
            }
        }
    }
    if(req) {
        var as;
	if (async == undefined) {
	    as = true;
	} else {
            as = async;
        }		
        req.open("GET", url, as);
        req.send("");
    }
}

function heartbeat() {
    loadXMLDoc("heartbeat.jsp");
    setTimeout('heartbeat()',60*1000);
}
