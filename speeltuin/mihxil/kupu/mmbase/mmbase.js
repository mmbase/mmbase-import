
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


/**
 * Load one node from server into the 'node' div. Unless that already happened, in which case this
 * result is taken from cache.
 */
function loadNode(nodeNumber) {
    var nodeDiv = document.getElementById('node');
    if (currentNode != undefined) {
        // store corrent values in loaded-values maps.
        loadedNodes.add(currentNode, nodeDiv.innerHTML);
        loadedNodeBodies.add(currentNode, kupu.getHTMLBody());
    }
    var nodeXml = loadedNodes.get(nodeNumber);
    if (nodeXml == null) {					
        var request = new XMLHttpRequest();
        request.open('GET', 'node.jspx?node=' + nodeNumber, false);
        request.send('');
        nodeXml = Sarissa.serialize(request.responseXML);
        //nodeXml = request.responseXML.xml;
    } else {
        var request = new XMLHttpRequest();
        request.open('GET', 'node.jspx?loadonly=true&node=' + nodeNumber, false);
        request.send('');        
    }
        
    nodeDiv.innerHTML = nodeXml;
    //alert("" + nodeDiv.title);
    //document.getElementById("header").innerHTML = nodeDiv.title;

    var nodeBodyXml = loadedNodeBodies.get(nodeNumber);
    if (nodeBodyXml == null) {
        var request = new XMLHttpRequest();
        request.open('GET', 'node.body.jspx', false);
        request.send('');
        //alert("Getting node.body.jspx");        
        nodeBodyXml = Sarissa.serialize(request.responseXML);
        //nodeBodyXml = request.responseXML.xml;
    }
    //alert("received" + nodeBodyXml);
    
    kupu.setHTMLBody(nodeBodyXml);    
    currentNode = nodeNumber;
    adjustLayout();
    
}

/**
 * Load a part from the 'tree' of nodes. A request is done, and the div with the correct id is filled.
 */
function loadRelated(nodeNumber) {
    var treeXml = loadedTrees.get(nodeNumber);
    if (treeXml == null) {
        var request = new XMLHttpRequest();
        request.open('GET', 'tree.jspx?node=' + nodeNumber, false);    
        request.send('');
        treeXml = Sarissa.serialize(request.responseXML);
        //treeXml = request.responseXML.xml;
        loadedTrees.add(nodeNumber, treeXml);
    }
    var related = document.getElementById('node_' + nodeNumber);
    unloadedTrees.add(nodeNumber, related.innerHTML);
    related.innerHTML = treeXml;
    related.className = 'tree on';    
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
        var request = new XMLHttpRequest();
        request.open('GET', 'tree.jspx?node=' + nodeNumber, false);    
        request.send('');
        html = Sarissa.serialize(request.responseXML);
        //html = request.responseXML.xml;
    }
    related.innerHTML = html;
    related.className = 'tree off';
    uncollapsedNodes['node' + nodeNumber] = null;
}

function reloadTree() {
    var request = new XMLHttpRequest();
    request.open('GET', 'tree.jspx?parent=true', false);    
    request.send('');
    var tree =  Sarissa.serialize(request.responseXML);
    //var tree = request.responseXML.xml;
    document.getElementById('tree').innerHTML = tree;
    // alert(" " + uncollapsedNodes.length + " " + uncollapsedNodes);
}

/**
 * Load a part from the 'tree' of nodes. A request is done, and the div with the correct id is filled.
 */
function createSubNode(nodeNumber) {
    var request = new XMLHttpRequest();
    request.open('GET', 'create-subnode.jspx?node=' + nodeNumber, false);    
    request.send('');
    var result = Sarissa.serialize(request.responseXML);
    //var result = request.responseXML.xml;
    alert(result);          
    reloadTree();

}

