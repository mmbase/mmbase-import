/*
 * this function can get the div of ajaxtree menu
 */
var NAVIGATION = "CMSC-NAVIGATION";
function getContextMenuDiv() {
   var menudiv = document.getElementById("_contextmenu");
   if (menudiv==null) {
		 var contextMenuNode = document.createElement("DIV");
         contextMenuNode.style.position="absolute";
         contextMenuNode.style.zIndex = 10000;
         contextMenuNode.style.display = "none";
         contextMenuNode.style.backgroundColor = "menu";
         contextMenuNode.className="htmlarea-context-menu";
         contextMenuNode.id="_contextmenu";
         contextMenuNode.name="_contextmenu";
         document.body.appendChild(contextMenuNode);
		 menudiv = document.getElementById("_contextmenu");
  }
  return menudiv;
}

/*
 * this function hide the ajaxtree menu
 */
function hideContextMenu () {
    var menudiv = getContextMenuDiv();
    menudiv.style.display="none";
}

var ajaxTreeConfig = {
    url                 : null,
	resources           : 'images/',
	addressbarId        : null,
	rootIcon            : function () { return this.resources + 'foldericon.png'; },
	openRootIcon        : function () { return this.resources + 'openfoldericon.png'; },
	folderIcon          : function () { return this.resources + 'foldericon.png'; },
	openFolderIcon      : function () { return this.resources + 'openfoldericon.png'; },
	fileIcon            : function () { return this.resources + 'file.png'; },
	iIcon               : function () { return this.resources + 'I.png'; },
	lIcon               : function () { return this.resources + 'L.png'; },
	lMinusIcon          : function () { return this.resources + 'Lminus.png'; },
	lPlusIcon           : function () { return this.resources + 'Lplus.png'; },
	tIcon               : function () { return this.resources + 'T.png'; },
	tMinusIcon          : function () { return this.resources + 'Tminus.png'; },
	tPlusIcon           : function () { return this.resources + 'Tplus.png'; },
	blankIcon           : function () { return this.resources + 'blank.png'; },
	optionIcon          : function () { return this.resources + 'option.png'; },
	loadingIcon         : function () { return this.resources + 'blank.png'; },
	defaultText         : 'Tree Item',
	defaultAction       : 'javascript:void(0);',
	defaultBehavior     : 'classic',
	usePersistence	    : true,
	defaultPersistentId : '-1',
	role                : '',
	loadingText         : '.....'
};

function markNode(firstnode,cssName) {
	document.getElementById(firstnode.id + '-anchor').className = cssName;
	var allchildren = firstnode.childNodes;

	if (allchildren) {
		for (var i=0;i<allchildren.length;i++) {
			markNode(allchildren[i],cssName);
		}

	}
}

var ajaxTreeHandler = {
	idCounter : 0,
	idPrefix  : "ajax-tree-object-",
	all       : {},
	behavior  : null,
	selected  : null,
	onSelect  : null, /* should be part of tree, not handler */
	getId     : function() { return this.idPrefix + this.idCounter++; },
	toggle    : function (oItem) { this.all[oItem.id.replace('-plus','')].toggle(); },
	select    : function (oItem) { this.all[oItem.id.replace('-icon','')].select(); },
	focus     : function (oItem) { this.all[oItem.id.replace('-anchor','')].focus(); },
	blur      : function (oItem) { this.all[oItem.id.replace('-anchor','')].blur(); },
	isclick   : function () { alldragObject.isover = false;},
	imouseover: function (oItem) {
		alldragObject.insertitem = this.all[oItem.id.replace('-anchor','')];
		return alldragObject.insertitem;
		},
	initcopy  : function(oItem) {
		var copyObject = document.createElement("div");
		var inner = copyTreetoString(oItem);
		copyObject.innerHTML = inner;
		document.body.appendChild(copyObject);
		return copyObject;
	},
	makeDraggable : function (oItem){
		if(!oItem) return;
		//if document.onmousedown is not null, it means that oItem is copyObject but not
		//dargObject. Then do nothing and return
		if(document.onmousedown) return;
		document.onmousedown = function(ev){
		//if document.onmouseup is not null, it means that document.onmousedown has not
		//been destroyed.If you down another mouse button here, it should do nothing and return		
		if(document.onmouseup) return;
		document.onmousemove = alldragObject.mouseMove(ev);
		document.onmouseup = alldragObject.mouseUp(ev);
		oItem.onclick = ajaxTreeHandler.isclick;
		alldragObject.dragObject = ajaxTreeHandler.all[oItem.id.replace('-icon','')];
		alldragObject.copyObject = ajaxTreeHandler.initcopy(ajaxTreeHandler.all[oItem.id.replace('-icon','')]);
		return false;
		}
	},
	markSubTree  : function (oItemID,cssName) {

		var itemNode = this.all[oItemID];
		if (itemNode!=null) {
			markNode(itemNode,cssName);
		}
	},
	showContextMenu : function(oItem,evt) {

		//get all options of the tree node
		var menuoptions = this.all[oItem.id].options;
		if (menuoptions==null || menuoptions.length==0) {
		  return;
		}
		alldragObject.isover = false;
		//create new contextmenu items
		var htmlcontent = "<table>";
		for (var i=0;i<menuoptions.length;i++){
		    htmlcontent += menuoptions[i].toString(oItem.id);
		}
		htmlcontent += "</table>";

		//add items to context menu
		var menudiv = getContextMenuDiv();
		menudiv.innerHTML = htmlcontent;

		//setting menu's location
		menudiv.style.top=evt.clientY + 'px';
		menudiv.style.left=evt.clientX + 'px';


		//show context menu
		menudiv.style.display="";

		// this must be below the display, because otherwise we will not know the dimensions of the div.
		var leftdiv = document.getElementById("left");
		var sUserAgent=navigator.userAgent;
		if(sUserAgent.indexOf("Mozilla/5.0")>-1){
			var lwstr=""+(leftdiv.clientWidth/0.24);
			var st;
			if(lwstr.indexOf(".")>-1)
				st=lwstr.substring(0,lwstr.indexOf("."))- menudiv.clientWidth;
			else
				st=lwstr-menudiv.clientWidth;
			if(st < evt.clientX) {
				menudiv.style.left = (st) + 'px';
			}
		}
		else{
			if(leftdiv.clientWidth - menudiv.clientWidth < evt.clientX) {
				menudiv.style.left = (leftdiv.clientWidth - menudiv.clientWidth) + 'px';
			}
		}
		if(document.body.clientHeight - menudiv.clientHeight < evt.clientY) {
				menudiv.style.top = (document.body.clientHeight - menudiv.clientHeight) + 'px';
			}
		if (menudiv.focus) {
			menudiv.focus();
		}
		alphaImages();

		document.onclick=hideContextMenu;
/*		//when context menu lost focus, it should be hidden
 		window.onfocusout=function() {
            alert(event.srcElement.tagName);
            if (event.srcElement==menudiv) {
                hideContextMenu();
                event.cancelBubble=true;
            } else {
                event.cancelBubble=false;
            }
       }
*/
	},

	keydown   : function (oItem, e) { return this.all[oItem.id].keydown(e.keyCode); },
	insertHTMLBeforeEnd	:	function (oElement, sHTML) {
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
	},
	updateAddressBar : function (node) {
		if (ajaxTreeConfig.addressbarId) {
			var element = document.getElementById(ajaxTreeConfig.addressbarId);
			var addressBarValue = node.fragment;
			while (node.parentNode) {
				node = node.parentNode;
				addressBarValue = node.fragment + '/' + addressBarValue;
			}
			element.value = addressBarValue;
		}
	}
};

ajaxTreeLoader = {
	initTree : function (persistentId, elementId) {
		var treeAction = new AjaxTreeAction();
		treeAction.elementId = elementId;
		treeAction.execute('inittree', persistentId);
	},
	loadChildren : function (node) {
		var treeAction = new AjaxTreeAction();
		treeAction.node = node;
		treeAction.execute('loadchildren', node.persistentId);
	},
	collapse : function (node) {
		var treeAction = new AjaxTreeAction();
		treeAction.execute('collapse', node.persistentId);
	},
	expand : function (node) {
		var treeAction = new AjaxTreeAction();
		treeAction.execute('expand', node.persistentId);
	}
};

/*
 * AjaxTreeAction class
 */
function AjaxTreeAction() {
}

AjaxTreeAction.prototype.execute = function(action, persistentId) {
    var options  = {asynchronous : true, onFailure: this.errorRequest };
    options.onSuccess = null;
	options.parameters = 'action=' + action;
	options.parameters += '&persistentid=' + persistentId;
	if (action == 'inittree') {
		options.onSuccess = this.buildTree.bind(this);
	}
	if (action == 'loadchildren') {
		options.onSuccess = this.buildChildren.bind(this);
	}
    new Ajax.Request(ajaxTreeConfig.url, options);
}

AjaxTreeAction.prototype.buildTree = function(request) {
	try {
		if(!request.responseXML||!request.responseText){
			window.parent.location.href="#";
			}
		else{
		var treeXml = request.responseXML.getElementsByTagName("tree")[0];
		var tree = this.createTree(treeXml);
		var element = document.getElementById(this.elementId);
		element.innerHTML = tree.toString();
				}
		
		try {
			alphaImages();
		}
		catch(e2) {
			// ignore
		}
	} catch(e) {
		alert("ERROR: AjaxTreeAction.prototype.buildTree ("+request.responseText+")");
	}
}

AjaxTreeAction.prototype.buildChildren = function(request) {
	try {
		var treeXml = request.responseXML.getElementsByTagName("tree")[0];
		var items = treeXml.childNodes;
		for (var i = 0 ; i < items.length ; i++) {
			var itemXml = items[i];
			if (itemXml.tagName == "item") {
				var itemXml = items[i];
				var item = this.createItem(itemXml);
				this.node.add(item, false);
			}
		}

		// remove dummy
		if (this.node._loadingItem != null) {
			this.node._loadingItem.remove();
		}

		this.node.indent()
		this.node.loaded = true;
		this.node.doExpand();

		try {
			alphaImages();
		}
		catch(e2) {
			// ignore
		}
	} catch(e) {
		alert("ERROR: AjaxTreeAction.prototype.buildChildren ("+request.responseText+")");
	}
}

AjaxTreeAction.prototype.errorRequest = function(request) {
	alert(request.responseText);
}

AjaxTreeAction.prototype.createTree = function(treeXml) {
	var text = treeXml.getAttribute("text");
	var action = treeXml.getAttribute("action");
	var behavior = treeXml.getAttribute("behavior");
	var icon = treeXml.getAttribute("icon");
	var openIcon = treeXml.getAttribute("openIcon");
	var target = treeXml.getAttribute("target");
	var open = treeXml.getAttribute("open");
	var loaded = treeXml.getAttribute("loaded");
	var persistentId = treeXml.getAttribute("persistentId");
	var fragment = treeXml.getAttribute("fragment");

	var tree = new AjaxTree(text, action, behavior, icon, openIcon, target, open, loaded, persistentId, fragment);
	var options = treeXml.childNodes;
	for (var i = 0 ; i < options.length ; i++) {
		var optionXml = options[i];
		if (optionXml.tagName == "option") {
			var option = this.createOption(optionXml);
			tree.addOption(option);
		}
	}
	var items = treeXml.childNodes;
	for (var i = 0 ; i < items.length ; i++) {
		var itemXml = items[i];
		if (itemXml.tagName == "item") {
			var item = this.createItem(itemXml);
			tree.add(item);
		}
	}
	return tree;
}

AjaxTreeAction.prototype.createItem = function(treeXml) {
	var text = treeXml.getAttribute("text");
	var action = treeXml.getAttribute("action");
	var icon = treeXml.getAttribute("icon");
	var openIcon = treeXml.getAttribute("openIcon");
	var target = treeXml.getAttribute("target");
	var open = treeXml.getAttribute("open");
	var loaded = treeXml.getAttribute("loaded");
	var persistentId = treeXml.getAttribute("persistentId");
	var fragment = treeXml.getAttribute("fragment");

	var tree = new AjaxTreeItem(text, action, null, icon, openIcon, target, open, loaded, persistentId, fragment);
	var options = treeXml.childNodes;
	for (var i = 0 ; i < options.length ; i++) {
		var optionXml = options[i];
		if (optionXml.tagName == "option") {
			var option = this.createOption(optionXml);
			tree.addOption(option);
		}
	}
	var items = treeXml.childNodes;
	for (var i = 0 ; i < items.length ; i++) {
		var itemXml = items[i];
		if (itemXml.tagName == "item") {
			var item = this.createItem(itemXml);
			tree.add(item);
		}
	}
	return tree;
}

AjaxTreeAction.prototype.createOption = function(treeXml) {
	var text = treeXml.getAttribute("text");
	var action = treeXml.getAttribute("action");
	var icon = treeXml.getAttribute("icon");
	var target = treeXml.getAttribute("target");
	var tree = new AjaxTreeOption(text, action, icon, target);
	return tree;
};

/*
 * AjaxTreeAbstractNode class
 */

function AjaxTreeAbstractNode(sText, sAction, sTarget, sLoaded, sPersistentId, sFragment) {
	this.childNodes  = [];
	this.options  = [];
	this.id     = ajaxTreeHandler.getId();
	this.text   = sText || ajaxTreeConfig.defaultText;
	this.action = sAction || ajaxTreeConfig.defaultAction;
	if (sTarget) this.target = sTarget;
	this.loading = false;
	this.loaded = sLoaded == 'true' || false;
	this.persistentId = sPersistentId || ajaxTreeConfig.defaultPersistentId;
	if (sFragment) this.fragment = sFragment;
	this._last  = false;
	ajaxTreeHandler.all[this.id] = this;
}

/*
 * To speed thing up if you're adding multiple nodes at once (after load)
 * use the bNoIdent parameter to prevent automatic re-indentation and call
 * the obj.ident() method manually once all nodes has been added.
 */

AjaxTreeAbstractNode.prototype.add = function (node, bNoIdent) {
	node.parentNode = this;
	this.childNodes[this.childNodes.length] = node;
	var root = this;
	if (this.childNodes.length >= 2) {
		this.childNodes[this.childNodes.length - 2]._last = false;
	}
	while (root.parentNode) { root = root.parentNode; }
	if (root.rendered) {
		if (this.childNodes.length >= 2) {
			var treeIcon;
			if (this.childNodes[this.childNodes.length -2].folder) {
				if (this.childNodes[this.childNodes.length -2].open) {
					treeIcon = ajaxTreeConfig.tMinusIcon();
				} else {
					treeIcon = ajaxTreeConfig.tPlusIcon();
				}
			} else {
				treeIcon = ajaxTreeConfig.tIcon();
			}
			document.getElementById(this.childNodes[this.childNodes.length - 2].id + '-plus').src = treeIcon;
			this.childNodes[this.childNodes.length - 2].plusIcon = ajaxTreeConfig.tPlusIcon();
			this.childNodes[this.childNodes.length - 2].minusIcon = ajaxTreeConfig.tMinusIcon();
			this.childNodes[this.childNodes.length - 2]._last = false;
		}
		this._last = true;
		var foo = this;
		while (foo.parentNode) {
			for (var i = 0; i < foo.parentNode.childNodes.length; i++) {
				if (foo.id == foo.parentNode.childNodes[i].id) { break; }
			}
			if (i == foo.parentNode.childNodes.length - 1) { foo.parentNode._last = true; }
			else { foo.parentNode._last = false; }
			foo = foo.parentNode;
		}
		ajaxTreeHandler.insertHTMLBeforeEnd(document.getElementById(this.id + '-cont'), node.toString());
		if ((!this.folder) && (!this.openIcon)) {
			this.icon = ajaxTreeConfig.folderIcon();
			this.openIcon = ajaxTreeConfig.openFolderIcon();
		}
		if (!this.folder) { this.folder = true; this.collapse(true); }
		if (!bNoIdent) { this.indent(); }
	}
	return node;
}

AjaxTreeAbstractNode.prototype.addOption = function (node) {
	this.options[this.options.length] = node;
}

AjaxTreeAbstractNode.prototype.toggle = function() {
	if (this.folder || !this.loaded) {
		if (this.open) { this.collapse(); }
		else { this.expand(); }
	}
}

AjaxTreeAbstractNode.prototype.select = function() {
	document.getElementById(this.id + '-anchor').focus();
}

AjaxTreeAbstractNode.prototype.deSelect = function() {
	document.getElementById(this.id + '-anchor').className = '';
	ajaxTreeHandler.selected = null;
}

AjaxTreeAbstractNode.prototype.focus = function() {
	if ((ajaxTreeHandler.selected) && (ajaxTreeHandler.selected != this)) {
		ajaxTreeHandler.selected.deSelect();
	}

	ajaxTreeHandler.selected = this;
	if ((this.openIcon) && (ajaxTreeHandler.behavior != 'classic')) { document.getElementById(this.id + '-icon').src = this.openIcon; }
	document.getElementById(this.id + '-anchor').className = 'selected';
	document.getElementById(this.id + '-anchor').focus();

	ajaxTreeHandler.updateAddressBar(this);
	if (ajaxTreeHandler.onSelect) { ajaxTreeHandler.onSelect(this); }
}

AjaxTreeAbstractNode.prototype.blur = function() {
	if ((this.openIcon) && (ajaxTreeHandler.behavior != 'classic')) { document.getElementById(this.id + '-icon').src = this.icon; }
	document.getElementById(this.id + '-anchor').className = 'selected-inactive';
}

AjaxTreeAbstractNode.prototype.doExpand = function() {
	if (ajaxTreeConfig.usePersistence && !this.loaded) {
		if (!this.loading) {
			this.loading = true;
			// and create loading item if not
			var loadingIcon = ajaxTreeConfig.loadingIcon();
			this._loadingItem = new AjaxTreeItem(ajaxTreeConfig.loadingText, null, null, loadingIcon, null, null, 'true', 'true');
			this.add(this._loadingItem);
			this.openTreeItem();

			ajaxTreeLoader.loadChildren(this);
		}
	}
	else{
		this.openTreeItem();
		if (ajaxTreeConfig.usePersistence) {
			ajaxTreeLoader.expand(this);
		}
		ajaxTreeHandler.updateAddressBar(this);

	}
}

AjaxTreeAbstractNode.prototype.openTreeItem = function() {
		if (ajaxTreeHandler.behavior == 'classic') { document.getElementById(this.id + '-icon').src = this.openIcon; }
		if (this.childNodes.length) {  document.getElementById(this.id + '-cont').style.display = 'block'; }
		this.open = true;
}

AjaxTreeAbstractNode.prototype.doCollapse = function() {
	if (ajaxTreeHandler.behavior == 'classic') { document.getElementById(this.id + '-icon').src = this.icon; }
	if (this.childNodes.length) { document.getElementById(this.id + '-cont').style.display = 'none'; }
	this.open = false;
	if (ajaxTreeConfig.usePersistence) {
		ajaxTreeLoader.collapse(this);
	}
	ajaxTreeHandler.updateAddressBar(this);
}

AjaxTreeAbstractNode.prototype.expandAll = function() {
	this.expandChildren();
	if ((this.folder) && (!this.open)) { this.expand(); }
}

AjaxTreeAbstractNode.prototype.expandChildren = function() {
	for (var i = 0; i < this.childNodes.length; i++) {
		this.childNodes[i].expandAll();
	}
}

AjaxTreeAbstractNode.prototype.collapseAll = function() {
	this.collapseChildren();
	if ((this.folder) && (this.open)) { this.collapse(true); }
}

AjaxTreeAbstractNode.prototype.collapseChildren = function() {
	for (var i = 0; i < this.childNodes.length; i++) {
		this.childNodes[i].collapseAll();
	}
}

AjaxTreeAbstractNode.prototype.indent = function(lvl, del, last, level, nodesLeft) {
	/*
	 * Since we only want to modify items one level below ourself,
	 * and since the rightmost indentation position is occupied by
	 * the plus icon we set this to -2
	 */
	if (lvl == null) { lvl = -2; }
	var state = 0;
	for (var i = this.childNodes.length - 1; i >= 0 ; i--) {
		state = this.childNodes[i].indent(lvl + 1, del, last, level);
		if (state) { return; }
	}
	if (del) {
		if ((level >= this._level) && (document.getElementById(this.id + '-plus'))) {
			if (this.folder) {
				document.getElementById(this.id + '-plus').src = (this.open)?ajaxTreeConfig.lMinusIcon():ajaxTreeConfig.lPlusIcon();
				this.plusIcon = ajaxTreeConfig.lPlusIcon();
				this.minusIcon = ajaxTreeConfig.lMinusIcon();
			}
			else if (nodesLeft) { document.getElementById(this.id + '-plus').src = ajaxTreeConfig.lIcon(); }
			return 1;
		}
	}
	var foo = document.getElementById(this.id + '-indent-' + lvl);
	if (foo) {
		if ((foo._last) || ((del) && (last))) { foo.src =  ajaxTreeConfig.blankIcon(); }
		else { foo.src =  ajaxTreeConfig.iIcon(); }
	}
	return 0;
};

/*
 * AjaxTree class
 */

function AjaxTree(sText, sAction, sBehavior, sIcon, sOpenIcon, sTarget, sOpen, sLoaded, sPersistentId, sFragment) {
	this.base = AjaxTreeAbstractNode;
	this.base(sText, sAction, sTarget, sLoaded, sPersistentId, sFragment);
	this.icon      = sIcon || ajaxTreeConfig.rootIcon();
	this.openIcon  = sOpenIcon || ajaxTreeConfig.openRootIcon();
	this.open      = sOpen == 'true' || true;
	this.folder    = true;
	this.rendered  = false;
	this.onSelect  = null;
	if (!ajaxTreeHandler.behavior) {  ajaxTreeHandler.behavior = sBehavior || ajaxTreeConfig.defaultBehavior; }
}

AjaxTree.prototype = new AjaxTreeAbstractNode;

AjaxTree.prototype.setBehavior = function (sBehavior) {
	ajaxTreeHandler.behavior =  sBehavior;
}

AjaxTree.prototype.getBehavior = function (sBehavior) {
	return ajaxTreeHandler.behavior;
}

AjaxTree.prototype.getSelected = function() {
	if (ajaxTreeHandler.selected) { return ajaxTreeHandler.selected; }
	else { return null; }
}

AjaxTree.prototype.remove = function() { }

AjaxTree.prototype.expand = function() {
	this.doExpand();
}

AjaxTree.prototype.collapse = function(b) {
	if (!b) { this.focus(); }
	this.doCollapse();
}

AjaxTree.prototype.getFirst = function() {
	return null;
}

AjaxTree.prototype.getLast = function() {
	return null;
}

AjaxTree.prototype.getNextSibling = function() {
	return null;
}

AjaxTree.prototype.getPreviousSibling = function() {
	return null;
}

AjaxTree.prototype.keydown = function(key) {
	if (key == 39) {
		if (!this.open) { this.expand(); }
		else if (this.childNodes.length) { this.childNodes[0].select(); }
		return false;
	}
	if (key == 37) { this.collapse(); return false; }
	if ((key == 40) && (this.open) && (this.childNodes.length)) { this.childNodes[0].select(); return false; }
	return true;
}

AjaxTree.prototype.toString = function() {
	var sbOption = [];
	for (var i = 0; i < this.options.length; i++) {
		sbOption[i] = this.options[i].toString(i,this.options.length);
	}
	var str = "<div id=\"" + this.id + "\" ondblclick=\"ajaxTreeHandler.toggle(this);\" class=\"ajax-tree-item\" style.position = 'absolute';" +
				"onkeydown=\"return ajaxTreeHandler.keydown(this, event)\"  oncontextmenu=\"ajaxTreeHandler.showContextMenu(this,event);return false\">" +
		"<img id=\"" + this.id + "-icon\" class=\"ajax-tree-icon\" src=\"" +
			((ajaxTreeHandler.behavior == 'classic' && this.open)?this.openIcon:this.icon)
			+ "\" onclick=\"ajaxTreeHandler.select(this);\" />";

	if(hasRights(this.icon)) {
		str += "<a href=\"" + this.action + "\" id=\"" + this.id + "-anchor\" onfocus=\"ajaxTreeHandler.focus(this);\" onmouseover=\"ajaxTreeHandler.imouseover(this); return true;\" " +
			"onblur=\"ajaxTreeHandler.blur(this);\"" + (this.target ? " target=\"" + this.target + "\"" : "") +
			 ">" + this.text + "</a>"
	}
	else {
		str +=	"<font style='color:#777'>"+this.text+"</font>"
	}

		str += "</div>" +
		"<div id=\"" + this.id + "-cont\" class=\"ajax-tree-container\" style=\"display: " + ((this.open)?'block':'none') + ";\">";

	var sb = [];
	for (var i = 0; i < this.childNodes.length; i++) {
		sb[i] = this.childNodes[i].toString(i, this.childNodes.length);
	}
	this.rendered = true;
	return str + sb.join("") + "</div>";
};

function copyTreetoString(oItem) {
	var sbOption = [];
	if (oItem.options) {
		for (var i = 0; i < oItem.options.length; i++) {
			sbOption[i] = oItem.options[i].toString(i,oItem.options.length);
		}
	}
	var str = "<div id=\"" + oItem.id + "\" ondblclick=\"ajaxTreeHandler.toggle(this);\" class=\"ajax-tree-item\" style.position = 'absolute';" +
				"onkeydown=\"return ajaxTreeHandler.keydown(this, event)\"  oncontextmenu=\"ajaxTreeHandler.showContextMenu(this,event);return false\">" +
		"<img id=\"" + oItem.id + "-icon\" class=\"ajax-tree-icon\" src=\"" +
			((ajaxTreeHandler.behavior == 'classic' && oItem.open)?oItem.openIcon:oItem.icon)
			+ "\" onclick=\"ajaxTreeHandler.isclick(this);ajaxTreeHandler.select(this);\" onmousedown=\"ajaxTreeHandler.makeDraggable(this);\" />" +
		"<a href=\"" + oItem.action + "\" id=\"" + oItem.id + "-anchor\" onfocus=\"ajaxTreeHandler.focus(this);\" onmouseover=\"ajaxTreeHandler.imouseover(this);\" " +
			"onblur=\"ajaxTreeHandler.blur(this);\"" + (oItem.target ? " target=\"" + oItem.target + "\"" : "") +
		">" + oItem.text + "</a>" + "</div>" +
		"<div id=\"" + oItem.id + "-cont\" class=\"ajax-tree-container\" style=\"display: " + ((oItem.open)?'block':'none') + ";\">";

	var sb = [];
	for (var i = 0; i < oItem.childNodes.length; i++) {
		sb[i] = oItem.childNodes[i].copyItemtoString(i, oItem.childNodes.length);
	}
	oItem.rendered = true;
	return str + sb.join("") + "</div>";
}
/*
 * AjaxTreeItem class
 */

function AjaxTreeItem(sText, sAction, eParent, sIcon, sOpenIcon, sTarget, sOpen, sLoaded, sPersistentId, sFragment) {
	this.base = AjaxTreeAbstractNode;
	this.base(sText, sAction, sTarget, sLoaded, sPersistentId, sFragment);
	this.open = sOpen == 'true' || false;
	if (sIcon) { this.icon = sIcon; }
	if (sOpenIcon) { this.openIcon = sOpenIcon; }
	if (eParent) { eParent.add(this); }
}

AjaxTreeItem.prototype = new AjaxTreeAbstractNode;

AjaxTreeItem.prototype.remove = function() {
	var iconSrc = document.getElementById(this.id + '-plus').src;
	var parentNode = this.parentNode;
	var prevSibling = this.getPreviousSibling(true);
	var nextSibling = this.getNextSibling(true);
	var folder = this.parentNode.folder;
	var last = ((nextSibling) && (nextSibling.parentNode) && (nextSibling.parentNode.id == parentNode.id))?false:true;
	this.getPreviousSibling().focus();
	this._remove();
	if (parentNode.childNodes.length == 0) {
		document.getElementById(parentNode.id + '-cont').style.display = 'none';
		parentNode.doCollapse();
		parentNode.folder = false;
		parentNode.open = false;
	}
	if (!nextSibling || last) { parentNode.indent(null, true, last, this._level, parentNode.childNodes.length); }
	if ((prevSibling == parentNode) && !(parentNode.childNodes.length)) {
		prevSibling.folder = false;
		prevSibling.open = false;
		iconSrc = document.getElementById(prevSibling.id + '-plus').src;
		iconSrc = iconSrc.replace('minus', '').replace('plus', '');
		document.getElementById(prevSibling.id + '-plus').src = iconSrc;
		document.getElementById(prevSibling.id + '-icon').src = ajaxTreeConfig.fileIcon();
	}
	if (document.getElementById(prevSibling.id + '-plus')) {
		if (parentNode == prevSibling.parentNode) {
			iconSrc = iconSrc.replace('minus', '').replace('plus', '');
			document.getElementById(prevSibling.id + '-plus').src = iconSrc;
		}
	}
}

AjaxTreeItem.prototype._remove = function() {
	for (var i = this.childNodes.length - 1; i >= 0; i--) {
		this.childNodes[i]._remove();
 	}
	for (var i = 0; i < this.parentNode.childNodes.length; i++) {
		if (this == this.parentNode.childNodes[i]) {
			for (var j = i; j < this.parentNode.childNodes.length; j++) {
				this.parentNode.childNodes[j] = this.parentNode.childNodes[j+1];
			}
			this.parentNode.childNodes.length -= 1;
			if (i + 1 == this.parentNode.childNodes.length) { this.parentNode._last = true; }
			break;
		}
	}
	ajaxTreeHandler.all[this.id] = null;
	var tmp = document.getElementById(this.id);
	if (tmp) { tmp.parentNode.removeChild(tmp); }
	tmp = document.getElementById(this.id + '-cont');
	if (tmp) { tmp.parentNode.removeChild(tmp); }
}

AjaxTreeItem.prototype.expand = function() {
	this.doExpand();
	document.getElementById(this.id + '-plus').src = this.minusIcon;
}

AjaxTreeItem.prototype.collapse = function(b) {
	if (!b) { this.focus(); }
	this.doCollapse();
	document.getElementById(this.id + '-plus').src = this.plusIcon;
}

AjaxTreeItem.prototype.getFirst = function() {
	return this.childNodes[0];
}

AjaxTreeItem.prototype.getLast = function() {

	if(this.childNodes[this.childNodes.length - 1].open == null){
		return false;
	}
	if (this.childNodes[this.childNodes.length - 1].open) {
		return this.childNodes[this.childNodes.length - 1].getLast(); }
	else {
		return this.childNodes[this.childNodes.length - 1]; }
}

AjaxTreeItem.prototype.getNextSibling = function() {
	for (var i = 0; i < this.parentNode.childNodes.length; i++) {
		if (this == this.parentNode.childNodes[i]) { break; }
	}
	if (++i == this.parentNode.childNodes.length) { return this.parentNode.getNextSibling(); }
	else { return this.parentNode.childNodes[i]; }
}

AjaxTreeItem.prototype.getPreviousSibling = function(b) {
	for (var i = 0; i < this.parentNode.childNodes.length; i++) {
		if (this == this.parentNode.childNodes[i]) { break; }
	}
	if (i == 0) { return this.parentNode; }
	else {
		if ((this.parentNode.childNodes[--i].open) || (b && this.parentNode.childNodes[i].folder)) {
			return this.parentNode.childNodes[i].getLast();
		} else {
			return this.parentNode.childNodes[i];
		}
	}
}

AjaxTreeItem.prototype.keydown = function(key) {
	if ((key == 39) && (this.folder)) {
		if (!this.open) { this.expand(); }
		else { this.getFirst().select(); }
		return false;
	}
	else if (key == 37) {
		if (this.open) { this.collapse(); }
		else { this.parentNode.select(); }
		return false;
	}
	else if (key == 40) {
		if (this.open) { this.getFirst().select(); }
		else {
			var sib = this.getNextSibling();
			if (sib) { sib.select(); }
		}
		return false;
	}
	else if (key == 38) { this.getPreviousSibling().select(); return false; }
	return true;
}

AjaxTreeItem.prototype.copyItemtoString = function (nItem, nItemCount) {
	var foo = this.parentNode;
	var indent = '';
	if (nItem + 1 == nItemCount) { this.parentNode._last = true; }
	var i = 0;
	while (foo.parentNode) {
		foo = foo.parentNode;
		indent = "<img id=\"" + this.id + "-indent-" + i + "\" src=\"" +
				((foo._last)?ajaxTreeConfig.blankIcon():ajaxTreeConfig.iIcon()) + "\" />" + indent;
		i++;
	}
	this._level = i;
	if (this.childNodes.length || !this.loaded ) { this.folder = 1; }
	else { this.open = false; }
	if ((this.folder) || (ajaxTreeHandler.behavior != 'classic')) {
		if (!this.icon) { this.icon = ajaxTreeConfig.folderIcon(); }
		if (!this.openIcon) { this.openIcon = ajaxTreeConfig.openFolderIcon(); }
	}
	else {
		if (!this.icon) { this.icon = ajaxTreeConfig.fileIcon(); }
	}
	var label = this.text.replace(/</g, '&lt;').replace(/>/g, '&gt;');
	var sbOption = [];
	for (var i = 0; i < this.options.length; i++) {
		sbOption[i] = this.options[i].toString(i,this.options.length);
	}
	var treeIcon;
	if (this.folder) {
		if (this.open) {
			if (this.parentNode._last) { treeIcon = ajaxTreeConfig.lMinusIcon(); } else { treeIcon = ajaxTreeConfig.tMinusIcon(); }
		} else {
			if (this.parentNode._last) { treeIcon = ajaxTreeConfig.lPlusIcon(); } else { treeIcon = ajaxTreeConfig.tPlusIcon(); }
		}
	} else {
		if (this.parentNode._last) { treeIcon = ajaxTreeConfig.lIcon(); } else { treeIcon = ajaxTreeConfig.tIcon(); }
	}

	var str = "<div id=\"" + this.id + "\" ondblclick=\"ajaxTreeHandler.toggle(this);\" class=\"ajax-tree-item\" style.position = 'absolute';" +
				"onkeydown=\"return ajaxTreeHandler.keydown(this, event)\"  oncontextmenu=\"ajaxTreeHandler.showContextMenu(this,event);return false;\" >" +
		"<img id=\"" + this.id + "-plus\" src=\"" + treeIcon + "\" onclick=\"ajaxTreeHandler.toggle(this);\" />" +
		"<img id=\"" + this.id + "-icon\" class=\"ajax-tree-icon\" src=\"" +
			((ajaxTreeHandler.behavior == 'classic' && this.open)?this.openIcon:this.icon) +
			"\" onclick=\"ajaxTreeHandler.isclick(this);ajaxTreeHandler.select(this);\" onmousedown=\"ajaxTreeHandler.makeDraggable(this);\" />" +
		"<a href=\"" + this.action + "\" id=\"" + this.id + "-anchor\" onfocus=\"ajaxTreeHandler.focus(this);\" onmouseover=\"ajaxTreeHandler.imouseover(this);\" " +
			"onblur=\"ajaxTreeHandler.blur(this);\"" + (this.target ? " target=\"" + this.target + "\"" : "") +
		">" + label + "</a>" + "</div>" +
		"<div id=\"" + this.id + "-cont\" class=\"ajax-tree-container\" style=\"display: " + ((this.open)?'block':'none') + ";\">";

    var sb = [];
	for (var i = 0; i < this.childNodes.length; i++) {
		sb[i] = this.childNodes[i].toString(i,this.childNodes.length);
	}
	this.plusIcon = ((this.parentNode._last)?ajaxTreeConfig.lPlusIcon():ajaxTreeConfig.tPlusIcon());
	this.minusIcon = ((this.parentNode._last)?ajaxTreeConfig.lMinusIcon():ajaxTreeConfig.tMinusIcon());
	return str + sb.join("") + "</div>";
}

AjaxTreeItem.prototype.toString = function (nItem, nItemCount) {
	var foo = this.parentNode;
	var indent = '';
	if (nItem + 1 == nItemCount) { this.parentNode._last = true; }
	var i = 0;
	while (foo.parentNode) {
		foo = foo.parentNode;
		indent = "<img id=\"" + this.id + "-indent-" + i + "\" src=\"" +
				((foo._last)?ajaxTreeConfig.blankIcon():ajaxTreeConfig.iIcon()) + "\" />" + indent;
		i++;
	}
	this._level = i;
	if (this.childNodes.length || !this.loaded ) { this.folder = 1; }
	else { this.open = false; }
	if ((this.folder) || (ajaxTreeHandler.behavior != 'classic')) {
		if (!this.icon) { this.icon = ajaxTreeConfig.folderIcon(); }
		if (!this.openIcon) { this.openIcon = ajaxTreeConfig.openFolderIcon(); }
	}
	else {
		if (!this.icon) { this.icon = ajaxTreeConfig.fileIcon(); }
	}
	var label = this.text.replace(/</g, '&lt;').replace(/>/g, '&gt;');
	var sbOption = [];
	for (var i = 0; i < this.options.length; i++) {
		sbOption[i] = this.options[i].toString(i,this.options.length);
	}
	var treeIcon;
	if (this.folder) {
		if (this.open) {
			if (this.parentNode._last) { treeIcon = ajaxTreeConfig.lMinusIcon(); } else { treeIcon = ajaxTreeConfig.tMinusIcon(); }
		} else {
			if (this.parentNode._last) { treeIcon = ajaxTreeConfig.lPlusIcon(); } else { treeIcon = ajaxTreeConfig.tPlusIcon(); }
		}
	} else {
		if (this.parentNode._last) { treeIcon = ajaxTreeConfig.lIcon(); } else { treeIcon = ajaxTreeConfig.tIcon(); }
	}

	var str = "<div id=\"" + this.id + "\" ondblclick=\"ajaxTreeHandler.toggle(this);\" class=\"ajax-tree-item\" style.position = 'absolute';" +
				"onkeydown=\"return ajaxTreeHandler.keydown(this, event)\"  oncontextmenu=\"ajaxTreeHandler.showContextMenu(this,event);return false;\">" +
		indent +
		"<img id=\"" + this.id + "-plus\" src=\"" + treeIcon + "\" onclick=\"ajaxTreeHandler.toggle(this);\" />" +
		"<img id=\"" + this.id + "-icon\" class=\"ajax-tree-icon\" src=\"" +
			((ajaxTreeHandler.behavior == 'classic' && this.open)?this.openIcon:this.icon) +
			"\" onclick=\"ajaxTreeHandler.isclick(this);ajaxTreeHandler.select(this);\" onmousedown=\"ajaxTreeHandler.makeDraggable(this);\" />";

	if(hasRights(this.icon)) {
		str += "<a href=\"" + this.action + "\" id=\"" + this.id + "-anchor\" onfocus=\"ajaxTreeHandler.focus(this);\" onmouseover=\"ajaxTreeHandler.imouseover(this); return true;\" " +
			"onblur=\"ajaxTreeHandler.blur(this);\"" + (this.target ? " target=\"" + this.target + "\"" : "") +
			">" + this.text + "</a>"
	}
	else {
		str +=	"<font style='color:#777'>"+this.text+"</font>"
	}

	str += "</div>" +
		"<div id=\"" + this.id + "-cont\" class=\"ajax-tree-container\" style=\"display: " + ((this.open)?'block':'none') + ";\">";
	var sb = [];
	for (var i = 0; i < this.childNodes.length; i++) {
		sb[i] = this.childNodes[i].toString(i,this.childNodes.length);
	}
	this.plusIcon = ((this.parentNode._last)?ajaxTreeConfig.lPlusIcon():ajaxTreeConfig.tPlusIcon());
	this.minusIcon = ((this.parentNode._last)?ajaxTreeConfig.lMinusIcon():ajaxTreeConfig.tMinusIcon());
	return str + sb.join("") + "</div>";
};

/*
 * AjaxTreeOption class
 */

function AjaxTreeOption(sText, sAction, sIcon, sTarget) {
	this.text   = sText || ajaxTreeConfig.defaultText;
	this.action = sAction || ajaxTreeConfig.defaultAction;
	this.icon   = sIcon || ajaxTreeConfig.optionIcon();
	if (sTarget) this.target = sTarget;
}

function methodpaste(actionstr){
	if (actionstr.indexOf('paste') == 0) {
		//alert("paste")
		eval(actionstr);
	}


}
function methodcut(actionstr){
	if (actionstr.indexOf('cut') == 0) {
		//alert("cut")
		eval(actionstr);
	}
}
function methodWrapper(oItemId,actionstr){
	var needToBeMarked = false;
	if (actionstr.indexOf('cut') == 0) {
		methodcut(actionstr);
		needToBeMarked = true;
	}
	if (needToBeMarked) {
    	var oldItemID = readCookie(NAVIGATION, 'copiedItemID', '');
		if (oldItemID!='') {
			ajaxTreeHandler.markSubTree(oldItemID,'ajax-tree-item');
		}
	    writeCookie(NAVIGATION, 'copiedItemID', oItemId);
		ajaxTreeHandler.markSubTree(oItemId,'marked');
	}

}
AjaxTreeOption.prototype.toString = function (oItem) {
	var foo = this.parentNode;
	var actionStr;
	if (this.action.indexOf('javascript:') == 0) {
		var stroptions = this.action.substring('javascript:'.length, this.action.length - 1)
		if(stroptions.indexOf('cut') == 0){
			alldragObject.cutoption = stroptions;
		}
		else if(stroptions.indexOf('paste') == 0){
			alldragObject.pasteoption = stroptions;
		}
		actionStr = "methodWrapper('"+ oItem + "'," + stroptions + ")";
	}
	else {
		actionStr = "window.parent."+this.target+".location = '"+this.action+"'";
	}
	var label = this.text.replace(/</g, '&lt;').replace(/>/g, '&gt;');
	var str =
      "<tr class=item onmouseover=\"this.className='item_hover'\" onmouseout=\"this.className='item'\" onclick=\""+actionStr+"\">"+
      "<td class=icon >"+
      "<img src='" + this.icon +"'/>"+
      "</td>"+
      "<td class=label nowrap>"+
      label+
      "</td>"+
      "</tr>";
	return str;
};

var alldragObject = {
	copyObject : null,
	insertitem : null,
	isover     : null,
	cutoption  : null,
	pasteoption: null,
	mouseCoords: function (ev) {var mousaction = new mouseAction(); return mousaction.mouseCoords(ev)},
	mouseDown  : function (ev,dragObject) {var mousaction = new mouseAction(); mousaction.mouseDown(ev,dragObject)},
	mouseMove  : function (ev) {var mousaction = new mouseAction(); return mousaction.mouseMove;},
	mouseUp    : function (ev) {var mousaction = new mouseAction(); return mousaction.mouseUp;},
	pastenode  : function () {var mousaction = new mouseAction(); mousaction.pastenode()}
}
function mouseAction(){

}

mouseAction.prototype.mouseCoords = function (ev){

	if(ev.pageX || ev.pageY){
		return {x:ev.pageX, y:ev.pageY};
	}
	return {
		x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
		y:ev.clientY + document.body.scrollTop  - document.body.clientTop
	};
}

mouseAction.prototype.mouseDown = function (ev,dragObject){
	ev = ev || window.event;

	if(dragObject != null){

		var itemoption = dragObject.options;
		if (itemoption == null || itemoption.length == 0) {
		  return;
		}
		itemoption.toString(dragObject.id);
		if(alldragObject.isover == true){
			methodcut(alldragObject.cutoption);
			alldragObject.cutoption = null;
		}

	}
}

mouseAction.prototype.mouseMove = function (ev){
	ev = ev || window.event;
	alldragObject.mouseDown(ev,alldragObject.dragObject);
	alldragObject.isover = true;
	var target   = ev.target || ev.srcElement;
	var mousePos = alldragObject.mouseCoords(ev);
	if(alldragObject.copyObject){
		alldragObject.copyObject.style.position = 'absolute';
		alldragObject.copyObject.style.top      = mousePos.y;
		alldragObject.copyObject.style.left     = mousePos.x + 5;
		return false;
	}
}

mouseAction.prototype.mouseUp = function (ev){
	/* Fix for Internet Explorer
	   When dragging a node, mousemove and mouseover events of individual nodes do not occur.
	   Therefore, it is not known on which element the node is dropped.
	*/
	if (!ev) {
		var dropTarget = event.srcElement.id;
		if (dropTarget.indexOf(ajaxTreeHandler.idPrefix) >= 0 && dropTarget.indexOf('-anchor') > 0) {
			alldragObject.insertitem = ajaxTreeHandler.all[dropTarget.replace('-anchor','')];
		}
	}
	
	alldragObject.dragObject = null;
	alldragObject.iMouseDown = false;
	document.onmousemove = null;
	document.onmouseup = null;
	document.onmousedown = null;
	alldragObject.copyObject.style.display = 'none';
	alldragObject.pastenode();
	alldragObject.insertitem = null;
	alldragObject.cutoption = null;
	return true;
}

mouseAction.prototype.pastenode = function (){
	if(alldragObject.insertitem != null){
	var itemoption = alldragObject.insertitem.options;
	if (itemoption == null || itemoption.length == 0) {
	  return;
	}
	itemoption.toString(alldragObject.insertitem.id);
	if(alldragObject.isover == true){
		methodpaste(alldragObject.pasteoption);
		alldragObject.pasteoption = null;
		alldragObject.isover = false;
	}
}
}

function hasRights(icon) {
	var role = ajaxTreeConfig.role;
	if(role == undefined || role == '' || role == 'none' || role == '-') {
		return true;
	}
	var pageRole = icon.substring(icon.lastIndexOf('_')+1, icon.lastIndexOf('.'));
	if(pageRole == role) {
		return true;
	}
	if(pageRole == "webmaster") {
		return true;
	}
	if(pageRole == "chiefeditor" && (role == "editor" || role == "writer")) {
		return true;
	}
	if(pageRole == "editor" && (role == "writer")) {
		return true;
	}
	return false;
}