addLoadEvent(zetNav)

function zetNav(){

	var div = document.getElementById("contentPart");
	div.onmouseover = hideMenu;
	var div = document.getElementById("headerImage");
	div.onmouseover = hideMenu;
	
	for(i=1;i<6;i++){
		navparent = document.getElementById('nav0'+i);
		if (navparent != null && navparent.hasChildNodes()) {
			nav1=navparent.childNodes;
			for(j=0;j<nav1.length;j++){
				if(nav1[j].nodeType == 1 && nav1[j].tagName == 'A'){
					nav1[j].onmouseover = function(){
						toonSub(this);
					}
				}
			}
	 	}
	}
}

function hideMenu() {
	denav = document.getElementById('nav');

	deas = denav.getElementsByTagName('A');
	for(i=0;i<deas.length;i++){
		deas[i].className = '';
	}

	for(j=1;j<6;j++){
		if (document.getElementById('subnav0'+j) != null) {
			document.getElementById('subnav0'+j).style.display = 'none';
		}
	}
}

function toonSub(dit){
	denav = document.getElementById('nav');
	
	hideMenu();
	
	dit.className = 'active';
	parentstring = dit.parentNode.id;
	beginIndex = parentstring.lastIndexOf("0");
	endIndex = parentstring.length;
	lastdigits = parentstring.substring(beginIndex, endIndex);		
	if (document.getElementById('subnav'+lastdigits) != null) {		
		document.getElementById('subnav'+lastdigits).style.display = 'block';
	}		
}