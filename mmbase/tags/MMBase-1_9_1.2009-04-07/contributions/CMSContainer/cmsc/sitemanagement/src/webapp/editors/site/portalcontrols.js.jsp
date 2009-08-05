<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="cmsc" scope="request" />

var usermode = 'all';
var oldChannelsCols = '0,*';
 
function frameLoaded() {
	if (isPortalPage()) {
		insertHTMLBeforeEnd( getBodyElement( getDocument('content') ), getPageControls());

		var jslocation = document.location.href;
		jslocation = jslocation.replace('index.jsp', 'portaledit.js.jsp');
		createJavascript( getDocument('content'), jslocation);
	}
}

function isPortalPage() {
	var location = frames['content'].location.href;
	return (location.indexOf('/editors/') < 0 && location.indexOf('/mmbase/') < 0);
}

function getBodyElement(doc) {
	return doc.getElementsByTagName("body")[0];
}

function getHeadElement(doc) {
	return doc.getElementsByTagName("head")[0];
}

function getDocument(framename) {
	return frames[framename].document;
}

function getFrame(framename) {
	return frames[framename];
}

function createStylesheet(doc, location) {
	var head = getHeadElement(doc);
	var stylesheet = doc.createElement("link");
	stylesheet.setAttribute("href", location);
	stylesheet.setAttribute("type", "text/css");
	stylesheet.setAttribute("rel", "stylesheet");
	head.appendChild(stylesheet);
}

function createJavascript(doc, location) {
	var head = getHeadElement(doc);
	var javascript = doc.createElement("script");
	javascript.setAttribute("src", location);
	javascript.setAttribute("type", "text/javascript");
	head.appendChild(javascript);
}

function getPageControls() {
	return '<div class="portalcontrols">'+
			'<div><fmt:message key="portalcontrols.title" />:</div>'+
			'<select name="usermode" id="usermode" onchange="return selectPortletMode(this);">' +
			'<option value="all"><fmt:message key="portalcontrols.mode.all" /></option>' +
			'<option value="site"><fmt:message key="portalcontrols.mode.site" /></option>' +
			'<option value="content"><fmt:message key="portalcontrols.mode.content" /></option>' +
			'<option value="preview"><fmt:message key="portalcontrols.mode.preview" /></option>' +
			'</select>'+
			'<input type="button" value="<fmt:message key="portalcontrols.hide" />" onclick="return switchChannelPanel(this);" />' +
			'</div>';
}

function insertHTMLBeforeEnd(oElement, sHTML) {
	if (oElement.insertAdjacentHTML != null) {
		oElement.insertAdjacentHTML("BeforeEnd", sHTML)
		return;
	}
	var df;	// DocumentFragment
	var r = oElement.ownerDocument.createRange();
	r.selectNodeContents(oElement);
	r.collapse(false);
	df = r.createContextualFragment(sHTML);
	oElement.appendChild(df);
}