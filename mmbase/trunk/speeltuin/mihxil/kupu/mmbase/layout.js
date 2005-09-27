

if (document.getElementById || document.all) { // minimum dhtml support required
  document.write("<"+"style type='text/css'>#footer{visibility:hidden;}<"+"/style>");
  window.onload = winOnLoad;
}
function winOnLoad() {
  var ele = document.getElementById('leftColumn');
  if (ele && xDef(ele.style, ele.offsetHeight)) { // another compatibility check
      //adjustLayout();
    addEventHandler(window, 'resize', winOnResize, window);
  }
}
function winOnResize() {
  adjustLayout();
}

function rePosition(id) {
    // This seems to be only necessary in Mozilla.
    var el = document.getElementById(id);
    el.style.position = "absolute";
    el.style.left = (xClientWidth() - 202) + "px";
}

function adjustToolBoxesLayout() {
    var toolbox = 40;
    var spacing = 5;
    var toolboxRight = 2;
    xTop("kupu-toolbox-links", toolbox);
    rePosition("kupu-toolbox-links");
    toolbox += xHeight("kupu-toolbox-links") + spacing;
    xTop("kupu-toolbox-images", toolbox);
    rePosition("kupu-toolbox-images");
    toolbox += xHeight("kupu-toolbox-images") + spacing;
    xTop("kupu-toolbox-tables", toolbox);
    rePosition("kupu-toolbox-tables");
    toolbox += xHeight("kupu-toolbox-tables") + spacing;
    xTop("kupu-toolbox-divs", toolbox);
    rePosition("kupu-toolbox-divs");
    toolbox += xHeight("kupu-toolbox-divs") + spacing;
    xTop("kupu-toolbox-debug", toolbox);
    rePosition("kupu-toolbox-debug");

}
function adjustLayout() {
    var zoomTool = kupu.getTool("zoomtool");
    if (zoomTool && zoomTool.zoomed) return;

    var leftColumnWidth = 250;
    var maxHeight = xClientHeight() - 20;
    var maxWidth  = xClientWidth() - leftColumnWidth - 4;
  
    // Assign maximum height to all columns
    xHeight('leftColumn', maxHeight - 3);
    xHeight('centerColumn', maxHeight);
    xWidth('centerColumn', maxWidth);

    var a = document.getElementById('leftColumn').getElementsByTagName('input');    
    for (i = 0; i < a.length; i++) {
        if (a[i].className == 'small') { // to avoid date-time entries
            xWidth(a[i], leftColumnWidth - 6);
        }
    }

    a = document.getElementById('leftColumn').getElementsByTagName('textarea');
    for (i=0; i < a.length; i++) {
        xWidth(a[i], leftColumnWidth - 6);
    }

    var maxHeightArea = maxHeight - 27;

    a = xGetElementsByClassName('kupu-editorframe');
    for (i=0; i < a.length; i++) {
        xHeight(a[i], maxHeightArea);
        xWidth(a[i], maxWidth);
        
    }

    xHeight("toolboxes", maxHeight);
    xHeight("kupu-editor", maxHeightArea - 3);
    xWidth("kupu-editor", maxWidth - 201);

    adjustToolBoxesLayout();

    var nodeHeight = xHeight('nodefields');

    xHeight("mmbase-tools", maxHeight - nodeHeight - 1);

    
}

