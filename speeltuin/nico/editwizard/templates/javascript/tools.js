/**
 * tools.jsp
 * Routines for reading and writing cookies
 *
 * @since    MMBase-1.6
 * @version  $Id: tools.js,v 1.1 2003-11-30 19:53:52 nico Exp $
 * @author   Kars Veling
 * @author   Pierre van Rooden
 */

//function for reading a cookie
function readCookie_general(theName, theDefault) {
    try {
        var theCookie = document.cookie + "|;";
        var p = theCookie.indexOf("MMBase-EditWizard=");
        if (p == -1) return theDefault;
        theCookie = theCookie.substring(p, theCookie.indexOf(";", p)) + "|";
        var pos = theCookie.indexOf(theName + ":");
        if (pos == -1) return theDefault;
        //alert("theCookie = " + theCookie);
        theValue = unescape(theCookie.substring(pos+theName.length+1, theCookie.indexOf("|", pos)));
        //alert(theValue);
        if (theValue == "") return theDefault;
        else return theValue;
    } catch (e) {
        //alert("Error in readCookie_general('" + theName + "', '" + theDefault + "'): " + e.description);
        return theDefault;
    }
}

//function for writing a cookie
function writeCookie_general(theName, theValue) {
    try {
        //get the cookie
        var theCookie = document.cookie;

        //get the content of the cookie
        if (theCookie != "") var theContent = theCookie.substring(theCookie.indexOf("=") + 2, theCookie.length - 1);
        else var theContent = "";

        //split the content into name:value pairs
        var cArray = theContent.split("|");

        //make an associative name value array of them
        var nvs = new Array();
        for (var i=0; i<cArray.length; i++) {
            if (cArray[i].substring(0, cArray[i].indexOf(":")) != "")
                nvs[cArray[i].substring(0, cArray[i].indexOf(":"))] = cArray[i].substring(cArray[i].indexOf(":") + 1, cArray[i].length);
        }

        //add the new cookie name/value pair
        nvs[theName] = theValue;

        //serialize it again
        var s = "";
        for (var n in nvs) s += "|" + n + ":" + nvs[n];

        //write the cookie

		//var nextYear = new Date();
        //nextYear.setFullYear(nextYear.getFullYear()+1);
		//var c = "MMBase-EditWizard=" + s + "|; expires=" + nextYear.toGMTString() + "; path=/;";
        var c = "MMBase-EditWizard=" + s + "|; path=/;"; 
        document.cookie = c;

    } catch (e) {
        alert("Error in writeCookie_general('" + theName + "', '" + theValue + "'): " + e.description);
    }
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
            curleft += obj.offsetLeft
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
