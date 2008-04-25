/* onload function loads some scripts */
var sortedOn = 0;
var cookie_str = "my_editors_sort";
window.onload = function() {
    initSortTables();    // TODO: mention tables to sort
}

/* sort tablerows */
function sortT(tableId, sortOn) {
    sortTable(tableId, sortOn);
    
    /* detect last 2 rows */
    var table = document.getElementById(tableId);
	var tbody = table.getElementsByTagName('tbody')[0];
    var rows = tbody.getElementsByTagName('tr');
    var rowArray = new Array();
    for (var i=0, length=rows.length; i<length; i++) {
        rowArray[i] = new Object;
        rowArray[i].oldIndex = i;
        if (sortOn == 0) {
            rowArray[i].value = rows[i].getElementsByTagName('td')[0].firstChild.nodeValue;
        } else {
            rowArray[i].value = rows[i].getElementsByTagName('a')[0].firstChild.nodeValue;
        }
    }
    var dir = "normal";
	var rows = rowArray.length;
	if (rowArray[rows - 1].value < rowArray[rows - 2].value) dir = "reverse";
    
    var sortvalues = sortOn + "_" + dir;
    var cookieValue = tableId + "." + sortvalues;
    createCookie(cookie_str, cookieValue, 99);
}

function initSortTables() {
    if (readCookie(cookie_str)) {
        // console.log("cookie gevonden: " + readCookie(cookie_str));
        var cookieValue = readCookie(cookie_str);
        var tableId = cookieValue.substring(0, cookieValue.indexOf("."));
        var sortValues = cookieValue.substring(cookieValue.indexOf(".") + 1, cookieValue.length);
        var sortOn = sortValues.substring(0, sortValues.indexOf("_"));
        var dir = sortValues.substring(sortValues.indexOf("_") + 1, sortValues.length);
        
        if (dir == "reverse") { 
            sortedOn = sortOn;
        } else {
            if (sortOn == 0) sortedOn = 1;
            if (sortOn == 1) sortedOn = 0;
        }
        // console.log("dir: " + dir + ", sortOn: " + sortOn + ", sortedOn: " + sortedOn);
        sortTable(tableId, sortOn);
    }
}

function sortTable(tableId, sortOn) {
    //console.log("sortOn: " + sortOn + " sortedOn: " + sortedOn);
	if (!document.getElementById(tableId)) return;
	var table = document.getElementById(tableId);
	var tbody = table.getElementsByTagName('tbody')[0];
    var rows = tbody.getElementsByTagName('tr');
    var rowArray = new Array();

    for (var i=0, length=rows.length; i<length; i++) {
        rowArray[i] = new Object;
        rowArray[i].oldIndex = i;
        if (sortOn == 0) {
            rowArray[i].value = rows[i].getElementsByTagName('td')[0].firstChild.nodeValue;
        } else {
            rowArray[i].value = rows[i].getElementsByTagName('a')[0].firstChild.nodeValue;
        }
    }
    
    if (sortOn == sortedOn) { 
        rowArray.reverse(); 
    } else {
        sortedOn = sortOn;
        rowArray.sort(RowCompare);
    }
    
    var newTbody = document.createElement('tbody');
    for (var i=0, length=rowArray.length; i<length; i++) {
        newTbody.appendChild(rows[rowArray[i].oldIndex].cloneNode(true));
    }
    var newRows = newTbody.getElementsByTagName('tr');
    for (var i=0; i<newRows.length; i++) {
		if ((i % 2) != 0) {
			if (newRows[i].className == 'odd' || !(newRows[i].className.indexOf('odd') == -1) ) {
				newRows[i].className = replace(newRows[i].className, 'odd', 'even');
			} else {
				newRows[i].className = " even";
			}
		} else {
			if (newRows[i].className == 'even' || !(newRows[i].className.indexOf('even') == -1) ) {
				newRows[i].className = replace(newRows[i].className, 'even', 'odd');
			} else {
				newRows[i].className = " odd";
			}
		}    	
    }
    
    table.replaceChild(newTbody, tbody);
}

function RowCompare(a, b) {
    var aVal = a.value.toLowerCase();
    var bVal = b.value.toLowerCase();
    return (aVal == bVal ? 0 : (aVal > bVal ? 1 : -1));
}

function RowCompareNumbers(a, b) {
    var aVal = parseInt(a.value);
    var bVal = parseInt(b.value);
    return (aVal - bVal);
}

function RowCompareDollars(a, b) {
    var aVal = parseFloat(a.value.substr(1));
    var bVal = parseFloat(b.value.substr(1));
    return (aVal - bVal);
}

function replace(s, t, u) {
  /*
  **  Replace a token in a string
  **    s  string to be processed
  **    t  token to be found and removed
  **    u  token to be inserted
  **  returns new String
  */
  i = s.indexOf(t);
  r = "";
  if (i == -1) return s;
  r += s.substring(0,i) + u;
  if ( i + t.length < s.length)
    r += replace(s.substring(i + t.length, s.length), t, u);
  return r;
}

/* cookie stuff */
function createCookie(name,value,days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

function eraseCookie(name) {
	createCookie(name,"",-1);
}
