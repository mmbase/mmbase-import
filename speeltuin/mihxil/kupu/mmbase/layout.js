

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
function adjustLayout() {
    var leftColumnWidth = 150;
    var maxHeight = xClientHeight() - 20;
    var maxWidth  = xClientWidth() - leftColumnWidth;
  
    // Assign maximum height to all columns
    xHeight('leftColumn', maxHeight);
    xHeight('centerColumn', maxHeight);
    xWidth('centerColumn', maxWidth);

    var a = xGetElementsByTagName('input', xGetElementById('leftColumn'));
    for (i=0; i < a.length; i++) {
        xWidth(a[i], leftColumnWidth);
    }
    a = xGetElementsByTagName('textarea', xGetElementById('leftColumn'));
    for (i=0; i < a.length; i++) {
        xWidth(a[i], leftColumnWidth);
    }
    a = xGetElementsByClassName('kupu-editorframe');
    for (i=0; i < a.length; i++) {
        xHeight(a[i], maxHeight - 40);
    }
    xHeight("toolboxes", maxHeight);
    xHeight("kupu-editor", maxHeight - 40);

    var nodeHeight = xHeight('node');

    xHeight("tree", maxHeight - nodeHeight - 20);

    
}

