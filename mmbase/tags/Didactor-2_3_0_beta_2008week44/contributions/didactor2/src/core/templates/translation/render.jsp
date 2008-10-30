<%@page import="java.util.Vector,nl.didactor.taglib.TranslateTable"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>
  <style>
    div.floating {
      position: fixed;
      left: 10px;
      top: 10px;
      z-index: 100000000;
      width: 600px;
      background: #aaa;
      filter: alpha(opacity=90);
      -moz-opacity:0.9;
      padding: 1px;
    }
    .float_title {
      height: 1.1em;
      background: #333;
      color: #fff;
      font-weight: bold;
      padding: 1px;
    }
    .float_title a {
      color: #ffff;
    }
    div.titem {
      cursor: hand;
    }
  </style>
<script type="text/javascript">//<![CDATA[

//*****************************************************************************
// Do not remove this notice.
//
// Copyright 2001 by Mike Hall.
// See http://www.brainjar.com for terms of use.
//*****************************************************************************

// Determine browser and version.

function Browser() {

  var ua, s, i;

  this.isIE    = false;
  this.isNS    = false;
  this.version = null;

  ua = navigator.userAgent;

  s = "MSIE";
  if ((i = ua.indexOf(s)) >= 0) {
    this.isIE = true;
    this.version = parseFloat(ua.substr(i + s.length));
    return;
  }

  s = "Netscape6/";
  if ((i = ua.indexOf(s)) >= 0) {
    this.isNS = true;
    this.version = parseFloat(ua.substr(i + s.length));
    return;
  }

  // Treat any other "Gecko" browser as NS 6.1.

  s = "Gecko";
  if ((i = ua.indexOf(s)) >= 0) {
    this.isNS = true;
    this.version = 6.1;
    return;
  }
}

var browser = new Browser();

// Global object to hold drag information.

var dragObj = new Object();
dragObj.zIndex = 0;

function dragStart(event, id) {

  var el;
  var x, y;

  // If an element id was given, find it. Otherwise use the element being
  // clicked on.

  if (id)
    dragObj.elNode = document.getElementById(id);
  else {
    if (browser.isIE)
      dragObj.elNode = window.event.srcElement;
    if (browser.isNS)
      dragObj.elNode = event.target;

    // If this is a text node, use its parent element.

    if (dragObj.elNode.nodeType == 3)
      dragObj.elNode = dragObj.elNode.parentNode;
  }

  // Get cursor position with respect to the page.

  if (browser.isIE) {
    x = window.event.clientX + document.documentElement.scrollLeft
      + document.body.scrollLeft;
    y = window.event.clientY + document.documentElement.scrollTop
      + document.body.scrollTop;
  }
  if (browser.isNS) {
    x = event.clientX + window.scrollX;
    y = event.clientY + window.scrollY;
  }

  // Save starting positions of cursor and element.

  dragObj.cursorStartX = x;
  dragObj.cursorStartY = y;
  dragObj.elStartLeft  = parseInt(dragObj.elNode.style.left, 10);
  dragObj.elStartTop   = parseInt(dragObj.elNode.style.top,  10);

  if (isNaN(dragObj.elStartLeft)) dragObj.elStartLeft = 0;
  if (isNaN(dragObj.elStartTop))  dragObj.elStartTop  = 0;

  // Update element's z-index.

  dragObj.elNode.style.zIndex = ++dragObj.zIndex;

  // Capture mousemove and mouseup events on the page.

  if (browser.isIE) {
    document.attachEvent("onmousemove", dragGo);
    document.attachEvent("onmouseup",   dragStop);
    window.event.cancelBubble = true;
    window.event.returnValue = false;
  }
  if (browser.isNS) {
    document.addEventListener("mousemove", dragGo,   true);
    document.addEventListener("mouseup",   dragStop, true);
    event.preventDefault();
  }
}

function dragGo(event) {

  var x, y;

  // Get cursor position with respect to the page.

  if (browser.isIE) {
    x = window.event.clientX + document.documentElement.scrollLeft
      + document.body.scrollLeft;
    y = window.event.clientY + document.documentElement.scrollTop
      + document.body.scrollTop;
  }
  if (browser.isNS) {
    x = event.clientX + window.scrollX;
    y = event.clientY + window.scrollY;
  }

  // Move drag element by the same amount the cursor has moved.

  dragObj.elNode.style.left = (dragObj.elStartLeft + x - dragObj.cursorStartX) + "px";
  dragObj.elNode.style.top  = (dragObj.elStartTop  + y - dragObj.cursorStartY) + "px";

  if (browser.isIE) {
    window.event.cancelBubble = true;
    window.event.returnValue = false;
  }
  if (browser.isNS)
    event.preventDefault();
}

function dragStop(event) {

  // Stop capturing mousemove and mouseup events.

  if (browser.isIE) {
    document.detachEvent("onmousemove", dragGo);
    document.detachEvent("onmouseup",   dragStop);
  }
  if (browser.isNS) {
    document.removeEventListener("mousemove", dragGo,   true);
    document.removeEventListener("mouseup",   dragStop, true);
  }
}

//]]></script>
<script>
  function toggle(id) {
    var elem = document.getElementById(id);
    if (elem) {
      if (elem.style.display == 'none') {
        elem.style.display = 'block';
      } else {
        elem.style.display = 'none';
      }
    }
  }

  function edit(elem) {
    if (elem) {
      var oldinner = elem.innerHTML;
      if (!oldinner || oldinner.indexOf("<input") == -1) {
        if (oldinner) {
          oldinner = oldinner.replace("\"", "&quot;");
        }
        elem.innerHTML = "<input onKeyDown='dotype(this, \"" + elem.id + "\", event)' type='text' value=\"" + oldinner + "\" oldinner=\"" + oldinner + "\" style='width: 500px' />";
      }
    }
  }

  function dotype(input, id, e) {
    var element = document.getElementById(id);
    if (e.which == 13) { // enter
      var value = input.value;
      element.innerHTML = value;
      var message = element.getAttribute("message");
      var locale = element.getAttribute("locale");
      doRequest("<mm:treefile page="/translation/submit.jsp?a" objectlist="$includePath" referids="$referids" escapeamps="false"/>&value=" + escape(value) + "&message=" + message + "&locale=" + locale);
    } else if (e.which == 27) {  // escape
      element.innerHTML = input.getAttribute('oldinner');
    } else {
      return true;
    }
  }

var hD="0123456789ABCDEF";
function d2h(d) {
  var h = hD.substr(d&15,1);
  while(d>15) {d>>=4;h=hD.substr(d&15,1)+h;}
  return h;
}

function encode(input) {
  var res = "";
  var oldres = "";
  for (var i=0; i<input.length; i++) {
    var hex = d2h(input.charCodeAt(i));
    oldres += " " + hex;
    while (hex.length > 0) {
      res += "%" + hex.substring(0, 2);
      hex = hex.substring(2);
    }
  }
  return res;
}

var req;

function doRequest(url) {
  req = false;
  // branch for native XMLHttpRequest object
  if(window.XMLHttpRequest) {
    try {
      req = new XMLHttpRequest();
    } catch(e) {
      req = false;
    }
    // branch for IE/Windows ActiveX version
  } else if(window.ActiveXObject) {
    try {
      req = new ActiveXObject("Msxml2.XMLHTTP");
    } catch(e) {
      try {
        req = new ActiveXObject("Microsoft.XMLHTTP");
      } catch(e) {
        req = false;
      }
    }
  }
  if(req) {
    req.onreadystatechange = processReqChange;
    req.open("GET", url, true);
    req.send("");
  }
}

function processReqChange() {
  // only if req shows "loaded"
  if (req.readyState == 4) {
    // only if "OK"
    if (req.status == 200) {
      // ...processing statements go here...
    } else {
    }
  }
}

function toggleOpenClose(link) {
  var fbody = document.getElementById('float_body');
  if (fbody) {
    if (fbody.style.display == 'block') {
      fbody.style.display = 'none';
      link.innerHTML = 'open';
    } else {
      fbody.style.display = 'block';
      link.innerHTML = 'close';
    }
  }
}

</script>
  <div class="floating" id="box">
    <div class="float_title" onmousedown="dragStart(event, 'box')" >
      <span style="float: left">Translations</span>
      <span style="float: right"><a style="color: #fff" href="#" onclick="javascript: toggleOpenClose(this);">open</a></span>
    </div>
    <div class="float_body" id="float_body" style="display: none" />
    <ul>
    <% 
      Vector locales = new Vector(); 
      String loc = "";
      locales.add(loc);
    %>
    <mm:present referid="provider">
      <mm:node number="$provider" notfound="skip">
        <mm:field jspvar="locale" name="locale" write="false">
          <% loc += locale; if (loc.equals("")) { loc = "en"; } else { locales.add(loc); } %>
        </mm:field>
        <mm:present referid="education">
          <mm:node number="$education" notfound="skip">
            <mm:field jspvar="path" name="path" write="false"><% loc += "_" + path; locales.add(loc); %></mm:field>
            <mm:present referid="class">
              <mm:node number="$class" notfound="skip">
                <mm:field jspvar="path" name="path" write="false"><% loc += "_" + path; locales.add(loc); %></mm:field>
              </mm:node>
            </mm:present>
          </mm:node>
        </mm:present>
      </mm:node>
    </mm:present>
        
    <%
      Vector transIds = (Vector)pageContext.getAttribute("t_usedtrans", PageContext.REQUEST_SCOPE);
      if (transIds != null) {
        for (int i=0; i<transIds.size(); i++) {
          String key = (String)transIds.get(i);
          out.println("<li class='titem' onclick='toggle(\"tr" + i + "\")'>" + key + "</li>");
          out.println("<div id='tr" + i + "' style='display: none' onclick='edit(\"tr" + i + "\")' >");
          String prev = "--";
          for (int j=0; j<locales.size(); j++) {
            String translation = new TranslateTable((String)locales.get(j)).translate(key);
            if (translation == null) {
              translation = "(ERROR)";
            }
            out.print("" + locales.get(j) + " =&gt; ");
            if (translation.equals(prev)) {
              out.print("<i>"); 
            }
            out.print("<span locale='" + locales.get(j) + "' message='" + key + "' id='edit_" + i + "_" + locales.get(j) + "' onclick='edit(this)'>");
            out.print(translation);
            out.print("</span>");
            if (translation.equals(prev)) {
              out.print("</i>");
            }
            out.println("<br />");
            prev = translation;
          }
          out.println("</div>");
        }
      }
    %>
    </ul>
    </div>
  </div>
  <script type="text/javascript">
    // Temporarily disable this functionality on IE
    if (browser.isIE) {
      document.getElementById('box').style.display = 'none';
    }
  </script>
</mm:cloud>
</mm:content>
