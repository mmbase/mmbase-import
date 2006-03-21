/**
 * Simple JavaScript to make XHTML compliant menus.
 *
 * Requires either:
 *   <div id="[menuId]">
 *     <a id="[entryid1]" />
 *     <div id="sub_[entryid1]">
 *       <a id="[entryid2]" /
 *       <div id="sub_[entryid2]">
 *          ... unsupported as yet...
 *       </div>
 *       <a id="[entryid3]" />
 *       ....
 *     </div>
 *   </div>
 *
 * Or:
 *   <ul id="[menuId]">
 *    <li>
 *      <a id="[entryid1]" />
 *      <ul id="sub_[entryid1]">
 *        <li>
 *          <a id="[entryid2]" />
 *          <ul id="..>
 *            ....
 *          </ul>
 *        </li>
 *        .....
 *      </ul>
 *    </li>
 *     .....
 *   </ul>
 *
 * @author Michiel Meeuwissen
 * $Id: menu.js,v 1.3 2006-03-21 22:16:13 michiel Exp $
 */
var usedMenus = new Object(); // currently can contain only one, but when deeper nesting is implemented can contain more.
var timeOut = 1000;

var debugarea;

function debug(mesg) {
    if (debugarea) debugarea.value = mesg + "\n" + debugarea.value;
}

// cross-browser shit
function getTarget(event) {
    if (! event) event = window.event; // IE requires window.event
    if (! event) return undefined;     // err.
    var target = event.target ? event.target : event.srcElement; // IE requires srcElement
    return target;
}
// more cross-browser shit
function addEvent(obj, evType, fn){
    if (obj.addEventListener){
        obj.addEventListener(evType, fn, false);
        return true;
    } else if (obj.attachEvent){
        var r = obj.attachEvent("on"+evType, fn);
        return r;
    } else {
        return false;
    }
}

function initMenu(menuId, reposition) {
    debugarea = document.getElementById("menu_debug");
    var elm = document.getElementById(menuId).firstChild;
    var siblings = new Object();
    while (elm) {
        var subelm = elm;
        if (subelm.tagName && subelm.tagName.toLowerCase() == 'li') {
            subelm = subelm.firstChild;
            while (subelm && !(subelm.tagName && subelm.tagName.toLowerCase() == 'a')) {
                subelm = subelm.nextSibling;
            }
        }
        if (subelm && subelm.id && subelm.tagName && (subelm.tagName.toLowerCase() == 'a')
            ) {
            siblings[subelm.id] = subelm;
            subelm.siblings = siblings;
            addEvent(subelm, "mouseover", openMenu);
            addEvent(subelm, "mouseout",  closeMenu);
            var subMenu = getSubMenu(subelm);
            if (subMenu) {
                debug("found " + subMenu.id);
                subMenu.parent_a = subelm;
                subMenu.left       = (subelm.left ? subelm.left : subelm.offsetLeft);
                subMenu.top        = (subelm.top  ? subelm.top  : subelm.offsetTop);
                if (reposition == "bottom") {
                    subMenu.style.position = 'absolute';
                    subMenu.style.left = subMenu.left + "px";
                    subMenu.top        = (subelm.top ? subelm.top : subelm.offsetTop) + subelm.offsetHeight;
                    subMenu.style.top  = subMenu.top + "px";
                    if (subMenu.offsetWidth < elm.offsetWidth) {
                        // if submenu is smaller, then at least make it same width.
                        subMenu.style.width = elm.offsetWidth + "px";
                    }

                } else if (reposition == "right") {
                    subMenu.style.position = 'absolute';
                    subMenu.left       = (subelm.left ? subelm.left: subelm.offsetLeft) + subelm.offsetWidth;
                    subMenu.style.left = subMenu.left + "px";
                    subMenu.style.top  = subMenu.top + "px";
                } else if (reposition == "left") {
                    subMenu.style.position = 'absolute';
                    subMenu.left       = (subelm.left ? subelm.left: subelm.offsetLeft) - subMenu.offsetWidth;
                    subMenu.style.left = subMenu.left + "px";
                    subMenu.style.top  = subMenu.top + "px";
                }
                subMenu.style.display = "none";
                debug("usemenu on " + subMenu.tagName);
                addEvent(subMenu, "mouseover", useMenu);
                addEvent(subMenu, "mouseout",  unuseMenu);
                initMenu(subMenu.id, reposition);
            }
        }
        elm = elm.nextSibling;
    }
}
function getMenu(elm) {
    while (true) {
        var tagName = elm.tagName;
        if (tagName) {
            if (tagName.toLowerCase() == "div"
                || tagName.toLowerCase() == "ul") {
                return elm;
            }
        }
        elm = elm.parentNode;
    }    
}

function getSubMenu(elm) {
    var tagName = elm.tagName;
    if (tagName && (tagName == "a" || tagName == "A")) {
        return document.getElementById("sub_" + elm.id);
    } else {
        return elm;
    }
}

function useMenu(event) {
    debug(event);
    var elm = getMenu(getTarget(event));
    if (elm) {
        usedMenus[elm.id] = elm;
        debug("Marking menu " + elm.id + " / " + elm.left + " in use");
    }
}
function unuseMenu(event) {
    var target = getMenu(getTarget(event));
    if (target) {
        usedMenus[target.id] = undefined;
        debug("Unmarking menu " + target.id + "/" + target.parent_a.id + "  in use");
        if (target.parent_a) {
            setTimeout('collapseMenu("' + target.id + '")', timeOut);
        }
    }
}


function openMenu(event) {
    var target = getTarget(event);
    // collapse siblings
    for (var sibl in  target.siblings) {
        if (sibl != target.id) {
            debug("Collapsing other sibling of " + target.id + ": " + sibl);
            collapseMenu(getSubMenu(document.getElementById(sibl)).id);
        }
    }
    var elm = getSubMenu(target);
    if (elm) {
        debug("opening because because of " + target.tagName + "/" + target.id);
        elm.style.display = "block";
    } else {
    }
    usedMenus[elm.id] = elm;
    debug("marked used " + elm.id);
}

function closeMenu(event) {
    var target = getSubMenu(getTarget(event));
    debug("collapings because because of " + target.tagName + "/" + target.id);
    usedMenus["" + target.id] = undefined;
    setTimeout('collapseMenu("' + target.id + '")', timeOut);
}

function collapseMenu(id) {
    var elm = document.getElementById(id);
    if (elm && ! usedMenus[id]) {
        debug("Collapsed " + elm.id);
        elm.style.display = "none";
    } else {
        debug("Cancelled collapse of " + id);
    }
}
