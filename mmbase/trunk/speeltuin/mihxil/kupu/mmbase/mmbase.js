
/**
 * The node currently being edited
 */
var currentNode;

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
}

/**
 * Caches of loaded nodes (represented in HTML)
 */

var loadedNodes      = new Map();
var loadedTrees      = new Map();
var unloadedTrees      = new Map();
var uncollapsedNodes = new Array();
var loadedNodeBodies = new Map();

function mmbaseInit() {
    winOnLoad();
}

function getRequest() {
    return Sarissa.getXmlHttpRequest();
    // new sarissa:
    //return new XMLHttpRequest();
}
    
function serialize(request) {
    return request.responseXML.xml;
    // new sarissa:
    //return Sarissa.serialize(request.responseXML);
}

function saveNode(button, editor) {
    editor.saveDocument();
    var content = "";
    var a = xGetElementsByTagName('input', xGetElementById('node'));
    for (i=0; i < a.length; i++) {
        content += a[i].name + ':' + a[i].value + "\n";
    }

    var boundary = "----------__________----------";
    a = xGetElementsByTagName('textarea', xGetElementById('node'));
    for (i=0; i < a.length; i++) {
        content += a[i].name + ":" + boundary + "\n" + a[i].value + "\n" + boundary + "\n";
    }
    kupu.logMessage(_('Sending request part 2 to server'));
    var request = getRequest();
    request.open("PUT", "receive.jspx?fields=true", false);
    request.setRequestHeader("Content-type", "text/plain");
    request.send(content);
    kupu.handleSaveResponse(request);

}

function updateTree(nodeNumber, title) {
    var nodeA = document.getElementById('a_' + nodeNumber);
    nodeA.innerHTML = title;
    var nodeXML = document.getElementById('node').innerHTML;
    //alert ("node is now" + nodeXML);
    loadedNodes.add(nodeNumber, nodeXML);
}

/**
 * Load one node from server into the 'node' div. Unless that already happened, in which case this
 * result is taken from cache.
 */
function loadNode(nodeNumber) {    
    var currentA;
    var nodeDiv = document.getElementById('node');
    if (currentNode != undefined) {
        // store corrent values in loaded-values maps.
        loadedNodes.add(currentNode, nodeDiv.innerHTML);
        loadedNodeBodies.add(currentNode, kupu.getHTMLBody());
        currentA = document.getElementById('a_' + currentNode);
        currentA.className = "";

     }
     var nodeXml = loadedNodes.get(nodeNumber);
     if (nodeXml == null) {					
         var request = getRequest();
         request.open('GET', 'node.jspx?node=' + nodeNumber, false);
         request.send('');
         nodeXml = serialize(request);
    } else {
        var request = getRequest();
        request.open('GET', 'node.jspx?loadonly=true&node=' + nodeNumber, false);
        request.send('');        
    }
        
    nodeDiv.innerHTML = nodeXml;
    //alert("" + nodeDiv.title);
    //document.getElementById("header").innerHTML = nodeDiv.title;

    var nodeBodyXml = loadedNodeBodies.get(nodeNumber);
    if (nodeBodyXml == null) {
        var request = getRequest();
        request.open('GET', 'node.body.jspx', false);
        request.send('');
        //alert("Getting node.body.jspx");        
        nodeBodyXml = serialize(request);
    }
    //alert("received" + nodeBodyXml);
    
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
    uncollapsedNodes['node' + nodeNumber] = true;
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
    request.open('GET', 'tree.jspx?parent=true', false);    
    request.send('');
    var tree = serialize(request);
    document.getElementById('tree').innerHTML = tree;
    // alert(" " + uncollapsedNodes.length + " " + uncollapsedNodes);
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
    reloadTree();

}

