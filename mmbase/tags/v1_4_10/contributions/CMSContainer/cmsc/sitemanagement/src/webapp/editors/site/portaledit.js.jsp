<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="cmsc" scope="request" />

function selectPortletMode(element) {
	setPortletModeForString(element.value);
	saveUserMode(element.value);
}

function switchChannelPanel(element) {
	if (window.top.bottompane.oldChannelsCols) {
		var oldChannelsCols = window.top.bottompane.oldChannelsCols;
		window.top.bottompane.sitemanagement
		
		var channelsCols = window.top.bottompane.document.body.cols;
		if (channelsCols == '0,*') {
			element.value = "<fmt:message key="portalcontrols.hide" />";
		}
		else {
			element.value = "<fmt:message key="portalcontrols.show" />";
		}
		window.top.bottompane.oldChannelsCols = channelsCols;
		window.top.bottompane.document.body.cols = oldChannelsCols;
	}	
}

function setPortletModeForString(value) {
	if (value == 'all') {
		setPortletMode(true, true, true);
	}
	if (value == 'site') {
		setPortletMode(true, false, true);
	}
	if (value == 'content') {
		setPortletMode(false, true, true);
	}
	if (value == 'preview') {
		setPortletMode(false, false, false);
	}
	
	setElementStyleByClassName('portlet-mode-spacer', 'display', (value == 'preview') ? 'none' : '');
	setElementStyleByClassName('portlet-header-canvas', 'display', (value == 'preview') ? 'none' : '');
	setElementStyleByClassName('portlet-canvas', 'borderWidth', (value == 'preview') ? '0px' : '1px');
	setElementStyleByClassName('portlet-mode-canvas portlet-mode-type-view', 'display', (value == 'preview') ? 'none' : '');
}

function setPortletMode(admin, edit, view) {
	setElementStyleByClassName('portlet-mode-type-admin', 'display', admin ? '' : 'none');
	setElementStyleByClassName('portlet-mode-type-edit', 'display', edit ? '' : 'none');
	setElementStyleByClassName('portlet-mode-type-view', 'display', view ? '' : 'none');
}

function setElementStyleById(id, propertyName, propertyValue) {
	if (!document.getElementById) return;
	var el = document.getElementById(id);
	if (el) el.style[propertyName] = propertyValue;
}

function setElementStyle(element, propertyName, propertyValue) {
	if (!document.getElementsByTagName) return;
	var el = document.getElementsByTagName(element);
	for (var i = 0; i < el.length; i++) {
		el[i].style[propertyName] = propertyValue;
	}
}

function setElementStyleByClassName(cl, propertyName, propertyValue) {
	if (!document.getElementsByTagName) return;
	var re = new RegExp("(^| )" + cl + "( |$)");
	var el = document.all ? document.all : document.getElementsByTagName("body")[0].getElementsByTagName("*"); // fix for IE5.x
	for (var i = 0; i < el.length; i++) {
		if (el[i].className && el[i].className.match(re)) {
			el[i].style[propertyName] = propertyValue;
		}
	}
}

function saveUserMode(value) {
	if (window.top.bottompane.usermode) {
		window.top.bottompane.usermode = value;
	}
}

function loadUserMode() {
	if (window.top.bottompane.usermode) {
		var usermode = window.top.bottompane.usermode;
		if (document.getElementById('usermode')) {
			document.getElementById('usermode').value = usermode;
		}
		setPortletModeForString(usermode);
	}
}

loadUserMode();

function hideMode(event, canvas, modes) {
	canvas = $(canvas);
  if (document.all && !canvas.contains(event.toElement)) {
	setElementStyleById(modes, 'display', 'none');
  }
  else {
    if (document.getElementById && event.currentTarget != event.relatedTarget && !contains_gecko(event.currentTarget, event.relatedTarget)) {
		setElementStyleById(modes, 'display', 'none');
   	}
  }
}

function showMode(event, canvas, modes) {
	canvas = $(canvas);

  if (document.all && canvas.contains(event.toElement)) {
	setElementStyleById(modes, 'display', 'block');
  }
  else {
    if (document.getElementById && event.currentTarget != event.relatedTarget && contains_gecko(event.currentTarget, event.relatedTarget)) {
		setElementStyleById(modes, 'display', 'block');
   	}
  }
}

contains_gecko = function(a, b) {
  //Determines if 1 element in contained in another
  while (b.parentNode)
    if ((b = b.parentNode) == a)
      return true;
  return false;
}