<!-- 
   var debug = false;
   
   function openPopupWindow(windowName, width, height) {      
      if (!width) {w = 750;} else { w = width; }
      if (!height) {h = 750;} else { h = height; }
      var left = (screen.availWidth - w)/2;
      var top = (screen.availHeight - h)/2;
      if (screen.availWidth < w) {
         w = screen.availWidth;
         left = 0;
      }
      if (screen.availHeight < h) {
         h = screen.availHeight - 25;
         top = 0;
      }
      if (debug) {
         alert("width: " + w + " " + left);
         alert("height: " + h + " " + top);      
      }

      str = 'window.open("","' + windowName + '","width=' + w + ',height=' + h + ',left=' + left + ',top=' + top + ',scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=yes")';    
      if (debug) {
         alert(str);
      }
	   eval(str);
	   return true;
   }
  
   /// selector js functions      
  
   function setDebug(b) {
      debug = b;
   }
  
  /** */
   function refreshOpenerAndClose(refreshpage, pagenumber) {
      newLocation = refreshpage + '?pagenumber='+pagenumber;
      callStr = "opener.top.location='"+newLocation+"'";
      if (debug) {
         alert(callStr);
      }
      eval(callStr);
      window.close();
   }
   
   /** */
   function refreshOpenerFrameAndClose(refreshpage, refreshframe, pagenumber) {
      newLocation = refreshpage + '?pagenumber='+pagenumber;
      callStr = "opener.top." + refreshframe + ".location='"+newLocation+"'";
      if (debug) {
         alert(callStr);
      }
      eval(callStr);
      window.close();
   }
   
   /** */
   function callJsFunctionInFrame(functionname, frame, pagenumber, pagename) {      
      callStr = "opener.top." + frame + "." + functionname + "('" + pagenumber + "', '" + pagename + "')";
      if (debug) {
         alert(callStr);
      }
      eval(callStr);
      window.close();
   }
   
   /** */
   function callJsFunction(functionname, pagenumber, pagename) {
      callStr = "opener.top." + functionname + "(" + pagenumber + ", " + pagename + ")";
      if (debug) {
         alert(callStr);
      }
      eval(callStr);
      window.close();
   }
  
  function refreshOpener(loc, param) { 
    opener.top.location = loc + "?" + param;
  }
// -->