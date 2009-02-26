var debugAlpha = false;

 function alphaImages(){
 
   var rslt = navigator.appVersion.match(/MSIE (\d+\.\d+)/, '');
   var itsAllGood = (rslt != null && Number(rslt[1]) >= 5.5 && Number(rslt[1]) < 7.0);

   if (itsAllGood) {
     if(debugAlpha) alert("doing alpha!");
   
     var replacedBackgroundImages = 0;
     var replacedImages = 0;
     for (i=0; i<document.all.length; i++){
       var el = document.all[i];
       var bg = el.currentStyle.backgroundImage;
       if(bg){
         if (bg.match(/\.png/i) != null){
           var mypng = bg.substring(5,bg.length-2);
           el.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+mypng+"', sizingMethod='crop')";
           el.style.backgroundImage = "URL('"+mypng.substring(0,mypng.lastIndexOf("/")+1)+"empty.gif')";
           replacedBackgroundImages++;
         }
       }
       var mypng = el.src;
       if(mypng && mypng.indexOf(".png") != -1 && mypng.indexOf("/icons/") != -1) {
         el.src = mypng.substring(0,mypng.lastIndexOf("/")+1)+"empty.gif";
         el.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+mypng+"', sizingMethod='crop')";
         replacedImages++;
       }
     }
   }
   if(debugAlpha) alert("done alpha (bg:"+replacedBackgroundImages+") (i:"+replacedImages+")");
}


window.onload = alphaImages;

ajaxUrl = null;
load=null;
time=null;
function workfor(url, a){			
	ajaxUrl = url + "number=" + a;
	getFreezeInfo();
	time=setTimeout("clear()",60000*5);
}
function clear(){
	clearInterval(load);			
	document.getElementById("needajax").style.display="none";
	document.getElementById("working").style.display="";
}

function getFreezeInfo(){
   load=setInterval("loadInfo()", 10000);
  
}

function loadInfo(){          
	var req = loadXMLDoc(ajaxUrl, false);
	var result = req.status;
	if (result == 200) {
		var areaInfo = req.responseText;
		if (areaInfo == "0") {
			location.reload();
		}
	}
	req = null;
}
        
		
function loadXMLDoc(url, async) {
	var req = false;
	// branch for native XMLHttpRequest object
	if(window.XMLHttpRequest && !(window.ActiveXObject)) {
		try {
			req = new XMLHttpRequest();
		} catch(e) {
			req = false;
		}
	// branch for IE/Windows ActiveX version
	} else if(window.ActiveXObject) {
		try {
			req = new ActiveXObject("Msxml2.XMLHTTP");
		} catch(e) {
			try {
				req = new ActiveXObject("Microsoft.XMLHTTP");
			} catch(e) {
				req = false;
			}
		}
	}
	if(req) {
		var as;
	if (async == undefined) {
		as = true;
	} else {
			as = async;
		}		
		req.open("GET", url, as);
		req.send("");
	}
	return req;
}