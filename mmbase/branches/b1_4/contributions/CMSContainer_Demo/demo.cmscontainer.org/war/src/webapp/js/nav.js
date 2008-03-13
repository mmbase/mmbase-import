addLoadEvent(zetNav)

function zetNav(){
	document.body.onclick = function(){
		toonNav(this);
	
	}
	for(i=1;i<6;i++){
		navparent = document.getElementById('nav0'+i);
		if(navparent == null){
			navparent = document.getElementById('navrecprod0'+i);
		}
		if (navparent == null){
			navparent = document.getElementById('navtext0'+i);
		}	
		if (navparent != null && navparent.hasChildNodes()) {
			nav1=navparent.childNodes;		
			for(j=0;j<nav1.length;j++){
				if(nav1[j].nodeType == 1 && nav1[j].tagName == 'A'){
					nav1[j].onclick = function(e){
						toonNav(this);
						if (!e) var e = window.event;
						e.cancelBubble = true;
						if (e.stopPropagation) e.stopPropagation();
						return false;
					}
					nav1[j].onmouseover = function(){
						toonSub(this);
					}
				}
			}
	 	}
	}
}

aan = 0;
function toonNav(dit){
	if(aan == 0){
		if(dit.parentNode.tagName != 'LI') return;
		aan = 1;
	}
	else if(aan == 1){
		aan = 0;
	}
	toonSub(dit);
}

function toonSub(dit){
	denav = document.getElementById('nav');
	if(denav == null){
		denav = document.getElementById('navrecprod');
	}
	if (denav == null){
		denav = document.getElementById('navtext');
	}	
	deas = denav.getElementsByTagName('A');
	for(i=0;i<deas.length;i++){
		deas[i].className = '';
	}
	for(j=1;j<6;j++){
		if (document.getElementById('subnav0'+j) != null) {
			document.getElementById('subnav0'+j).style.display = 'none';
		}
	}
	if(aan==1){
		dit.className = 'active';
		parentstring = dit.parentNode.id;
		beginIndex = parentstring.lastIndexOf("0");
		endIndex = parentstring.length;
		lastdigits = parentstring.substring(beginIndex, endIndex);		
		if (document.getElementById('subnav'+lastdigits) != null) {		
			document.getElementById('subnav'+lastdigits).style.display = 'block';
		}		
	}
}