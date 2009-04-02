var dom = document.getElementById ? 1 : 0; // DOM1 supported?
function getElement(e){
	if (dom)
		return document.getElementById(e);
	if (document.all)
		return document.all[e]; return false;
}

// tooltip  ----------------------------------------------------------------------------
var tooltip; // stores reference to tooltip layer

// utility functions
function getH(e){return e.offsetHeight;} // get actual height of element
function getW(e){return e.offsetWidth;} // get actual height of element
function setX(e,x){e.style.left=x + "px";} // set x-coordinate of element
function setY(e,y){e.style.top=y + "px";} // set y-coordinate of element

// hide tooltip layer
function hideTooltip(){
	if (tooltip){
		tooltip.style.display="none";
		tooltip = null;
	}
}

// show tooltip layer
function showTooltip(object,tipContent){
	tooltip = getElement(object);
	if (tooltip){
		var evt = window.event || arguments.callee.caller.arguments[0];
		var target = window.event ? evt.srcElement : evt.target;
		target.onmouseout = hideTooltip;
		if (tipContent) tooltip.innerHTML=unescape(tipContent);
		tooltip.style.display="block";
		positionTooltip(evt);
		target.onmousemove = positionTooltip;
	}
}

// postitioning of tooltip above mouse pointer
function positionTooltip(evt){
	if (tooltip){
		if (!evt) var evt = window.event;
		getMousePosition(evt);

		if (mouseX - 15 + getW(tooltip) < document.body.offsetWidth) {
		   setX(tooltip,mouseX - 15);
		}
		else {
		   setX(tooltip, max(0, document.body.offsetWidth - getW(tooltip)));
		}
		if (mouseY - getH(tooltip) - 10 > 0) {
		   setY(tooltip,mouseY - getH(tooltip) - 10);
	   }
	   else {
	      setY(tooltip,mouseY + 10);
	   }
	}
}

// Refresh and store current position of the mouse pointer
var mouseX = 0; // stores current x-coordinate of mouse pointer
var mouseY = 0; // stores current y-coordinate of mouse pointer
function getMousePosition(evt){
	var scrollX = window.innerHeight ? 0 : ((document.documentElement.scrollLeft > document.body.scrollLeft) ? document.documentElement.scrollLeft : document.body.scrollLeft);
	var scrollY = window.innerHeight ? 0 : ((document.documentElement.scrollTop > document.body.scrollTop) ? document.documentElement.scrollTop : document.body.scrollTop);
	if (evt.pageX || evt.pageY){ // DOM event model supported
		mouseX = evt.pageX;
		mouseY = evt.pageY;
	} else { // IE event model supported
		mouseX = evt.clientX + scrollX;
		mouseY = evt.clientY + scrollY;
	}
}