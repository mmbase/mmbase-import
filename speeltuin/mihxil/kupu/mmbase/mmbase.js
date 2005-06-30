/**
 * The node currently being edited
 */
var currentNode;
var trunkNode;

// any object can be used as map in javascript, but make it look a bit nicer.
function Map() {
    this.add = function(key, value) { 
        var prevValue = this[key];
        this[key] = value;
        return prevValue;
    };
    this.get = function(key) {       
        return this[key];
    };
    this.remove = function(key) {
        this[key] = undefined;
    }
}

/**
 * Caches of loaded nodes (represented in HTML)
 */

var loadedNodes      = new Map();
var loadedTrees      = new Map();
var unloadedTrees    = new Map();
var uncollapsedNodes = new Array();
var loadedNodeBodies = new Map();

function startKupu(language) {
    // first let's load the message catalog
    // if there's no global 'i18n_message_catalog' variable available, don't
    // try to load any translations

    if (window.i18n_message_catalog) {        
        var request = getRequest();
        // sync request, scary...
        request.open('GET', '../common/kupu.pox.jspx?mymessages=../mmbase/mymessages.jspx&language=' + language, false);
        request.send('');
        if (request.status != '200') {
            alert('Error loading translation (status ' + status + '), falling back to english');
        } else {
            // load successful, continue
            var dom = request.responseXML;
            window.i18n_message_catalog.initialize(dom);
        };
    }   
    // initialize the editor, initKupu groks 1 arg, a reference to the iframe
    var frame = getFromSelector('kupu-editor'); 
    var kupu = initKupu(frame);
    
    // this makes the editor's content_changed attribute set according to changes
    // in a textarea or input (registering onchange, see saveOnPart() for more
    // details)
    kupu.registerContentChanger(getFromSelector('kupu-editor-textarea'));
    
    // let's register saveOnPart(), to ask the user if he wants to save when 
    // leaving after editing
    if (kupu.getBrowserName() == 'IE') {
        // IE supports onbeforeunload, so let's use that
        addEventHandler(window, 'beforeunload', saveOnPart);
    } else {
        // some versions of Mozilla support onbeforeunload (starting with 1.7)
        // so let's try to register and if it fails fall back on onunload
        var re = /rv:([0-9\.]+)/
        var match = re.exec(navigator.userAgent)
        if (match[1] && parseFloat(match[1]) > 1.6) {
            addEventHandler(window, 'beforeunload', saveOnPart);
        } else {
            addEventHandler(window, 'unload', saveOnPart);
        };
    };

    // and now we can initialize...
    kupu.initialize();

    return kupu;
};


function mmbaseInit(node) {

    KupuZoomTool.prototype.origcommandfunc  = KupuZoomTool.prototype.commandfunc;
    KupuZoomTool.prototype.commandfunc = function(button, editor) {
        this.origcommandfunc(button, editor);
        if (this.zoomed == true) {
            xGetElementById("leftColumn").style.display = "none";
            //xGetElementById("header").style.display = "none";
        } else {
            xGetElementById("leftColumn").style.display = "block";
            //xGetElementById("header").style.display = "block";
            adjustLayout();
        }
    }

    winOnLoad();    
    trunkNumber = node;
    loadNode(node);

}

function getRequest() {
    return Sarissa.getXmlHttpRequest();
    // new sarissa:
    //return new XMLHttpRequest();
}
    
function serialize(request) {
    //return request.responseXML.xml;
    // new sarissa:
    return Sarissa.serialize(request.responseXML);
}


/**
 * Called by the save button.
 */
function saveNode(button, editor) {
    kupu.logMessage(_("Saving body (kupu)") + " " + currentNode);
    editor.saveDocument(undefined, true); // kupu-part of save
    var content = "";
    var a = xGetElementsByTagName('input', xGetElementById('nodefields'));
    for (i=0; i < a.length; i++) {
        content += a[i].name + ':' + a[i].value + "\n";
    }
    kupu.logMessage(_("Saving fields (form)") + " " + currentNode);
    var boundary = "----------__________----------";
    a = xGetElementsByTagName('textarea', xGetElementById('nodefields'));
    for (i=0; i < a.length; i++) {
        content += a[i].name + ":" + boundary + "\n" + a[i].value + "\n" + boundary + "\n";
    }

    //alert("putting " + content);
    var request = getRequest();
    request.open("PUT", "receive.jspx?fields=true", true);
    request.setRequestHeader("Content-type", "text/plain");
    request.send(content);
    //kupu.handleSaveResponse(request);
    var node = currentNode;
    currentNode = undefined;
    kupu.logMessage("Reloading " + node);
    loadedNodes.remove(node);
    loadedNodeBodies.remove(node);
    loadNode(node);
    alert(_("saved") + " " + node);

}

/**
 * If title is edited, tree must be updated (used in onKeyUp)
 */
function updateTree(nodeNumber, title) {
    var nodeA = document.getElementById('a_' + nodeNumber);
    nodeA.innerHTML = title;
}

/**
 * Load one node from server into the 'node' div. Unless that already happened, in which case this
 * result is taken from cache.
 */
function loadNode(nodeNumber) {
    
    var nodeDiv = document.getElementById('nodefields');

    if (nodeNumber == currentNode) {
        kupu.logMessage(_("RELOAD"));
        loadedNodes.remove(nodeNumber);
        loadedNodeBodies.remove(nodeNumber);
        currentNode = undefined;
    }
    var currentA;

    if (currentNode != undefined) {
        // store corrent values in loaded-values maps.
        loadedNodes.add(currentNode, nodeDiv.innerHTML);
        loadedNodeBodies.add(currentNode, kupu.getHTMLBody());
        currentA = document.getElementById('a_' + currentNode);
        if (currentA != undefined) {
            currentA.className = "";
        }

    }
    var nodeXml = loadedNodes.get(nodeNumber);
    if (nodeXml == null) {
        kupu.logMessage(_("Getting node fields for ") + nodeNumber); 
        var dom = Sarissa.getDomDocument();
        dom.async = false;
        dom.load('node.jspx?node=' + nodeNumber);
        nodeXml = Sarissa.serialize(dom);
        loadedNodes.add(nodeNumber, nodeXml);
    } else {
        kupu.logMessage(_("Loading node fields for ") + nodeNumber); 
        var request = getRequest();
        request.open('GET', 'node.jspx?loadonly=true&node=' + nodeNumber, false);
        request.send('');        
    }
    
    nodeDiv.innerHTML = nodeXml;
    
    var nodeBodyXml = loadedNodeBodies.get(nodeNumber);
    if (nodeBodyXml == null) {
        kupu.logMessage(_("Getting node body ") + " " + nodeNumber);
        var dom = Sarissa.getDomDocument();
        dom.async = false;
        dom.load('node.body.jspx');
        nodeBodyXml = Sarissa.serialize(dom);
        loadedNodeBodies.add(nodeNumber, nodeBodyXml);
    } else {
        kupu.logMessage(_("Loading node body ") + " " + nodeNumber);
    }
    
    /*
    alert("found " + Sarissa.serialize(nodeBodyXml));
    var text = "";
    for (i in kupu.document.getDocument()) text += "  " + i;
    alert("" + text);
    kupu.document.getDocument().clear();
    kupu.document.getDocument().appendChild(nodeBodyXml);
    */
    kupu.setHTMLBody(nodeBodyXml);
    currentNode = nodeNumber;
    currentA = document.getElementById('a_' + currentNode);
    currentA.className = "current";
    adjustLayout();
    
}

/**
 * Load a part from the 'tree' of nodes. A request is done, and the div with the correct id is filled.
 */
function loadRelated(nodeNumber) {
    var treeXml = loadedTrees.get(nodeNumber);
    if (treeXml == null) {
        var request = getRequest();
        request.open('GET', 'tree.jspx?node=' + nodeNumber, false);    
        request.send('');
        treeXml = serialize(request);
        loadedTrees.add(nodeNumber, treeXml);
    }
    var related = document.getElementById('node_' + nodeNumber);
    unloadedTrees.add(nodeNumber, related.innerHTML);

    related.innerHTML = treeXml;
    uncollapsedNodes['node' + nodeNumber] = nodeNumber;
}

/**
 * Unload a part from the 'tree' of nodes. The div with the correct id is made empty.
 */
function unloadRelated(nodeNumber) {
    var related = document.getElementById('node_' + nodeNumber);    
    var html = unloadedTrees.get(nodeNumber);   
    if (html == null) {
        // just fall-back
        var request = getRequest();
        request.open('GET', 'tree.jspx?node=' + nodeNumber, false);    
        request.send('');
        html = serialize(request);
    }
    related.innerHTML = html;
    uncollapsedNodes['node' + nodeNumber] = null;
}

function reloadTree() {
    var request = getRequest();
    request.open('GET', 'tree.jspx?node=' + trunkNumber, false);    
    request.send('');
    var tree = serialize(request);
    document.getElementById('tree').innerHTML = tree;    
    for (var i in uncollapsedNodes) {
        if (i.indexOf("node") == 0) {
            loadRelated(uncollapsedNodes[i]);
        }
    }
    // trick to make current node active again
    var node = currentNode;
    currentNode = undefined; // otherwise it will be relaoded from the server
    
    loadNode(node);

}

/**
 * Load a part from the 'tree' of nodes. A request is done, and the div with the correct id is filled.
 */
function createSubNode(nodeNumber) {
    var request = getRequest();
    request.open('GET', 'create-subnode.jspx?node=' + nodeNumber, false);    
    request.send('');
    var result = serialize(request);
    alert(result);
    loadedTrees = new Map();
    reloadTree();

}

