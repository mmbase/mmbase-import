/**
 * The node currently being edited
 */
var currentNode;
var trunkNode;
var absoluteUrl;

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


function mmbaseInit(node, abs) {

    KupuZoomTool.prototype.origcommandfunc  = KupuZoomTool.prototype.commandfunc;
    KupuZoomTool.prototype.commandfunc = function(button, editor) {
        this.origcommandfunc(button, editor);
        if (this.zoomed == true) {
            document.getElementById("leftColumn").style.display = "none";
            //document.getElementById("header").style.display = "none";
        } else {
            document.getElementById("leftColumn").style.display = "block";
            adjustLayout();
        }
    }

    winOnLoad();
    trunkNumber = node;
    loadNode(node);

    absoluteUrl = abs;

}

function getRequest() {
    return new XMLHttpRequest();
}

function serialize(request) {
    //return request.responseXML.xml;
    // new sarissa:
    return Sarissa.serialize(request.responseXML);
}

var boundary = "----------mmbase-kupu-node-fields----1234567890";
var newline  = "\x0D\x0A";

function addMultiPart(content, a) {
    content += "--" + boundary + newline;
    content += "Content-Disposition: form-data; name=\"" + a.name + "\"" + newline + newline;
    content += a.value + newline;
    return content;
}

/**
 * Called by the save button.
 */
function saveNode(button, editor) {
    // hmm, i think editor == kupu
    kupu.logMessage(_("Saving body (kupu)") + " " + currentNode);
    editor.saveDocument(undefined, false); // kupu-part of save
    var content = "";
    kupu.logMessage(_("Saving fields (form)") + " " + currentNode);

    
    var a = document.getElementsByTagName('input', document.getElementById('nodefields'));
    for (i=0; i < a.length; i++) {
        content = addMultiPart(content, a[i]);
    }
    a = document.getElementsByTagName('select', document.getElementById('nodefields'));
    for (i=0; i < a.length; i++) {
        content = addMultiPart(content, a[i]);
    }

    a = document.getElementsByTagName('textarea', document.getElementById('nodefields'));
    for (i=0; i < a.length; i++) {
        content = addMultiPart(content, a[i]);
    }
    content += "--" + boundary + "\x2D\x2D";

    var request = getRequest();

    request.open("POST", "receive.jspx?fields=true", true);
    request.setRequestHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    request.send(content);
    request.onreadystatechange = function() {
        if (request.readyState == 4) {
            kupu.handleSaveResponse(request);
            var node = currentNode;
            currentNode = undefined;
            //alert("Posted \n" + content);
            alert(_("saved") + " " + node);
            kupu.logMessage("Reloading " + node);
            loadedNodes.remove(node);
            loadedNodeBodies.remove(node);
            loadNode(node);
        }
    }

}


/**
 * If title is edited, tree must be updated (used in onKeyUp)
 */
function updateTree(nodeNumber, title) {
    var nodeA = document.getElementById('a_' + nodeNumber);
    if (nodeA != null) {
        nodeA.innerHTML = title;
    }}

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
        dom.load('node.jspx?objectnumber=' + nodeNumber);
        nodeXml = Sarissa.serialize(dom);
        //alert("received " + nodeXml);
        loadedNodes.add(nodeNumber, nodeXml);
    } else {
        kupu.logMessage(_("Loading node fields for ") + nodeNumber);
        var request = getRequest();
        request.open('GET', 'node.jspx?loadonly=true&objectnumber=' + nodeNumber, false);
        request.send('');
    }

    // request to node.jspx, should have put the node in the session

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

    kupu.setHTMLBody(nodeBodyXml);
    currentNode = nodeNumber;
    currentA = document.getElementById('a_' + currentNode);
    if (currentA != undefined) currentA.className = "current";
    adjustLayout();

}

/**
 * ================================================================================
 * TREE tool
 * ================================================================================
 */

/**
 * Load a part from the 'tree' of nodes. A request is done, and the div with the correct id is filled.
 */
function loadRelated(nodeNumber) {
    var treeXml = loadedTrees.get(nodeNumber);
    if (treeXml == null) {
        var request = getRequest();
        request.open('GET', 'tools/tree.jspx?objectnumber=' + nodeNumber, false);
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
        request.open('GET', 'tools/tree.jspx?objectnumber=' + nodeNumber, false);
        request.send('');
        html = serialize(request);
    }
    related.innerHTML = html;
    uncollapsedNodes['node' + nodeNumber] = null;
}

function reloadTree() {
    var request = getRequest();
    request.open('GET', 'tools/tree.jspx?objectnumber=' + trunkNumber, false);
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
    request.open('GET', 'tools/create-subnode.jspx?objectnumber=' + nodeNumber, false);
    request.send('');
    var result = serialize(request);
    alert(result);
    loadedTrees = new Map();
    reloadTree();
}

/**
 * ================================================================================
 * Related tool
 * ================================================================================
 */


function deleteRelation(type, nodeNumber, relationNode) {
    var relatedRequest = getRequest();
    relatedRequest.open('GET', absoluteUrl + 'tools/related-type.jspx?objectnumber=' + nodeNumber + "&delete_relation=" + relationNode + "&type=" + type, false);
    relatedRequest.send('');
    var result = serialize(relatedRequest);
    document.getElementById("related_" + type).innerHTML = result;


}

function createRelation(type, nodeNumber, relatedNode) {
    var relatedRequest = getRequest();
    relatedRequest.open('GET',  absoluteUrl + 'tools/related-type.jspx?objectnumber=' + nodeNumber + "&relate_node=" + relatedNode + "&type=" + type, false);
    relatedRequest.send('');
    var result = serialize(relatedRequest);
    document.getElementById("related_" + type).innerHTML = result;
}




// ================================================================================

/**
 * our own version to also save the other fields 
 */
function saveOnPart() {
    /* ask the user if (s)he wants to save the document before leaving */
    if (kupu.content_changed && 
        confirm(_('You have unsaved changes. Do you want to save before leaving the page?'))) {
        kupu.config.reload_src = 0;
        saveNode(null, kupu);
    };
};
