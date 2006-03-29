/**
 * Simple JavaScript to make XHTML compliant menus.
 *
 * Requires something like:
 *   <div id="[menuId]">
 *     <a class="mmenu" id="[entryid1]" />
 *     <div class="[entryid1]">
 *       <a class="mmenu" id="[entryid2]" /
 *       <div class="[entryid2]">
 *          ... unsupported as yet...
 *       </div>
 *       <a class="mmenu" id="[entryid3]" />
 *       ....
 *     </div>
 *   </div>
 *
 * Or:
 *   <ul id="[menuId]">
 *    <li>
 *      <a class="mmenu" id="[entryid1]" />
 *      <ul class="[entryid1]">
 *        <li>
 *          <a class="mmenu" id="[entryid2]" />
 *          <ul class="..>
 *            ....
 *          </ul>
 *        </li>
 *        .....
 *      </ul>
 *    </li>
 *     .....
 *   </ul>
 *
 *   <script type="text/JavaScript" src="menu.js"> </script>
 *
 *   <script type="text/JavaScript">
 *       addEvent(window, 'load',  function () {
 *          initMenu("[menuId]" [, "left"|"right"|"bottom"]);
 *       });
 *   </script>
 *
 * The essential bits are the id's with the corresponding classes for the subitems.
 *
 * @author Michiel Meeuwissen <jsmenu@meeuw.org>
 * $Id: menu.js,v 1.7 2006-03-29 12:05:15 michiel Exp $
 */

//
var TIMEOUT = 1000;
var MENU_CLASS = "mmenu";
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

function getElementsByClass(node, searchClass, tag) {
  var classElements = new Array();
  var els = node.getElementsByTagName(tag);
  var elsLen = els.length;
  var pattern = new RegExp("\\b" + searchClass + "\\b");
  for (i = 0, j = 0; i < elsLen; i++) {
    if ( pattern.test(els[i].className) ) {
        classElements[j] = els[i];
        j++;
    } else {
    }
  }
  return classElements;
}

function getSubMenus(elm, id) {
    return getElementsByClass(elm, id, "*");
}

function initMenu(menuId, reposition) {
    if (!debugarea) debugarea = document.getElementById("menu_debug");
    var menu = document.getElementById(menuId);
    var siblings = getElementsByClass(menu, MENU_CLASS, "a");
    debug("Found " + siblings.length + " subitems for " + menu.id);
    for (var i = 0; i < siblings.length; i++) {
        var subelm = siblings[i];
        subelm.parent   = menu;
        subelm.siblings = siblings;
        addEvent(subelm, "mouseover", openMenu);
        addEvent(subelm, "mouseout",  closeMenu);
        var subMenus = getSubMenus(menu, subelm.id);
        debug("found " + subMenus.length + " subitems for " + subelm.id);
        for (var j = 0; j < subMenus.length; j++) {
            var subMenu = subMenus[j];
            subMenu.parent     = subelm;
            subMenu.left       = (subelm.left ? subelm.left : subelm.offsetLeft);
            subMenu.top        = (subelm.top  ? subelm.top  : subelm.offsetTop);
            if (reposition == "bottom") {
                subMenu.style.position = 'absolute';
                subMenu.style.left = subMenu.left + "px";
                subMenu.top        = (subelm.top ? subelm.top : subelm.offsetTop) + subelm.offsetHeight;
                subMenu.style.top  = subMenu.top + "px";

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
            addEvent(subMenu, "mouseover", useMenu);
            addEvent(subMenu, "mouseout",  unuseMenu);
            if (subMenu.id) {
                initMenu(subMenu.id, reposition);
            }
        }
    }
}

/**
 * Return the elements which need to have an onmouse over event to switch on a menu.
 * @param elm In which element to search
 */
function getMenuByClass(elm) {
    var pattern = new RegExp("\\b" + MENU_CLASS + "\\b");
    while (elm != null) {
        if (pattern.test(elm.className)) return elm;
        elm = elm.parentNode;
    }
}

/**
 * Given a certain element, this returns the first parent that is a menu. Used by the events to get
 * the actual element of interest.
 */
function getMenuByParent(elm) {
    while (elm != null) {
        if (elm.parent) return elm.parent;
        debug(elm.tagName + " has no parent " );
        elm = elm.parentNode;
    }
}

/**
 * The mouse over event of a menu. It marks the menu so that it cannot collapse now.
 */
function useMenu(event) {
    var menu = getMenuByParent(getTarget(event));
    if (menu) {
        menu.inuse = true;
        debug("Marking menu " + menu.id + " / " + menu.left + " in use");
    }
}
/**
 * The mouse out event of a menu. It marks the menu so that it collapse now, and schedules that.
 * If the menu gets in used in a short period, this collaps is canceled.
 */
function unuseMenu(event) {
    var menu = getMenuByParent(getTarget(event));
    if (menu) {
        menu.inuse = false;
        debug("Unmarking menu " + menu.id + "/" + "  in use");
        setTimeout('collapseMenu("' + menu.id + '")', TIMEOUT);
    }
}


/**
 * The mouse over event of an menu-opener
 */
function openMenu(event) {
    var menu = getMenuByClass(getTarget(event));
    debug("opening " + menu.id + " ? " + menu.tagName);
    // collapse siblings
    for (var i = 0 ; i < menu.siblings.length ; i++) {
        debug("FOUND" + menu.siblings[i].id);
        var sibl = menu.siblings[i];
        if (sibl != menu) {
            debug("Collapsing other sibling of " + menu.id + ": " + sibl);
            collapseMenu(sibl.id);
        }
    }
    menu.inuse = true;
    debug("marked used " + menu.id);
    var subMenus = getSubMenus(menu.parent, menu.id);
    for (var i = 0 ; i < subMenus.length; i++) {
        debug("opening because because of " + menu.tagName + "/" + menu.id);
        var subMenu = subMenus[i];
        subMenu.style.display = "block";
        subMenu.style.visibility = "visible";
        if (subMenu.offsetWidth > 0 && subMenu.offsetWidth < subMenu.parent.offsetWidth) {
            // if submenu is smaller, then at least make it same width.
            subMenu.style.width = subMenu.parent.offsetWidth + "px";
        }
    }
}

/**
 * The mouse out event of an menu-opener
 */
function closeMenu(event) {
    var menu = getMenuByClass(getTarget(event));
    var subMenus = getSubMenus(menu.parent, menu.id);
    menu.inuse = false;
    for (var i = 0 ; i < subMenus.length; i++) {
        debug("collapings because because of " + menu.tagName + "/" + menu.id);
        setTimeout('collapseMenu("' + menu.id + '")', TIMEOUT);
    }
}

/**
 * This method actually closes a menu.
 */
function collapseMenu(id) {
    var menu = document.getElementById(id);
    if (menu && ! menu.inuse) {
        var subMenus = getSubMenus(menu.parent, menu.id);
        for (var i = 0 ; i < subMenus.length; i++) {
            debug("Collapsed " + menu.id);
            subMenus[i].style.display = "none";
        }
    } else {
        debug("Cancelled collapse of " + id);
    }
}
