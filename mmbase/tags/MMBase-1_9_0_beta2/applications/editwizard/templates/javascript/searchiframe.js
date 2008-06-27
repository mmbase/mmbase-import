//********************************
// MODAL IFRAME STUFF
//********************************
// override showSearchScreen() from editwizard.jsp
function showSearchScreen(cmd, url) {
	showModalIFrame(cmd, url);
}

// override addItem(selected) from search.js
function addItem(selected) {
    parent.doAdd(selected, cmd);
}

// override closeSearch() from search.js
function closeSearch() {
	parent.removeModalIFrame();
}

function showModalIFrame(cmd, url) {
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
        }
        catch(e) {}

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