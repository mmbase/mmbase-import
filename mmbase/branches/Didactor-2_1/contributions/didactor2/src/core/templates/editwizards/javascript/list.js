// Mozilla does not like this.
var hist = window.history;
if (hist) {
    hist.go(1);
}

var cancelClick = false;

function objMouseOver(el) {
   el.className="itemrow-hover";
}

function objMouseOut(el) {
   el.className="itemrow";
}

function objClick(el) {
   if (cancelClick) {
      cancelClick=false;
      return;
   }
   var href = el.getAttribute("href")+"";
   if (href.length<10) return;
   document.location=href;
}

function doDelete(prompt) {
   var conf;
   if (prompt && prompt!="") {
      conf = confirm(prompt);
   }
   else
      conf=true;
   cancelClick=true;
   return conf;
}