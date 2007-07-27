var newwin = null;
function launchCenter(url, name, height, width, features) {
  var str = "height=" + height + ",innerHeight=" + height;
  str += ",width=" + width + ",innerWidth=" + width;
  if (window.screen) {
    var ah = screen.availHeight - 30;
    var aw = screen.availWidth - 10;

    var xc = (aw - width) / 2;
    var yc = (ah - height) / 2;

    str += ",left=" + xc + ",screenX=" + xc;
    str += ",top=" + yc + ",screenY=" + yc;
    str += ",scrollbars=1";    
    str += "," + features;
  }
  newwin = window.open(url, name, str);
}
function OpenWindow(theURL,winName,features) { 
  window.open(theURL,winName,features);
}