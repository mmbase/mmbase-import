

if (document.getElementById || document.all) { // minimum dhtml support required
  document.write("<"+"style type='text/css'>#footer{visibility:hidden;}<"+"/style>");
  window.onload = winOnLoad;
}
function winOnLoad()
{
  var ele = xGetElementById('leftColumn');
  if (ele && xDef(ele.style, ele.offsetHeight)) { // another compatibility check
    adjustLayout();
    xAddEventListener(window, 'resize', winOnResize, false);
  }
}
function winOnResize() {
  adjustLayout();
}

function adjustToolBoxesLayout() {
    var toolbox = 40;
    var spacing = 5;
    xTop("kupu-toolbox-links", toolbox);
    toolbox += xHeight("kupu-toolbox-links") + spacing;
    xTop("kupu-toolbox-images", toolbox);
    toolbox += xHeight("kupu-toolbox-images") + spacing;
    xTop("kupu-toolbox-tables", toolbox);
    toolbox += xHeight("kupu-toolbox-tables") + spacing;
    xTop("kupu-toolbox-divs", toolbox);
    toolbox += xHeight("kupu-toolbox-divs") + spacing;
    xTop("kupu-toolbox-debug", toolbox);

}
function adjustLayout() {
    
    var leftColumnWidth = 150;
    var maxHeight = xClientHeight() - 20;
    var maxWidth  = xClientWidth() - leftColumnWidth - 4;
  
    // Assign maximum height to all columns
    xHeight('leftColumn', maxHeight - 3);
    xHeight('centerColumn', maxHeight);
    xWidth('centerColumn', maxWidth);

    var a = xGetElementsByTagName('input', xGetElementById('leftColumn'));
    for (i=0; i < a.length; i++) {
        xWidth(a[i], leftColumnWidth - 6);
    }

    a = xGetElementsByTagName('textarea', xGetElementById('leftColumn'));
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
    xWidth("kupu-editor", maxWidth - 210);

    adjustToolBoxesLayout();

    var nodeHeight = xHeight('nodefields');

    xHeight("tree", maxHeight - nodeHeight - 1);

    
}

