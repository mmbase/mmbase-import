// -*- mode: java; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
    <mm:content type="text/javascript" >
var open = new Array();

<mm:import externid="mode">educations</mm:import>

function restoreTree() {
    for (var i in open) {
        if (open[i]) {
            openNode(i);
        }
    }
}

function storeTree() {
    var xmlhttp =  new XMLHttpRequest();
    var ser = "";
    for (var i in open) {
        if (open[i]) {
            ser = ser + "," + i;
        }
    }
    <mm:link page="/education/js/storetree.jsp" referids="mode">
        xmlhttp.open('GET', '${_}&tree=' + ser, true);
    </mm:link>
    xmlhttp.send(null);
}

function openNode(node) {
    el = document.getElementById(node);
    img = document.getElementById('img_' + node);
    img2 = document.getElementById('img2_' + node);
    if (el != null && img != null) {
        open[node] = true;
        el.style.display='inline';
        if (img2 != null) img2.src = 'gfx/folder_open.gif';
        if (img.src.indexOf('last.gif') != -1 ) {
            img.src='gfx/tree_minlast.gif';
        } else {
            img.src='gfx/tree_min.gif';
        }
    }
}

function closeNode(node) {
    el = document.getElementById(node);
    img = document.getElementById('img_' + node);
    img2 = document.getElementById('img2_' + node);
    if (el != null && img != null) {
        open[node] = false;
        el.style.display='none';
        if (img2 != null) img2.src = 'gfx/folder_closed.gif';
        if (img.src.indexOf('last.gif') != -1) {
            img.src='gfx/tree_pluslast.gif';
        } else {
            img.src='gfx/tree_plus.gif';
        }
    }
}
function clickNode(node) {
    el = document.getElementById(node);
    if (el != null) {
        if (el.style.display=='none') {
            openNode(node);
        } else {
            closeNode(node);
        }
    }
}

function loadTree() {
<mm:import externid="tree_${mode}" from="session" />
<mm:present referid="tree_${mode}">
    <mm:stringlist referid="tree_${mode}">
    <mm:isnotempty>
    openNode('${_}');
    </mm:isnotempty>
    </mm:stringlist>
</mm:present>
}

</mm:content>
