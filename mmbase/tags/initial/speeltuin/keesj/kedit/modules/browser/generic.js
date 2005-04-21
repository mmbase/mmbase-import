function getObjectPosX(obj) { 
	if (obj.offsetParent) { 
		  return obj.offsetLeft ;
	}
	return obj.x; 
}

function getObjectPosY(obj) { 
	if (obj.offsetParent) { 
			return obj.offsetTop ;
	} 
   return obj.x; 
}

function getEventPosX(event){

	if (event == null) event = document.parentWindow.event;
	if (event.clientX) return event.clientX  + document.body.scrollLeft;
	if (event.pageX) return  event.pageX;
	if (event.x) return event.x;
    return event.clientX  + document.body.scrollLeft;
}

function getEventPosY(event){
	if (event == null) event = document.parentWindow.event;
	if (event.y) return event.y;
  if (event.pageY) return  event.pageY;
  return event.clientY  + document.body.scrollTop;
}

function setObjectPos(element,posx,posy){
					  if (element.pixelLeft){
					     element.pixelLeft =   posx;
						  element.pixelTop = posy;
					  } else {
					  element.style.left =  posx;
					   element.style.top =   posy;
					  }
}
