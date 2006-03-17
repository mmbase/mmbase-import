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
 * $Id: menu.js,v 1.2 2006-03-17 21:35:14 michiel Exp $
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
            subelm.onmouseover = openMenu;
            subelm.onmouseout  = scheduleCollapse;
            var subMenu = document.getElementById("sub_" + subelm.id);
            if (subMenu) {
                debug("found " + subMenu.id);
                subMenu.parent_a = subelm;
                subMenu.left       = (subelm.left ? subelm.left : subelm.offsetLeft);
                subMenu.top        = (subelm.top  ? subelm.top  : subelm.offsetTop);
                if (reposition == "vertical") {
                    subMenu.style.position = 'absolute';
                    subMenu.style.left = subMenu.left + "px";
                    subMenu.top        = (subelm.top ? subelm.top : subelm.offsetTop) + subelm.offsetHeight;
                    subMenu.style.top  = subMenu.top + "px";
                    if (subMenu.offsetWidth < elm.offsetWidth) {
                        // if submenu is smaller, then at least make it same width.
                        subMenu.style.width = elm.offsetWidth + "px";
                    }

                }
                if (reposition == "horizontal") {
                    subMenu.style.position = 'absolute';
                    subMenu.left       = (subelm.left ? subelm.left: subelm.offsetLeft) + subelm.offsetWidth;
                    subMenu.style.left = subMenu.left + "px";
                    subMenu.style.top  = subMenu.top + "px";
                }
                subMenu.style.display = "none";
                subMenu.onmouseover = useMenu;
                subMenu.onmouseout  = unuseMenu;
                initMenu(subMenu.id, reposition);
            }
        }
        elm = elm.nextSibling;
    }
}

function getDiv(elm) {
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

function useMenu(event) {
    var target = getDiv(getTarget(event));
    usedMenus[target.id] = target;
    debug("Marking menu " + target.id + " / " + target.left + " in use");
}
function unuseMenu(event) {
    var target = getDiv(getTarget(event));
    usedMenus[target.id] = undefined;
    debug("Unmarking menu " + target.id + " in use");
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
            debug("Collapsing other sibling of " + target.id + " " + sibl.id);
            collapseMenu(sibl);
        }
    }
    var elm = document.getElementById("sub_" + target.id);
    if (elm) {
        debug("opening because because of " + target.tagName + "/" + target.id);
        elm.style.display = "block";
    } else {
    }
    usedMenus["" + target.id] = target;
}

function scheduleCollapse(event) {
    var target = getTarget(event);
    debug("collapings because because of " + target.tagName + "/" + target.id);
    usedMenus["" + target.id] = undefined;
    setTimeout('collapseMenu("' + target.id + '")', timeOut);
}

function collapseMenu(id) {
    var elm = document.getElementById("sub_" + id);
    if (elm && ! usedMenus[elm.id]) {
        elm.style.display = "none";
    }
}
