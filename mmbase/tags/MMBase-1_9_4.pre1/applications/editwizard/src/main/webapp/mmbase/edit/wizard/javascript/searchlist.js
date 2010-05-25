function preselect(didlist) {
    didlist = "|" + didlist + "|";
    var f = document.forms[0];
    for (var i=0; i<f.elements.length; i++) {
        var e = f.elements[i];
        if (e.type.toLowerCase() == "checkbox") {
            if (didlist.indexOf("|" + e.getAttribute("did") + "|") > -1) doclick_search(document.getElementById("item_" + e.getAttribute("did")));
        }
    }
}

function getParameter_general(name, defaultValue) {
    // finds a parameter in the search-string of the location (...?.=..&.=..&.=..)
    // if not found this function returns the specified defaultValue
    // the defaultValue parameter is not required
    var qa = unescape(document.location.search).substring(1, document.location.search.length).split("&");
    for (var i=0; i<qa.length; i++) {
        if (qa[i].indexOf(name + "=") == 0) return qa[i].substring(name.length+1, qa[i].length);
    }
    return defaultValue;
}

function doclick_search(el) {
    var cb = document.getElementById("cb_" + el.getAttribute("number"));
    cb.checked = ! cb.checked;
    if(cb.checked) {
       if (el.className == "odd") {
          el.className = "selected-odd";
       } else {
          el.className = "selected-even";
       }
    } else {
       if (el.className == "selected-odd") {
          el.className = "odd";
       } else {
          el.className = "even";
       }
    }
}

function dosubmit() {
    selected = buildSelectedList();
    addItem(selected);
    closeSearch();
}

//alert("defining doAddSubmit");

function doAddSubmit(relationOriginNode, relationRole, relationCreateDir) {
    selected = buildSelectedList();
    var url = "linklistitem.jsp?"+"relationOriginNode="+relationOriginNode+"&relationRole=" + 
    relationRole + "&relationCreateDir" + relationCreateDir + "&selected="+selected;
    loadXMLDoc(url, false);

    window.opener.document.forms[0].submit();
    window.close();
}

function buildSelectedList() {
    var s = selected + "|";
    var f = document.forms[0];
    for (var i=0; i<f.elements.length; i++) {
        var e = f.elements[i];
        if (e.type.toLowerCase() == "checkbox") {
            if (e.checked) {
                // Add it if it's not already in the list.
                if (s.indexOf("|" + e.getAttribute("did") + "|") == -1) s += e.getAttribute("did") + "|";
            } else {
                // Remove it if it's not selected anymore.
                var pos = s.indexOf("|" + e.did);
                if (pos > -1) s = s.substring(0,pos) + s.substring(pos + e.did.length + 1);
            }
        }
    }
    return s.substring(0, s.length - 1);;
}

function browseTo(start) {
    selected = buildSelectedList();
    var href = listpage;
    href += "&popupid=search&listtype=search&start="+start+"&selected="+selected+"&cmd="+cmd;
    if (relationRole != undefined && relationRole != "") {
      href += "&relationRole=" + relationRole;
    }
    if (relationOriginNode != undefined && relationOriginNode != "") {
      href += "&relationOriginNode=" + relationOriginNode;
    }
    if (relationCreateDir != undefined && relationCreateDir != "") {
      href += "&relationCreateDir=" + relationCreateDir;
    }
    document.location.replace(href);
    return false;
}

function resizeSelectTable() {
    divnav = findPosY(document.getElementById("searchnavigation"));
    document.getElementById("searchresult").style.height = divnav;
}

function doOnloadSearch() {
}

function addItem(selected) {
        alert("Add the searchwindow.js or searchiframe.js to the searchlist xsl");
}

function closeSearch() {
        alert("Add the searchwindow.js or searchiframe.js to the searchlist xsl");
}
