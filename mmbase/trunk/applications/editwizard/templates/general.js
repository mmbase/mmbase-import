//function showError(err) {
//function getCSSClasses_general() {
//function serializeAttributes_pageeditor(el) {
//function buildcall_pageeditor(script, action) {
//function isEnabled_pageeditor(func, type) {
//function loadxml_general(xmlFile) {
//function getHTTP_general(url) {
//function getParameter_general(name, defaultValue)
//function unescape_general(html)
//function escape_general(html)
//function getObjectTop_general(o)
//function getObjectLeft_general(o)
//function getObjectBottom_general(o)
//function getObjectRight_general(o)
//function readCookie_general(theName, theDefault)
//function writeCookie_general(theName, theValue)
//function hlimg_general(img, highlight, postfix)
//function mapArray_general(array, processor)

function showError(err) {
	dialogengine_lime.error = err;
	dialogengine_lime.setStone("error");
}

function getCSSClasses_general() {
	var cns = new Array();
	var sss = document.styleSheets;
	for (var i=0; i<sss.length; i++) {
		if (sss[i].createdByLime) continue;
		for (var r=0; r<sss[i].rules.length; r++) {
			var t = sss[i].rules[r].selectorText;
			t = t.replace(/[\s]+/g, " ");
			t = t.split(" ");
			for (var c=0; c<t.length; c++)
				if ((p = t[c].indexOf(".")) != -1) cns[cns.length] = t[c].substring(p+1);
		}
	}
	return cns;
}

function buildcall_pageeditor(script, action) {
	return top.settings["path2lime"] + top.settings["serverScript"] + "/" + script + "." + top.settings["serverScript"] +
		"?action=" + escape(action) +
		"&oridocloc=" + escape(document.location) +
		"&path2root=" + escape(top.settings["path2root"]) +
		"&path2lime=" + escape(top.settings["path2lime"]) +
		"&defaultdocument=" + escape(top.settings["defaultDocument"]);
}

function serializeAttributes_pageeditor(el) {
	var s = "";

	mapArray_general(el, function(p, v) { var pos = p.indexOf("_q42"); if ((pos != -1) && (pos == p.length-4)) s += " " + p + "=\"" + v + "\""; } );
	return s;
}

//////////////////////////
//	Enabling code
function isEnabled_pageeditor(func, type) {
	try {
		func = "|" + func.toLowerCase() + "|";
		if (!type) type = "*";
		type = "|" + type.toLowerCase() + "|";

		//make one identifying string for the rule to look for
		var srule = func + "--" + type;
		if (top.settings["enablingcache"][srule]) return (top.settings["enablingcache"][srule] == "yes");

		var enabled = true;
		for (var r=(top.settings["enablingrules"].length-1); r>=0; r--) {
			var rule = top.settings["enablingrules"][r];
			if (!rule) continue;
			if ((rule["func"].indexOf(func) == -1) && (rule["func"].indexOf("|*|") == -1)) continue;
			if ((rule["type"].indexOf(type) == -1) && (rule["type"].indexOf("|*|") == -1)) continue;
		
			enabled = rule["enabled"];
			break;
		}

		//put in cache
		top.settings["enablingcache"][srule] = enabled ? "yes" : "no";
	} catch (e) {
		return false;
	}
	return enabled;
}

function loadxml_general(xmlFile) {
	////////////////////////////////////////////////
	// loads an xml file and returns an XMLDOM object
	////////////////////////////////////////////////
//	var oldstatus = status;
//	status = "...loading XML ('" + xmlFile + "')...";

	var domid = "Microsoft.XMLDOM";
	try {
		var xml = new ActiveXObject("MSXML2.DOMDocument.3.0");
	} catch (e) {
		var xml = new ActiveXObject(domid);
	}

	xml.async = false;
	xml.validateOnParse = true;
	if (!xml.load(xmlFile)) {
		alert("Lime could not succesfully read the file called '" + xmlFile + "'.\nLime will try again 3 seconds after you press OK. If the problem persists please contact your system administrator.\n\nWe apologize for the inconvenience.");
		setTimeout("document.execCommand('refresh');", 3000);
	}

//	status = oldstatus;
	return xml;
}

function getHTTP_general(url) {
	//loads the contents of url into a string and returns that string
	try {
		var xmldom = new ActiveXObject ("Microsoft.XMLDOM");
		var xmlhttp = new ActiveXObject ("Microsoft.XMLHTTP");

		try {
			xmlhttp.Open("GET", url, false);
		} catch (e) {
			if (e.description.indexOf("The parameter is incorrect") == 0) {
				alert("Loading libraries is only supported if called via HTTP!");
			} else {
				alert("Unknown error in general.getHTTP('" + url + "'): " + e.description);
			}
		}

		xmlhttp.Send(xmldom);
		return xmlhttp.responseText;
	} catch (e) {
		alert("Error in getHTTP_general('" + url + "'): " + e.description);
	}
}

function getParameter_general(name, defaultValue) {
	////////////////////////////////////////////////
	// finds a parameter in the search-string of the location (...?.=..&.=..&.=..)
	// if not found this function returns the specified defaultValue
	// the defaultValue parameter is not required
	////////////////////////////////////////////////
	var qa = unescape(document.location.search).substring(1, document.location.search.length).split("&");
	for (var i=0; i<qa.length; i++) {
		if (qa[i].indexOf(name + "=") == 0) return qa[i].substring(name.length+1, qa[i].length);
	}

	return defaultValue ? defaultValue : null;
}

function unescape_general(html) {
	return html.replace(/(\&amp;)/g, "&").replace(/(\&lt;)/g, "<").replace(/(\&gt;)/g, ">");
}

function escape_general(html) {
	return html.replace(/\&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

function getObjectTop_general(o) {
	return o ? (o.getBoundingClientRect().top + document.body.scrollTop) : -1;
}

function getObjectLeft_general(o) {
	return o ? (o.getBoundingClientRect().left + document.body.scrollLeft) : -1;
}

function getObjectBottom_general(o) {
	return o ? (o.getBoundingClientRect().bottom + document.body.scrollTop) : -1;
}

function getObjectRight_general(o) {
	return o ? (o.getBoundingClientRect().right + document.body.scrollLeft) : -1;
}

//function for reading a cookie
function readCookie_general(theName, theDefault) {
	try {
		var theCookie = document.cookie + "|;";
		var p = theCookie.indexOf("Q42=");
		if (p == -1) return theDefault;
		theCookie = theCookie.substring(p, theCookie.indexOf("|;", p)) + "|"; 
		var pos = theCookie.indexOf(theName + ":");
		if (pos == -1) return theDefault;
//alert("theCookie = " + theCookie);
		theValue = unescape(theCookie.substring(pos+theName.length+1, theCookie.indexOf("|", pos)));
//alert(theValue);
		if (theValue == "") return theDefault;
		else return theValue;
	} catch (e) {
		return theDefault;
	}
}

//function for writing a cookie
function writeCookie_general(theName, theValue) {
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
	var nextYear = new Date();
	nextYear.setFullYear(nextYear.getFullYear()+1);
	var c = "Q42=" + s + "|; expires=" + nextYear.toGMTString() + "; path=/;";
	document.cookie = c;
}

function hlimg_general(img, highlight, postfix) {
	if (!img) return;

	var src = img.src;
	if (highlight) {
		if (src.lastIndexOf(postfix) != (src.length-4-postfix.length)) src = src.substring(0, src.length-4) + postfix + src.substring(src.length-4, src.length);
	} else {
		if (src.lastIndexOf(postfix) == (src.length-4-postfix.length)) src = src.substring(0, src.length-4-postfix.length) + src.substring(src.length-4, src.length);
	}
	if (src != img.src) img.src = src;
}

function mapArray_general(array, processor) {
	//processes all elements of an array and returns a new array
	//array is the array (associative or enumerated)
	//processor is the processor function having for parameters (index/name, value) and returning the new value

	var tmp = new Array;
	if (array.length || (array.length == 0)) for (var i=0; i<array.length; i++) tmp[i] = processor(i, array[i]);
	else for (var a in array) tmp[a] = processor(a, array[a]);
	return tmp;
}
