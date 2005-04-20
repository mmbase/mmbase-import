
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
var uncollapsedNodes = new Array();
var loadedNodeBodies = new Map();

function mmbaseInit() {

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

    var nodeBodyXml = loadedNodeBodies.get(nodeNumber);
    if (nodeBodyXml == null) {
        var request = new XMLHttpRequest();
        request.open('GET', 'node.body.jspx', false);
        request.send('');
        nodeBodyXml = Sarissa.serialize(request.responseXML);
        //nodeBodyXml = request.responseXML.xml;
    }

    kupu.setHTMLBody(nodeBodyXml);
    currentNode = nodeNumber;
    
}

/**
 * Load a part from the 'tree' of nodes. A request is done, and the div with the correct id is filled.
 */
function loadRelated(nodeNumber) {
    var treeXml = loadedTrees.get(nodeNumber);
    if (treeXml == null) {
        var request = new XMLHttpRequest();
        request.open('GET', 'tree.jspx?node=' + nodeNumber, false);    
        request.send(null);
        treeXml = Sarissa.serialize(request.responseXML);
        loadedTrees.add(nodeNumber, treeXml);
    }
    document.getElementById('node_' + nodeNumber + '_related').innerHTML = treeXml;
    document.getElementById('node_' + nodeNumber + '_related').className = 'on';    
    uncollapsedNodes[nodeNumber] = true;
}

/**
 * Unload a part from the 'tree' of nodes. The div with the correct id is made empty.
 */
function unloadRelated(nodeNumber) {
    document.getElementById('node_' + nodeNumber + '_related').innerHTML = '';
    document.getElementById('node_' + nodeNumber + '_related').className = 'off';
    uncollapsedNodes[nodeNumber] = null;
}

function reloadTree() {
    var request = new XMLHttpRequest();
    request.open('GET', 'tree.jspx?parent=true', false);    
    request.send(null);
    document.getElementById('tree').innerHTML = Sarissa.serialize(request.responseXML);
    alert(" " + i + ":" + uncollapsedNodes.length + " " + uncollapsedNodes[i]);
}

/**
 * Load a part from the 'tree' of nodes. A request is done, and the div with the correct id is filled.
 */
function createSubNode(nodeNumber) {
    var request = new XMLHttpRequest();
    request.open('GET', 'create-subnode.jspx?node=' + nodeNumber, false);    
    request.send('');
    alert(Sarissa.serialize(request.responseXML));
    reloadTree();

}

