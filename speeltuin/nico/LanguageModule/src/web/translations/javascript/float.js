function FloatDiv() {
	var startX = 3,	startY = 3;
	var ns = (navigator.appName.indexOf("Netscape") != -1);
	var d = document;
	
	function ml(id)	{
		var el = d.getElementById ? d.getElementById(id) : d.all ? d.all[id] : d.layers[id];
		if(d.layers)
			el.style=el;
		el.sP=function(x,y) {
			this.style.left=x;
			this.style.top=y;
		};
		el.x = startX;
		el.y = startY;
		return el;
	}

	window.stayLeft=function() {
		var pX = ns ? pageXOffset : document.body.scrollLeft;
		ftlObj.x += (pX + startX - ftlObj.x)/8;
		ftlObj.sP(ftlObj.x, ftlObj.y);
		setTimeout("stayLeft()", 10);
	}
	
	ftlObj = ml("divStayLeft");

	stayLeft();
}
FloatDiv();