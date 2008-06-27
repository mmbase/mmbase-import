var popper = new Popper();

var ie4=document.all
var ns6=document.getElementById && !document.all
var ns4=document.layers

//constructor
function Popper() {
}

function start_popper() {
  var spans = document.getElementsByTagName('div');
  for (var i=0; i<spans.length; i++) {
		  //get the element
		  var el = spans[i];
		  if(isPopmenu(el)) {
			   popper.attachHighlightMenu(el);
			   popper.attachMenuToArea(el);
    }
  }
}

function isPopmenu(element) {
 	return (element.id.indexOf("popmenu") > -1);
}

// attach events
Popper.prototype.attachHighlightMenu = function (element) {
  if (!element) return;

 	var self=this;
	 addEvent(element, "mouseover", function(event) { self.clearhidemenu();self.highlightmenu(event,'on') });
	 addEvent(element, "mouseout", function(event) { self.highlightmenu(event,'off'); self.dynamichide(event); });
}

Popper.prototype.attachMenuToArea = function (element) {
	 name1 = element.id.substring("popmenu".length);
	 var spanElement = document.getElementById('treespan' + name1);

	 var self=this;
	 addEvent(spanElement, "mouseover", function(event) { self.showmenu(event, element) });
	 addEvent(spanElement, "mouseout", function(event) { self.clearshowmenu(); self.delayhidemenu(); });
}

addEvent = function(el, evname, func) {
  if (navigator.userAgent.toLowerCase().indexOf("msie") != -1) {
    if (navigator.appVersion.indexOf('Mac') != -1) {
      if (evname == 'mouseover') {
        el.onmouseover = func;
      }
      if (evname == 'mouseout') {
        el.onmouseout = func;
      }
    }
    else {
      el.attachEvent("on" + evname, func);
    }
  } else {
    el.addEventListener(evname, func, true);
  }
}

Popper.prototype.highlightmenu = function(e,state){
  if (document.all)
    source_el=event.srcElement
  else {
    if (document.getElementById)
      source_el=e.target
    if (source_el.className=="menuitems") {
      source_el.id=(state=="on")? "mouseoverstyle" : ""
    }
    else {
      while(source_el.id.indexOf("popmenu") == -1) {
         source_el=document.getElementById? source_el.parentNode : source_el.parentElement
         if (source_el.className=="menuitems"){
            source_el.id=(state=="on")? "mouseoverstyle" : "";
         }
      }
    }
  }
}

Popper.prototype.dynamichide = function(e){
  var self=this;
  if (ie4 && !self.menuobj.contains(e.toElement)) {
  	if (self.hidemenu) {
   	  self.hidemenu();
   	}
  }
  else {
    if (ns6 && e.currentTarget != e.relatedTarget && !contains_ns6(e.currentTarget, e.relatedTarget)) {
   	  self.hidemenu();
   	}
  }
}

Popper.prototype.showmenu = function(e,menuobj){
  if (!document.all && !document.getElementById && !document.layers)
    return;

  this.clearhidemenu();

  if (popper.menuobj) {
    this.hidemenu();
  }
 	popper.menuobj = menuobj;
 	popper.menuobj.thestyle = (ie4||ns6) ? menuobj.style : menuobj;

    // Get size and scroll position of window
    var frameWidth = getFrameWidth(window.top);
    var frameHeight = getFrameHeight(window.top);
    var frameLeft = window.pageXOffset || window.document.getElementsByTagName('body')[0].scrollLeft;
    var frameTop = window.pageYOffset || window.document.getElementsByTagName('body')[0].scrollTop;

    var menuobjWidth = (ie4||ns6)? menuobj.offsetWidth : menuobj.document.gui.document.width;
    var menuobjHeight = (ie4||ns6)? menuobj.offsetHeight : menuobj.document.gui.document.height
    var mouseX = ie4? event.clientX : ns6? e.clientX : e.x;
    var mouseY = ie4? event.clientY : ns6? e.clientY : e.y;

    var popitLeft = mouseX;
    var popitTop = mouseY;

    // adjust to right of screen
    if (popitLeft + menuobjWidth > frameWidth) popitLeft = frameWidth - menuobjWidth;
    // adjust to bottom of screen
    if (popitTop + menuobjHeight > frameHeight) popitTop = frameHeight - menuobjHeight;
    // adjust to left of screen
    if (popitLeft < frameLeft) popitLeft = frameLeft;
    // adjust to top of screen
    if (popitTop < frameTop) popitTop = frameTop;

    menuobj.thestyle.left = popitLeft;
    menuobj.thestyle.top = popitTop;

    delayshow=setTimeout("popper.showmenunow()",500);
    return false;
}

 /** Determines the width of the given window, or if non given the
  * current window (or frame).
  * @param win the window to measure (optional)
  */
function getFrameWidth (win) {
    if ( ! win) {
       win = window;
    }
    var width;
    if (win.innerWidth) {
        width = win.innerWidth;
    } else if (win.document.body.clientWidth) {
        width = win.document.body.clientWidth;
    } else {
        width = 863;   // default value for 1024x786
    }
    return width;
}

 /** Determines the height of the given window, or if non given the
  * current window (or frame).
  * @param win the window to measure (optional)
  */
function getFrameHeight (win) {
    if ( ! win) {
       win = window;
    }
    var height;
    if (win.innerHeight) {
        height = win.innerHeight;
    } else if (win.document.body.clientHeight) {
        height = win.document.body.clientHeight;
    } else {
        height = 543;   // default value for 1024x786
    }
    return height;
}


Popper.prototype.showmenunow = function(){
  if (this.menuobj) {
    popper.menuobj.thestyle.visibility="visible"
  }
}


Popper.prototype.hidemenu = function(){
  if (this.menuobj) {
    popper.menuobj.thestyle.visibility=(ie4||ns6)? "hidden" : "hide"
  }
}

Popper.prototype.delayhidemenu = function (){
  if (ie4||ns6||ns4)
    delayhide=setTimeout("popper.hidemenu()",500)
}

Popper.prototype.clearshowmenu = function(){
  if (window.delayshow)
    clearTimeout(delayshow)
}

Popper.prototype.clearhidemenu = function(){
  if (window.delayhide)
    clearTimeout(delayhide)
}

contains_ns6 = function(a, b) {
  //Determines if 1 element in contained in another
  while (b.parentNode)
    if ((b = b.parentNode) == a)
      return true;
  return false;
}