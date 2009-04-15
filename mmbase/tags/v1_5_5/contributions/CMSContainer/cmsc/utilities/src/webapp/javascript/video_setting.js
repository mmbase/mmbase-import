
/* Video clip setting */

function fWriteWMV(id, w, h, url, controls){
	var wmp7;try{if(window.ActiveXObject){wmp7 = new ActiveXObject("WMPlayer.OCX.7");}else if(window.GeckoActiveXObject){wmp7 = new GeckoActiveXObject("WMPlayer.OCX.7");}} catch(oError){}
	var classID = "CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95";
	var txt = "";
	txt += '<object classid="'+classID+'" id="'+id+'" width="'+w+'" height="'+h+'" type="application/x-oleobject"';
	txt += '<param name="name" value="'+id+'" />';
	txt += '<param name="filename" value="'+url+'" />';
	txt += '<param name="autoStart" value="true" />';
	txt += '<param name="enabled" value="true" />';
	txt += '<param name="uiMode" value="full" />';
	txt += '<param name="mute" value="true" />';
	txt += '<param name="volume" value="0" />';
	txt += '<param name="Mute" value="0" />';
	txt += '<param name="ShowAudioControls" value="false" />';
	txt += '<param name="ShowStatusBar" value="true" />';
	txt += '<param name="ShowDisplay" value="false" />';
	txt += '<param name="SAMIFileName" value="/videos/homepage.smi" />';
	txt += '<param name="CaptioningID" value="subtitle" />';
	
	if(!wmp7){ 
		txt += '<embed type="application/x-mplayer2" pluginspage="http://microsoft.com/windows/mediaplayer/en/download/" ';
		txt += 'id="'+id+'embed" name="'+id+'" ';
		txt += 'src="'+url+'" filename="'+url+'" ';
		
		if ( controls )
		{
			txt += 'width="'+w+'" height="'+h+'" autoStart="1" showstatusbar="1" showControls="1" showdisplay="0" showaudiocontrols="0" ></embed>';
		} else 
		{
			txt += 'width="'+w+'" height="'+h+'" autoStart="1" showstatusbar="1" showControls="1" showdisplay="0" showaudiocontrols="0" ></embed>';
		}

	}
	txt += '</object>';
	document.write(txt);
}

function fullScreen()
{
	var player=document.getElementById("solvideo");
	if (solvideo.DisplaySize==3)
	{
		solvideo.fullScreen=true;
	}
}


/* Browsers stuff - high contrast */

if(vHighContrast) fAddEvent(window, "load", fAddSeparator);
function fAddSeparator(){
	fLiAddSeparator("iNavL1M2", "&nbsp;|&nbsp;");
	fLiAddSeparator("iNavL1M3", "&nbsp;|&nbsp;");
	fLiAddSeparator("iNavL1M4", "&nbsp;|&nbsp;");
	fLiAddSeparator("iNavL1M5", "&nbsp;|&nbsp;");
	fLiAddSeparator("iNavL1M6", "&nbsp;|&nbsp;");
	fLiAddSeparator("iNavL1M7", "&nbsp;|&nbsp;");
	fLiAddSeparator("iNavL1M8", "&nbsp;|&nbsp;");
	fLiAddSeparator("iNavL3", "&nbsp;|&nbsp;");
	fLiAddSeparator("iNavMiscHead", "&nbsp;|&nbsp;");
	fLiAddSeparator("iNavCookie", "&nbsp;&gt;&gt;&nbsp;");
	fLiAddSeparator("iNavMiscFooter", "&nbsp;|&nbsp;");
	fLiAddSeparator("iHighContrast", ",&nbsp;");
}
function fLiAddSeparator(i, ch, sp){ if(!W3CDOM) return;
	var e = document.getElementById(i); if(e==null) return;
	e = e.getElementsByTagName("LI"); if(e==null) return;
	fLiAddSeparatorDo(e, ch, sp);
}
function fLiAddSeparatorDo(arr, ch, sp){
	var e;
	for(var i=0; i<arr.length-1; i++){ 
		e = arr[i];
		if(sp && e.parentNode.parentNode.nodeName!="LI") continue;
		if(e.className.indexOf("last")==-1) e.innerHTML += ch;
	}
}

/* Condition container */

function getElementsByCondition(condition,container,arg){	container = container || document; var all = container.all || container.getElementsByTagName('*'); var arr = new Array(); var e; for(var k=0; k<all.length; k++){ e = all[k]; if(condition(e,k,arg)) arr[arr.length] = e; } return arr; }
function fApplyStyle(id, style, prop){ var e = (typeof(id)=="string")?document.getElementById(id):id; if(e==null) return; if(e.style) e = e.style; e[style] = prop; }
function fAddEvent(obj, evType, fn){ if(obj.addEventListener){ obj.addEventListener(evType, fn, false); return true; } else if(obj.attachEvent){ var r = obj.attachEvent("on"+evType, fn); return r; } else return false; }
if(!Array.prototype.pop) {
	function array_pop(){ lastElement = this[this.length-1]; this.length = Math.max(this.length-1,0); return lastElement;	}
	Array.prototype.pop = array_pop;
}
