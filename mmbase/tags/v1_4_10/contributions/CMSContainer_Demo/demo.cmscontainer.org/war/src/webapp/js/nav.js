addLoadEvent(zetNav)

function zetNav(){
	
	document.body.onclick = function(){
		hideSubNavs();
	}
	
	var div = document.getElementById("top_img");
	div.onmouseover = hideSubNavs;
	var div = document.getElementById("background");
	div.onmouseover = hideSubNavs;

	for(i=1;i<6;i++){
		navparent = document.getElementById('nav0'+i);
		if(navparent == null){
			navparent = document.getElementById('navrecprod0'+i);
		}
		if (navparent == null){
			navparent = document.getElementById('navtext0'+i);
		}	
		if (navparent != null && navparent.hasChildNodes()) {
			navparent.onmouseover = function(){
				onMenuOver(this);
			}
			nav1=navparent.childNodes;		
			for(j=0;j<nav1.length;j++){
				if(nav1[j].nodeType == 1 && nav1[j].tagName == 'A'){
					nav1[j].onmouseover = function(){
						onSubMenuOver(this);
					}
				}
			}
	 	}
	}
}

function onMenuOver(dit){
	parentstring = dit.id;
	toonSub(dit, parentstring);
}

function onSubMenuOver(dit){
	dit.className = 'active';
}

function toonSub(dit, parentstring){
	hideSubNavs();
	deactivateAnchors();
	
	beginIndex = parentstring.lastIndexOf("0");
	endIndex = parentstring.length;
	lastdigits = parentstring.substring(beginIndex, endIndex);		
	if (document.getElementById('subnav'+lastdigits) != null) {		
		document.getElementById('subnav'+lastdigits).style.display = 'block';
	}
}

function getDeNav(){
	denav = document.getElementById('nav');
	if(denav == null){
		denav = document.getElementById('navrecprod');
	}
	if (denav == null){
		denav = document.getElementById('navtext');
	}
	return denav;
}

function hideSubNavs(){
	for(j=1;j<6;j++){
		if (document.getElementById('subnav0'+j) != null) {
			document.getElementById('subnav0'+j).style.display = 'none';
		}
	}
}

function deactivateAnchors(){
	denav = getDeNav();
	deas = denav.getElementsByTagName('A');
	for(i=0;i<deas.length;i++){
		deas[i].className = '';
	}
}
