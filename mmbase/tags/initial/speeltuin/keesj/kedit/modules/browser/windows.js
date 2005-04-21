var currentDiv = null;
var move = false;;

var eventStartX =0;
var eventStartY =0;
var currentDivStartX =0;
var currentDivStartY =0;

var dropTarget = null;
/**
 *
 **/
function mouseDown(e) {
  if (currentDiv != null){
    move = true;
    eventStartX = getEventPosX(e);
    eventStartY = getEventPosY(e);
    var element = document.getElementById("monitor");
    element.innerHTML ="start move window ";
    if (document.body.style){
      document.body.style.cursor = "move";
        if (currentDiv.style.position != "absolute"){
          element.innerHTML ="start move window  and set pos abs";
          currentDiv.style.position = "absolute";
          setObjectPos(currentDiv,currentDivStartX,currentDivStartY);
        }
      }
    return false;
    }
  return true;
}


function getObjectPosX2(obj) { 
    var curleft = 0;
    if (obj.offsetParent) {
      if (obj.style.position == 'absolute'){
        return obj.offsetLeft;
    } else {
      while (obj.offsetParent) { 
        curleft += obj.offsetLeft; 
        obj = obj.offsetParent; 
      }
    }
  } else if (obj.x) {
    curleft += obj.x;
  }
  return curleft;
}

function getObjectPosX(obj) { 
  if (obj.offsetParent) { 
    return obj.offsetLeft ;
  }
  return obj.x; 
}

function getObjectPosY(obj) { 
  var curtop = 0; 
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



function mouseUp(e) {
    move = false;
      var element = document.getElementById("monitor");
      if (currentDiv != null && dropTarget != null){
          dropTarget.innerHTML = "Drop here";
          //dropTarget.value = currentDiv.innerHTML;
      }
      if (document.body.style){
          document.body.style.cursor = "default";
      }
    currentDiv = null;
     element.innerHTML ="stop move window ";

}
function mouseMove(e) {
      if (currentDiv != null && move){
            var mouseX = getEventPosX(e);
            var mouseY = getEventPosY(e);

           var divx =  mouseX - eventStartX;
           var divy =  mouseY - eventStartY;

            var xxx = document.getElementById("monitor2");
            xxx.innerHTML ="abs("+currentDivStartX +","+ currentDivStartY +")div(x,y)(" + divx + "," + divy + ")";;

            setObjectPos(currentDiv,currentDivStartX + divx,currentDivStartY + divy);
            /**
            if (currentDiv.pixelLeft){
               currentDiv.pixelLeft =   currentDivStartX + divx;
               currentDiv.pixelTop = currentDivStartY + divy;
            } else {
             currentDiv.style.left =   currentDivStartX + divx;
             currentDiv.style.top =   currentDivStartY + divy;
            }
            **/
            return true;
      }
      return false;
}
if (document.captureEvents){
  document.captureEvents(Event.MOUSEDOWN | Event.MOUSEMOVE | Event.MOUSEUP);
}
  document.onmousedown = mouseDown;
  document.onmouseup = mouseUp;
  document.onmousemove = mouseMove;

function select(e){
    if (move == false){
    currentDiv = e;
    currentDivStartX = getObjectPosX2(currentDiv); 
    currentDivStartY = getObjectPosY(currentDiv); 
    var element = document.getElementById("monitor");
    element.innerHTML ="selected"  + e +  " " + currentDivStartX + " " + currentDivStartY;
    }
}

function unselect(){
      /**
      if (move){
           move= false;
              var element = document.getElementById("monitor");
              element.innerHTML ="unselected";
              currentDiv = null;
      }
      **/
}
function selectdrop(e){
    unselectdrop();
    dropTarget = e;
    e.style.background ="green";
}

function unselectdrop(){
    if (dropTarget != null){
            dropTarget.style.background ="grey";
            dropTarget = null;
    }
}

