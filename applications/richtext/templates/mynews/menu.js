/**
 * (c) PO Internet Services
 *
 * requires:
 *   <div id="[menuId]">
 *     <a id="[entryid1]" />
 *     <div ="sub_[entryid1]">
 *       <a id="[entryid2]" /
 *       <div ="sub_[entryid2]">
 *          ... unsupported as yet...
 *       </div>
 *       <a id="[entryid3]" />
 *       ....
 *     </div>
 *   </div>
 *
 * $Id: menu.js,v 1.1 2006-03-14 23:35:00 michiel Exp $
 */
var usedMenus = new Object(); // currently can contain only one, but when deeper nesting is implemented can contain more.
var timeOut = 1000;

function initMenu(menuId) {
    var elm = document.getElementById(menuId).firstChild;

    var siblings = new Object();
    while (true) {
        if (! elm) break;
        if (elm.tagName && elm.tagName.toLowerCase() == 'a') {
            siblings[elm.id] = elm;
            elm.siblings = siblings;
            elm.onmouseover = openMenu;
            elm.onmouseout  = scheduleCollapse;
            var subMenu = document.getElementById("sub_" + elm.id);
            if (subMenu) {
                subMenu.parent_a = elm;
                subMenu.style.position = 'absolute';
                subMenu.style.left = elm.offsetLeft + "px";
                subMenu.style.top = elm.offsetTop + 18 + "px";
                subMenu.style.display = "none";
                subMenu.onmouseover = useMenu;
                subMenu.onmouseout = unuseMenu;
                // recursivity should happen somewhere here.
            }
        }
        elm = elm.nextSibling;
    }
    
}

function getDiv(elm) {
    while (true) {
        var tagName = elm.tagName;
        if (tagName) {
            if (tagName.toLowerCase() == "div") {
                return elm;
            }            
        }
        elm = elm.parentNode;
    }
}

// cross-browser shit
function getTarget(event) {
    if (! event) event = window.event; // IE requires window.event
    if (! event) return undefined;     // err.
    var target = event.target ? event.target : event.srcElement; // IE requires srcElement
    return target;
}

function useMenu(event) {
    var target = getDiv(getTarget(event));
    usedMenus[target.id] = target;
}
function unuseMenu(event) {
    var target = getDiv(getTarget(event));
    usedMenus[target.id] = undefined;
    if (target.parent_a) {
        setTimeout('collapseMenu("' + target.parent_a.id + '")', timeOut);
    }
}

    
function openMenu(event) {
    var target = getTarget(event);
    usedMenus = new Object();
    // collapse siblings
    for (var sibl in  target.siblings) {
        if (sibl != target.id) {
            collapseMenu(sibl);
        }            
    }
    var elm = document.getElementById("sub_" + target.id);
    if (elm) {
        elm.style.display = "block";
    }
    usedMenus["div" + target.id] = target;
}

function scheduleCollapse(event) {
    var target = getTarget(event);
    usedMenus["div" + target.id] = undefined;
    setTimeout('collapseMenu("' + target.id + '")', timeOut);
}

function collapseMenu(id) {
    var elm = document.getElementById("sub_" + id);
    if (elm && ! usedMenus[elm.id]) {
        elm.style.display = "none";
    }
}
