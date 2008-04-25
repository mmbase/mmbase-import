/* Sort tablerows */
var sortedOn = 0;
function sortTable(tableId, sortOn) {
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
        if (sortOn == 0) {
            rowArray.sort(RowCompare);
        } else {
            rowArray.sort(RowCompare);
        }
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

function RowCompareLinks(a, b) {
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
