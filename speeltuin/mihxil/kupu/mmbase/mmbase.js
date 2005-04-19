
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
    if (nodeXml == undefined) {						  
        var request = Sarissa.getXmlHttpRequest();
        request.open('GET', 'node.jspx?node=' + nodeNumber, false);
        request.send('');
        nodeXml = request.responseXML.xml;
    }
        
    nodeDiv.innerHTML = nodeXml;

    var nodeBodyXml = loadedNodeBodies.get(nodeNumber);
    if (nodeBodyXml == null) {
        var request = Sarissa.getXmlHttpRequest();
        request.open('GET', 'node.body.jspx?node=' + nodeNumber, false);
        request.send('');
        nodeBodyXml = request.responseXML.xml;
    }

    alert("bla1 " + nodeBodyXml);
    kupu.setHTMLBody(nodeBodyXml);
    alert("bla2");
    currentNode = nodeNumber;
    
}

/**
 * Load a part from the 'tree' of nodes. A request is done, and the div with the correct id is filled.
 */
function loadRelated(nodeNumber) {
    var request = Sarissa.getXmlHttpRequest();
    request.open('GET', 'tree.jspx?node=' + nodeNumber, false);    
    request.send(null);
    document.getElementById('node_' + nodeNumber + '_related').innerHTML = request.responseXML.xml;
    document.getElementById('node_' + nodeNumber + '_li').className = 'on';
    
}

/**
 * Unload a part from the 'tree' of nodes. The div with the correct id is made empty.
 */
function unloadRelated(nodeNumber) {
    var request = Sarissa.getXmlHttpRequest();
    document.getElementById('node_' + nodeNumber + '_related').innerHTML = '';
    document.getElementById('node_' + nodeNumber + '_li').className = 'off';
    
}
