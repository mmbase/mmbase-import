<!-- Hide script from old browsers
var newwin;
function launchCenter(url, name, height, width, params) {
  var str = "height=" + height + ",innerHeight=" + height;
  str += ",width=" + width + ",innerWidth=" + width;
  if (window.screen) {
    var ah = screen.availHeight - 30;
    var aw = screen.availWidth - 10;

    var xc = (aw - width) / 2;
    var yc = (ah - height) / 2;

    str += ",left=" + xc + ",screenX=" + xc;
    str += ",top=" + yc + ",screenY=" + yc;
    if(params!='') str += params;
  }
  newwin = window.open(url, name, str);
}
// Stop hiding script from older browsers -->