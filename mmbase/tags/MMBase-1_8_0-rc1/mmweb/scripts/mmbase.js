/*
general javascripts for www.mmbase.org

*/
function launchCenter(url, name, height, width) {
  var str = "height=" + height + ",innerHeight=" + height;
  str += ",width=" + width + ",innerWidth=" + width;
  if (window.screen) {
    var ah = screen.availHeight - 30;
    var aw = screen.availWidth - 10;

    var xc = (aw - width) / 2;
    var yc = (ah - height) / 2;

    str += ",left=" + xc + ",screenX=" + xc;
    str += ",top=" + yc + ",screenY=" + yc + ",scrollbars";
  }
  return window.open(url, name, str);
}

/*
  Script to calculate the height of the doc displayed inside the iframe
*/
function calcIframeHeight() {
  try {
    document.getElementById('miframe').height=10;
    var h=document.getElementById('miframe').contentWindow.document.body.scrollHeight;
    document.getElementById('miframe').height=h+10;
  } catch (exception) {		// Firefox does not permit cross domain checking
    // alert("Exception: " + exception);
    document.getElementById('miframe').height=560;
  }
}