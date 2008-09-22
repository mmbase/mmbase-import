<%@taglib uri="http://finalist.com/cmsc" prefix="cmsc"%>

addLoadEvent(fillIframes)

function fillIframes() {
	elements = getElementsByClass("portlet-config-canvas", document, "div");
	for(x = 0; x < elements.length; x++) {
		var element = elements[x];

		element.style.display = "block";
		var left = realLeftPosition(element);
		element.style.display = "none";
		
		fillIframe(element, left)
	}
	
	
	elements = getElementsByClass("portlet-mode-canvas", document, "div");
	for(x = 0; x < elements.length; x++) {
		var element = elements[x];

		var top = realTopPosition(element);

		var className = element.className;
		var mode = className.substring(className.lastIndexOf("-")+1, className.length);
		if(mode != "view") {
			self.scrollTo(0,top-35);
			break;
		}
	}
}


function getElementsByClass(searchClass,node,tag) {
	var classElements = new Array();
	if ( node == null )
		node = document;
	if ( tag == null )
		tag = '*';
	var els = node.getElementsByTagName(tag);
	var elsLen = els.length;
	var pattern = new RegExp('(^|\\s)'+searchClass+'(\\s|$)');
	for (i = 0, j = 0; i < elsLen; i++) {
		if ( pattern.test(els[i].className) ) {
			classElements[j] = els[i];
			j++;
		}
	}
	return classElements;
}

function realLeftPosition(element) {
	if(element == undefined) {
		return 0;
	}
	else {
		return element.offsetLeft + realLeftPosition(element.offsetParent);
	}
}

function realTopPosition(element) {
	if(element == undefined) {
		return 0;
	}
	else {
		return element.offsetTop + realTopPosition(element.offsetParent);
	}
}

function fillIframe(div, left) {
  var iframe = document.createElement("iframe");
  var parent = div.parentNode;
  var parentWidth = parent.offsetWidth
	
  iframe.frameBorder = 0;
  iframe.className = "portlet-config-iframe";

  var agt = navigator.userAgent.toLowerCase();
  var is_ie = ((agt.indexOf("msie") != -1) && (agt.indexOf("opera") == -1));
  var is_webkit = (agt.indexOf('webkit') != -1);
  if (is_ie || is_webkit)
    iframe.src = '<cmsc:staticurl page='/editors/empty.html' />';

  parent.appendChild(iframe);
  parent.style.height="323px";

  var parentdocument = document;
  var ifrmaeload = function(e)
  {
    if (!iframe._iframeLoadDone) {
      iframe._iframeLoadDone = true;
      var iframedoc;
      if ( iframe.contentDocument ) {
        iframedoc = iframe.contentDocument;        
      }
      else {
       iframedoc = iframe.contentWindow.document;
      }
      writeDocument(iframedoc, div, parentdocument);
    }
    return true;
  };

  iframe._iframeLoadDone = false

  addLoadEvent(ifrmaeload, iframe);
	
  var difference = parentWidth - iframe.clientWidth;
  if(difference < 0) {
    var clientWidth = document.body.clientWidth;
		
    if(left < (clientWidth-100)/2) {
      iframe.style.marginRight=difference+'px';
    }
    else {
      iframe.style.marginLeft=difference+'px';
    }
  }
}

function writeDocument(doc, div, parentdocument) {
	var javascriptWindow = "<cmsc:staticurl page='/js/window.js' />";
	var cssPortaledit = "<cmsc:staticurl page='/editors/site/portaledit.css' />";
    html = "<html>\n";
    html += "<head>\n";
    html += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n";
	html += "<script type='text/javascript' src='" + javascriptWindow + "'></script>"
	html += "<link rel='stylesheet' type='text/css' href='" + cssPortaledit + "' />";
    html += "</head>\n";
    html += "<body>\n";
    html +=   div.innerHTML;
    html += "</body>\n";
    html += "</html>";

	doc.open();
	doc.write(html);
	doc.close();
	
	for(count = 0; count < parentdocument.styleSheets.length; count++) {
		ss = parentdocument.styleSheets[count];
		createStylesheet( doc, ss.href);
	}
}

function createStylesheet(doc, location) {
	var head = getHeadElement(doc);
	var stylesheet = doc.createElement("link");
	stylesheet.setAttribute("href", location);
	stylesheet.setAttribute("type", "text/css");
	stylesheet.setAttribute("rel", "stylesheet");
	head.appendChild(stylesheet);
}

function getHeadElement(doc) {
	return doc.getElementsByTagName("head")[0];
}

function showInfo(id) {
	document.getElementById('portlet-info-'+id).style.display = 'block';
	document.getElementById('portlet-mode-'+id).style.zIndex = 2001;
}

function hideInfo(id) {
	document.getElementById('portlet-info-'+id).style.display = 'none';
	document.getElementById('portlet-mode-'+id).style.zIndex = 2000;
}