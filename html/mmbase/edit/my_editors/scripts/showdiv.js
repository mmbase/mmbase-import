/* Shows a div (more or less) at the current cursor position like a tooltip 
 * and gives it the classname 'shown' to be used in css
 * @param id    The id of an html element
 * @param event Window event, f.e. mouseover, onclick etc.
 */
function showBox(id, event) {
    if (!document.getElementById) return;
    el = document.getElementById(id);
    
    // event = window.event
    var myx = 0;
    var myy = 0;
    var cursor = getPosition(event);
    // not exactly at cursor position
    myx = cursor.x + 20;
    myy = cursor.y + 8;
    
    var parentObj = document.body;
    var parentDoc = parentObj.ownerDocument || parentObj.document;
    var showId = "show_" + id;
    if (document.getElementById(showId)) {
        toggle(showId);
    } else {
        // NOTE: two steps to avoid "flashing" in gecko
        var emptyDiv = parentDoc.createElement("div");
        var tipObj = parentObj.appendChild(emptyDiv);
        tipObj.id = showId;
        tipObj.innerHTML = el.innerHTML;
        
        tipObj.className = "shown";
        tipObj.style.display = "";
        tipObj.style.visibility = "visible";
        tipObj.style.position = "absolute";
        tipObj.style.left = myx + "px";
        tipObj.style.top = myy + "px";    
    }
}

/* Gets the cursor position based on an event */
function getPosition(e) {
    e = e || window.event;
    var cursor = {x:0, y:0};
    if (e.pageX || e.pageY) {
        cursor.x = e.pageX;
        cursor.y = e.pageY;
    } else {    // MSIE
        var de = document.documentElement;
        var b = document.body;
        cursor.x = e.clientX + (de.scrollLeft || b.scrollLeft) - (de.clientLeft || 0);
        cursor.y = e.clientY + (de.scrollTop || b.scrollTop) - (de.clientTop || 0);
    }
    return cursor;
}

/*
 * Toggles the visibility of an html element 
 * @param targetId  The id of an html element
 */
function toggle(targetId){
  if (document.getElementById) {
        target = document.getElementById(targetId);
        if (target.style.display == "none"){
            target.style.display = "";
        } else {
            target.style.display = "none";
        }
    }
}

/* Toggle two Id's at once, presumably to hide one and show another 
 * @param hidId     The id of an html element to hide
 * @param showId    The id of an html element to show
 */
function toggleTwo(hideId, showId) {
  toggle(hideId);
  toggle(showId);
}
