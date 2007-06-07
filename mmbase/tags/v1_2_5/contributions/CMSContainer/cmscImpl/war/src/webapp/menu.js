/*
  Expandable Listmenu Script
  Author : Thomas Bakketun
  http://www.bakketun.net/listmenu/

  Based on script by:
  Author : Daniel Nolan
  http://www.bleedingego.co.uk/webdev.php
*/


function elementHasClass( element, className ) {
  if ( ! element.className ) { 
    return false;
  }
  var re = new RegExp( "(^|\\s+)" + className + "($|\\s+)" );
  return re.exec( element.className ); 
}    

function initMenus() {
  if ( ! document.getElementsByTagName ) {
    return;
  }
  var singleopen, keepopen;
  var menus = document.getElementsByTagName( "ul" );
  for ( var i = 0; i < menus.length; i++ ) {
    if ( elementHasClass( menus[ i ], "treemenu" ) ) {
      initMenu( menus[ i ], 
        elementHasClass( menus[ i ], "singleopen" ), 
        elementHasClass( menus[ i ], "keepopen" )
      );
    }
  }
}

function getChildNodes( element, tag ) {
  var foundNodes = new Array();
  var childNodes = element.childNodes;
  for (var i = 0; i < childNodes.length; i++ ) {
    var node = childNodes[ i ];
    //FIXME: should only do convert to lowercase when i HTML-mode
    if ( node.tagName && ( node.tagName.toLowerCase() == tag ) ) {
      foundNodes.push( node );
    }
  }
  return foundNodes;
}

function createA(menu) {
  var a, text;
  text = menu.firstChild;
  a = document.createElement("a");
  a.href = "#";
  menu.replaceChild(a, text);
  a.appendChild(text);
  return a;
}

function initMenu(menu, singleopen, keepopen) {
  var item, a, open;
  var items = getChildNodes(menu, "li");
  open = false;
  for (var i = 0; i<items.length; i++) {
    item = items[i];
    a = getChildNodes(item, "a")[0];
    var submenu = getChildNodes(item, "ul")[0];
    if (submenu) {
      if (!a) {
        a = createA(item);
      }
      open = initMenu(submenu, singleopen, keepopen) || open;
      a.onclick = function() { return menuonclick(this, singleopen); };
    } else {
      if (a) open = open || (keepopen && a.href == window.location);
    }
    if (item.className == "treenodeopen") setMenu(item, true);
    open = open || item.className == "treenodeshow";
  }
  setMenu(menu.parentNode, open);
  return open;
}

function menuonclick(a, singleopen) {
  setMenu(a.parentNode, a.className == "treeclosed");
  var menus = getChildNodes(a.parentNode.parentNode, "li");
  if (singleopen) {
    for (var i = 0; i<menus.length; i++) {
      if (menus[i] != a.parentNode) {
        setMenu(menus[i], false);
      }
    }
  }
  return false;
}

function setMenu(menu, open) {
  var a = getChildNodes(menu, "a")[0];
  var ul = getChildNodes(menu, "ul")[0];
  if (a && ul) {
    if (open) {
      a.className = "treeopen";
      ul.style.display = "block";
    } else {
      a.className = "treeclosed";
      ul.style.display = "none";
    }
  }
}

initMenus();