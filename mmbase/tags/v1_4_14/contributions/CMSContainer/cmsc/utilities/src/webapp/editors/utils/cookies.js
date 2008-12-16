var PAIR_SEP = "|";
var SEP = ":";

//function for reading a cookie
function readCookie(theCookieName, theKey, theDefault) {
    try {
        var theCookie = unescape(getValueForName(document.cookie, theCookieName, ";", "="));
        if (theCookie == "")
            return theDefault;
        theValue = getValueForName(theCookie, theKey, PAIR_SEP, SEP);
        //alert(theValue);
        if (theValue == "")
            return theDefault;
        else
            return theValue;
    } catch (e) {
        //alert("Error in readCookie('" + theCookieName + "', '" + theKey + "', '" + theDefault + "'): " + e.description);
        return theDefault;
    }
}

//function for writing a cookie
function writeCookie(theCookieName, theKey, theValue) {
    try {
        var theContent = unescape(getValueForName(document.cookie, theCookieName, ";", "="));
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
        nvs[theKey] = theValue;
        //serialize it again
        var s = "";
        for (var n in nvs) {
            if (typeof nvs[n] == "string") {
                s += "|" + n + ":" + nvs[n];
            }
        }
        //write the cookie
        // we don't set the expiredate to let the cookie expire when the browser is closed
        var c = theCookieName + "=" + s + "; path=/;";
        document.cookie = c;
    } catch (e) {
        alert("Error in writeCookie('" + theCookieName + "-" + theKey + "', '" + theValue + "'): " + e.description);
    }
}

function clearCookie(theCookieName, theKey) {
    try {
        var theContent = unescape(getValueForName(document.cookie, theCookieName, ";", "="));
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
            if (typeof nvs[n] == "string") {
	            if (n.indexOf(theKey) == -1) {
	                s += "|" + n + ":" + nvs[n];
	            }
	        }
        }
        //write the cookie
        // we don't set the expiredate to let the cookie expire when the browser is closed
        var c = theCookieName + "=" + s + "; path=/;";
        document.cookie = c;
    } catch (e) {
        alert("Error in clearCookie('" + theKey + "'): " + e.description);
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