var PAIR_SEP = "|";
var SEP = ":";

//function for checking if a radio button has been selected
function vote(myform, cookieName) {
	myOption = -1;
	for (i=myform.pollChoiceNumber.length-1; i > -1; i--) {
		if (myform.pollChoiceNumber[i].checked) {
			myOption = i;
		}
	}
	if (myOption == -1) {
		return false;
	}
	else
	{
		writePersistentCookie(cookieName, "vote", "set", 10);
		myform.submit(); 
	}
}

//function for writing a persistent cookie
function writePersistentCookie(theCookieName, theKey, theValue, nrYears) {
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
        //write the persistent cookie        
	  var currentDate = new Date();
	  var expiryDate = new Date ( currentDate.getFullYear() + nrYears, currentDate.getMonth(), currentDate.getDay() );
        var c = theCookieName + "=" + s + "; expires=" + expiryDate.toGMTString() +  "; path=/;";
        document.cookie = c;
    } catch (e) {
        alert("Error in writeCookie('" + theCookieName + "-" + theKey + "', '" + theValue + "'): " + e.description);
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