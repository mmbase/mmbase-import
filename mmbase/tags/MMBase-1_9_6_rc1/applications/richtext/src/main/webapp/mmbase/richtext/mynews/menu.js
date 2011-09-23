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
 * $Id: menu.js,v 1.11 2007-06-20 14:29:28 michiel Exp $
 */

//
var TIMEOUT    = 3000;    // ms
var MENU_CLASS = "mmenu"; // class of a menu element
var debugarea;            // textarea with id menu_debug if that is present on the page.

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

// missing in DOM
function getElementsByClass(node, searchClass, tag) {
  var classElements = new Array();
  var els = node.getElementsByTagName(tag);
  var elsLen = els.length;
  var pattern = new RegExp("\\b" + searchClass + "\\b");
  for (var i = 0, j = 0; i < elsLen; i++) {
    if ( pattern.test(els[i].className) ) {
        classElements[j] = els[i];
        j++;
    } else {
    }
  }
  return classElements;
}

function getElementsByClassNonNesting(node, searchClass, tag, result) {
    if (result == undefined) result = new Array();
    if (! node) return result;
    var childs = node.childNodes;
    var pattern = new RegExp("\\b" + searchClass + "\\b");
    tag = tag.toUpperCase();
    //debug("Searching " + childs.length + " of " + node.tagName);
    for (var i = 0; i < childs.length; i++) {
        if (pattern.test(childs[i].className) && (tag == '*' || tag == childs[i].tagName.toUpperCase())) {
            result[result.length] = childs[i];
        } else {
            getElementsByClassNonNesting(childs[i], searchClass, tag, result);
        }
    }
    return result;
}

function getSubMenus(elm, searchClass) {
    return getElementsByClassNonNesting(elm, searchClass, "*");
}

/**
 * Inits a menu by id
 * @param reposition 'bottom', 'right', 'left' or unset.
 */
function initMenu(menuId, reposition) {
    if (!debugarea) debugarea = document.getElementById("menu_debug");
    var menu = document.getElementById(menuId);
    debug("initing " + menuId);
    initMenuElement(menu, reposition, '');

}
function initMenuElement(menu, reposition) {
    var menuItems = getElementsByClassNonNesting(menu, MENU_CLASS, "a");
    debug("Found " + menuItems.length + " subitems for " + menu.id);
    for (var i = 0; i < menuItems.length; i++) {
        var menuItem = menuItems[i];
        menuItem._parent  = menu;
        menuItem.siblings = menuItems;
        addEvent(menuItem, "mouseover", openMenu);
        addEvent(menuItem, "mouseout",  closeMenu);
        var subMenus = getSubMenus(menu, menuItem.id);
        debug("found " + subMenus.length + " subitems for " + menuItem.id);
        for (var j = 0; j < subMenus.length; j++) {
            var subMenu = subMenus[j];
            subMenu._parent     = menuItem;
            subMenu.reposition = reposition;
            subMenu.style.display = "none";
            addEvent(subMenu, "mouseover", useMenu);
            addEvent(subMenu, "mouseout",  unuseMenu);
            // may contain itself mmenu items:
            initMenuElement(subMenu, reposition);
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
        if (elm._parent) return elm._parent;
        debug(elm.tagName + " has no parent " );
        elm = elm.parentNode;
    }
}

/**
 * The mouse over event of a menu. It marks the menu so that it cannot collapse now.
 */
function useMenu(event) {
    var thisMenu = getTarget(event);
    thisMenu._inuse = true;
    var menu = getMenuByParent(getTarget(event));
    debug("XXXUsing menu " + menu.id);
    if (menu) {
        menu._inuse = true;
        debug("Marking menu " + menu.id + " in use on event on " + getTarget(event));
    }
}
/**
 * The mouse out event of a menu. It marks the menu so that it collapse now, and schedules that.
 * If the menu gets in used in a short period, this collaps is canceled.
 */
function unuseMenu(event) {
    var menu = getMenuByParent(getTarget(event));
    if (menu) {
        menu._inuse = false;
        debug("Unusing menu " + menu.id + " on " + getTarget(event).tagName);
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
        //debug("FOUND" + menu.siblings[i].id);
        var sibl = menu.siblings[i];
        if (sibl != menu) {
            //debug("Collapsing other sibling of " + menu.id + ": " + sibl);
            //collapseMenu(sibl.id);
        }
    }
    menu._inuse = true;
    debug("marked used " + menu.tagName + " " + menu.id);
    var subMenus = getSubMenus(menu._parent, menu.id);
    for (var i = 0 ; i < subMenus.length; i++) {
        var subMenu = subMenus[i];
        subMenu.style.display = "block";
        subMenu.style.visibility = "visible";
        positionMenu(subMenu);
    }
}

function positionMenu(subMenu) {
    subMenu._left       = (subMenu._parent._left ? subMenu._parent._left : subMenu._parent.offsetLeft);
    subMenu._top        = (subMenu._parent._top  ? subMenu._parent._top  : subMenu._parent.offsetTop);
    if (subMenu.reposition == "bottom") {
        subMenu.style.position = 'absolute';
        subMenu.style.left = subMenu._left + "px";
        subMenu._top        += subMenu._parent.offsetHeight;
        subMenu.style.top  = subMenu._top + "px";
    } else if (subMenu.reposition == "right") {
        subMenu.style.position = 'absolute';
        subMenu._left       +=  subMenu._parent.offsetWidth;
        subMenu.style.left = subMenu._left + "px";
        subMenu.style.top  = subMenu._top + "px";
    } else if (subMenu.reposition == "left") {
        subMenu.style.position = 'absolute';
        subMenu._left           -= subMenu.offsetWidth;
        subMenu.style.left = subMenu._left + "px";
        subMenu.style.top  = subMenu._top + "px";
    }
    if (subMenu.offsetWidth > 0 && subMenu.offsetWidth < subMenu._parent.offsetWidth) {
        // if submenu is smaller, then at least make it same width.
        subMenu.style.width = subMenu._parent.offsetWidth + "px";
    }
}

/**
 * The mouse out event of an menu-opener
 */
function closeMenu(event) {
    var menu = getMenuByClass(getTarget(event));
    var subMenus = getSubMenus(menu._parent, menu.id);
    for (var i = 0 ; i < subMenus.length; i++) {
        debug("collapsing because because of " + menu.tagName + "/" + menu.id + " on mouse out of " + getTarget(event));        
        menu._inuse = false;
        setTimeout('collapseMenu("' + menu.id + '")', TIMEOUT);
    }
}

/**
 * This method actually closes a menu.
 */
function collapseMenu(id) {
    //xalert("Closing " + id);
    var menu = document.getElementById(id);
    if (menu && ! menu._inuse) {
        var subMenus = getSubMenus(menu._parent, menu.id);
        for (var i = 0 ; i < subMenus.length; i++) {
            debug("Collapsed " + menu.id);
            subMenus[i].style.display = "none";
        }
    } else {
        debug("Cancelled collapse of " + id);
    }
}
