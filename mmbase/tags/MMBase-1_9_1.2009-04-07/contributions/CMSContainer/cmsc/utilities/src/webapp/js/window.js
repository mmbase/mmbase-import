
function openPopupWindow(windowName, width, height, url) {      
    if (!width) {w = 750;} else { w = width; }
    if (!height) {h = 550;} else { h = height; }
    if (!url) {url = "";}
	var options = getPopupPositionProps(w, h) + ',scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=yes';
    var w = window.open(url, windowName, options);    
	w.focus();
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
    } else if (document.documentElement && document.documentElement.clientWidth) {
       width = document.documentElement.clientWidth;
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
    } else if (document.documentElement && document.documentElement.clientWidth) {
       height = document.documentElement.clientHeight;
    } else {
        height = 543;   // default value for 1024x786
    }
    return height;
}

function getOuterFrameWidth (win) {
    if ( ! win) {
       win = window;
    }
    var width;
    if(win.document.layers || (win.document.getElementById && !win.document.all)){
       width = win.outerWidth;
	} else if(win.document.all){
       width = win.document.body.clientWidth;
    } else {
        width = 1024;   // default value for 1024x786
    }
    return width;
}

function getOuterFrameHeight (win) {
    if ( ! win) {
       win = window;
    }
    var height;
    if(win.document.layers || (win.document.getElementById && !win.document.all)){
       height = win.outerHeight;
	} else if(win.document.all){
       height = win.document.body.clientHeight;
    } else {
       height = 786;   // default value for 1024x786
    }
    return height;
}


/**
 * Gives properties that determine size and position of a popup window
 * for the given size (modal or non-modal). These properties can be
 * passed to the window.open function.
 *
 * @param width the width of the window (pixels)
 * @param height the heigth of the window (pixels)
 *
 * <p>Example usage:
 * <pre>
 * var url = getBaseHref() + 'url';
 * var name = 'windowname';
 * var prop = getPopupPositionProps(880,400) + ',scrollbars=auto,resizable=yes';
 * window.open(url, name, prop);
 * </pre>
 */
function getPopupPositionProps(width, height) {
    var topwin = window.top;
    var openerx = topwin.screenLeft != null ? topwin.screenLeft : topwin.screenX;
    var openery = topwin.screenTop != null ? topwin.screenTop : topwin.screenY;
    var openerw = getFrameWidth(topwin.top);
    var openerh = getFrameHeight(topwin.top);
    
    var browsew = getOuterFrameWidth(topwin.top);
    var browseh = getOuterFrameHeight(topwin.top);
    
    var screenMinX = screen.availLeft != null ? screen.availLeft : 0;
    var screenMinY = screen.availTop != null ? screen.availTop : 0;
    var screenMaxX = screen.availWidth != null ? screen.availWidth : 0;
    var screenMaxY = screen.availHeight != null ? screen.availHeight : 0;

    var x = openerx + Math.round((openerw - width) / 2);
    var y = openery + Math.round((openerh - height) / 2);
    
    // adjust to right of screen
    if (x + parseInt(width) > screenMaxX) x = screenMaxX - width;
    // adjust to bottom of screen
    if (y + parseInt(height) > screenMaxY) y = screenMaxY - height;
    // adjust to left of screen
    if (x < screenMinX) x = screenMinX;
    // adjust to top of screen
    if (y < screenMinY) y = screenMinY;
    // toolbars are included in centering. adjust when possible to cneter of document area
    if (height < openerh) y = y + (browseh - openerh);
    
    return 'height=' + height +',width=' + width + ',left=' + x + ',top=' + y;
}

function refreshFrame(name, win, parentcall) {
	if (!win) {
		if (!refreshFrame(name, window)) {
			alert("Window/frame with name '" + name + "' not found");
			return false;
		}
		else {
			return true;
		}
	}
	if (win.name == name) {
	   url = win.location.href;
	   ref = '';
	   if ((index = url.indexOf("#")) != -1) {
	      ref = url.substring(index, url.length);
	      url = url.substring(0, index);
	   }
	   if (url.indexOf("?") == -1) {
	      url += "?";
	   }
	   else {
	      url += "&";
	   }
	   //added a dummy parameter to force a reload
	   win.location.replace(url + "dummy=" + new Date().getMilliseconds() + ref);
		return true;
	}
	else {
		for (var i = 0; i < win.frames.length; i++) {
	  if(refreshFrame(name, win.frames[i], true)) {
	  	return true;
	 	}
	 }
	 if (win.parent && win != parent && !parentcall) {
	 	return refreshFrame(name, win.parent);
	 }
	 if (win.opener) {
	 	return refreshFrame(name, win.opener);
	 }
	 return false;
 }
}

function openUrlInFrame(name, url, win, parentcall) {
	if (!win) {
		if (!openUrlInFrame(name, url, window)) {
			alert("Window/frame with name '" + name + "' not found");
			return false;
		}
		else {
			return true;
		}
	}
	if (win.name == name) {
		win.location = url;
		return true;
	}
	else {
		for (var i = 0; i < win.frames.length; i++) {
	  if(openUrlInFrame(name, url, win.frames[i], true)) {
	  	return true;
	 	}
	 }
	 if (win.parent && win != parent && !parentcall) {
	 	return openUrlInFrame(name, url, win.parent);
	 }
	 if (win.opener) {
	 	return openUrlInFrame(name, url, win.opener);
	 }
	 return false;
 }
}

// addLoadEvent(functieNaam)
function addLoadEvent(func, windowElement) {
  if (typeof func != 'function') return;
  if (windowElement == undefined) {
     windowElement = window; 
  }

  var newonload;
  if (typeof windowElement.onload != 'function') {
    newonload = func;
  } else {
    var oldonload = windowElement.onload;
    newonload = function() {
      oldonload();
      func();
    }
  }
  
  if ( document.addEventListener ) {
    windowElement.addEventListener("load", newonload, true);
  }
  else if ( document.attachEvent ) {
      windowElement.attachEvent("onload", newonload);
  }
}