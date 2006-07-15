var usermode = 'all';
var oldChannelsCols = '0,*';

function frameLoaded() {
	if (isPortalPage()) {
		insertHTMLBeforeEnd( getBodyElement( getDocument('content') ), getPageControls());

		var jslocation = document.location.href;
		jslocation = jslocation.replace('index.jsp', 'portaledit.js');
		createJavascript( getDocument('content'), jslocation);

		var csslocation = document.location.href;
		csslocation = csslocation.replace('index.jsp', 'portaledit.css');
		createStylesheet( getDocument('content'), csslocation);
		
//		var prototypelocation = document.location.href;
//		prototypelocation = prototypelocation.replace('site/index.jsp', 'js/prototype.js');
//		createJavascript( getDocument('content'), prototypelocation);

//		var scriptaculouslocation = document.location.href;
//		scriptaculouslocation = scriptaculouslocation.replace('site/index.jsp', 'js/scriptaculous/effects.js');
//		createJavascript( getDocument('content'), scriptaculouslocation);
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
			'<input type="button" value="hide" onclick="return switchChannelPanel(this);" />' +
			'<select name="usermode" id="usermode" onchange="return selectPortletMode(this);">' +
			'<option value="all">All</option>' +
			'<option value="site">Site</option>' +
			'<option value="content">Content</option>' +
			'<option value="preview">Preview</option>' +
			'</select>';
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